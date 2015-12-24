

/**
 * @author Prateek

 */

import java.awt.image.BufferedImage
import java.security.interfaces.RSAPublicKey
import java.security.spec.{RSAPublicKeySpec, X509EncodedKeySpec}
import javax.imageio.ImageIO

import java.security._
import javax.crypto._

import org.apache.commons.codec.binary.Base64
import play.api.libs.json._
import org.json4s._
import scala.util.{Random, Try, Sorting}
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.can.server.Stats
import spray.util._
import spray.http._
import HttpMethods._
import MediaTypes._
import spray.can.Http.RegisterChunkHandler
import akka.event.Logging
import akka.io.IO
import spray.can.Http
import spray.routing.HttpService
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport
import akka.remote._
import akka.pattern.ask
import akka.util.Timeout.durationToTimeout
import scala.concurrent.ExecutionContext.Implicits.global
import java.net.InetAddress
import akka.pattern.pipe
import scala.concurrent.duration.DurationInt
import com.typesafe.config.ConfigFactory
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future
import spray.http.HttpEntity
import spray.http.HttpEntity.apply
import spray.can.Http
import spray.http.ContentTypes
import spray.http.HttpMethods.GET
import spray.http.HttpMethods.POST
import spray.http.HttpRequest
import spray.http.HttpResponse
import java.io._

//import spray.json.pimpAny
//import spray.json.pimpString

import scala.collection.mutable.{ArrayBuffer, Map}
import scala.util.control.Breaks._
import akka.actor._
import collection.mutable
import java.security.MessageDigest
import akka.routing.BalancingPool
import akka.routing._
import akka.util._
import com.typesafe.config.ConfigFactory
import java.io.File
import akka.pattern.Patterns._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import spray.http._
import MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import scala.collection.immutable.ListMap
import collection.immutable.SortedMap
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.Serialization.{read, write, writePretty}

sealed trait fb

case class Like(Item: String, LikeNameList: ArrayBuffer[String]) extends fb

case class AddLike(Item: String, ClientId: String) extends fb
case class GetPagePosts(Pagename:String)extends fb
case class GetProfile(ClientID: String) extends fb

case class GetProfilePosts(ClientID: String) extends fb

case class Profile(ClientID: String, Name: String, Birthday: String, Sex: String) extends fb

case class Register(ClientId: String) extends fb

case class GetFriends(ClientId: String) extends fb

case class intCheck(x: Int) extends fb

case class FriendsList(ClientId: String, FriendsArray: ArrayBuffer[String])

case class GetPages(ClientId: String) extends fb

case class Photo(name: String, photo: String, size: Int) extends fb

case class Posts(postedBy: String, post: String, secretKey:String) extends fb

//sharique
case class AddFriend(usreID: String, requestedFriend: String) extends fb

case class GetAesKey(userID: String, friendID: String) extends fb

case class AddKeys(usreID: String, publicKey: String, secretKey: String) extends fb

case class Signature(userID: Int, nonce: String) extends fb

case class SayHello(userID: Int, msg: String) extends fb

case class AddAlbum(userID: String, albumName: String) extends fb

//we just need name to put an entry
case class AddPhoto(userID: String, albumName: String, photoIndex: String) extends fb

// inside which album?
case class GetAlbum(userID: String, albumName: String) extends fb

// should return photo array
case class GetPhoto(userID: String, albumName: String, photoIndex: String) extends fb

case class GetFriendList(userID: String) extends fb

case class RemoveFriend(userID: String, hisID: String) extends fb

case class PostOnProfile(userID: String, profileUserID: String, post: String) extends fb

case class CreatePage(userID: String, pageName: String) extends fb

case class LikePage(userID: String, pageName: String) extends fb

case class UnLikePage(userID: String, pageName: String) extends fb

case class PostOnPage(userID: String, pageName: String, post: String) extends fb

case class GetPostsInPage(userID: String, pageName: String) extends fb

//need index in array

case class ProfileClass(
                         ID: String,
                         Name: String,
                         friends: ArrayBuffer[String],
                         pages: ArrayBuffer[String]

                         ) extends fb

case class PostClass(
                      PostId: String,
                      Post: String,
                      Post_Date: String,
                      PostedBy: String,
                      likedBy: mutable.Map[String, ArrayBuffer[String]]

                      ) extends fb


case class Page(
                 PageName: String,
                 PageId: String,
                 createdBy: String,
                 likedBy: mutable.Map[String, ArrayBuffer[String]]) extends fb

object FacebookSimulator extends App {

  val configFile = getClass.getClassLoader.getResource("local_application.conf").getFile
  //parse the config
  val config = ConfigFactory.parseFile(new File(configFile))
  //create an actor system with that config
  val system = ActorSystem("fbServerEnv", config)
  //create a remote actor from actorSystem

  println("remote is ready, enter n ")

  val n = readInt()

  var numOfUsers: Int = n
  var users: ArrayBuffer[RegularUser] = new ArrayBuffer[RegularUser](n)
  var allPagesMap: scala.collection.mutable.HashMap[String, RegularPage] = scala.collection.mutable.HashMap[String, RegularPage]()
  var remote = new ArrayBuffer[ActorRef]()
  for (i <- 0 to n - 1) {
    remote += system.actorOf(Props(new FbServer(numOfUsers, users, allPagesMap)), name = "fbServer" + i)
  }

  val st: Long = System.currentTimeMillis()
  initialize(n)
  val en: Long = System.currentTimeMillis()
  var dif: Double = (en - st) / 1000
  println("Time taken to initialize : " + dif + " sec")

  def initialize(n: Int): Unit = {

    //var byteArray:Array[Byte] = Array[Byte]()
    // var in = None: Option[FileInputStream]
    // var out = None: Option[FileOutputStream]
    var image: BufferedImage = ImageIO.read(new File("image.png"))
    var baos: ByteArrayOutputStream = new ByteArrayOutputStream()
    ImageIO.write(image, "png", baos)
    var byteArray: Array[Byte] = baos.toByteArray()

    /*val source = scala.io.Source.fromFile("image.png")
    val byteArray = source.map(_.toByte).toArray
    source.close()*/

    for (i <- 0 to n - 1) {
      users += new RegularUser(i, "user" + i)
    }
    for (i <- 0 to n - 1) {
      //adding 7 posts in profile of every user
      //for(j <- 0 to 2)
      //users(i).profile.AddPostinProfile(new RegularPost("This is my " + j + "post", users(i).name))
      //creating 2 pages by every user
      for (j <- 0 to 2) {
        val pagename = users(i).name + "page" + j
        val page = new RegularPage(pagename)
        users(i).addPage(page)
        allPagesMap += (pagename -> page)
      }
      //adding 2 albums for every user
      for (j <- 0 to 1) {
        val albumname = users(i).name + "album" + j
        val album = new RegularAlbum(albumname)
        users(i).addAlbum(album)
        //println("Album initialized " + albumname)

        //Now add photo in this album
        //users(i).albums(j).AddPictureInAlbum(new RegularPicture(byteArray, j.toString))

      }

    }

    for (i <- 0 to n - 1) {
      //fill friendlist
      for (j <- 0 to 9) {
        users(i).friendList += (users(Random.nextInt(n)))
      }
    }

  }
}

class FbServer(numOfUsers: Int, users: ArrayBuffer[RegularUser], allPagesMap: scala.collection.mutable.HashMap[String, RegularPage]) extends Actor {


  var likeList: Like = new Like("Prateek", ArrayBuffer("ddff", "adfadf", "afdfferg", "kjkui"))


  def receive = {
    case _: Http.Connected =>
      sender ! Http.Register(self)

    //    case GetPagePosts(Pagename)={
    //
    //      }
    case GetProfilePosts(clientID) => {

      if (users.size > clientID.toInt)
        sender ! users(clientID.toInt).profile.getPostinProfile().length + "-" + ConvertToJson.toJsonObjectPosts(users(clientID.toInt).profile.getPostinProfile())
      else
        sender ! "user profile does not exist"

      println("sending profile" + ConvertToJson.toJsonObjectPosts(users(clientID.toInt).profile.getPostinProfile()))


    }
    case GetProfile(clientID) => {
      if (users.size > clientID.toInt)
        sender ! ConvertToJson.toJsonObject(users(clientID.toInt))
      else
        sender ! "user profile does not exist"

      println("sending profile")
      /*println(clientID)
      println("users.size > clientID.toInt " + users.size + " > " + clientID.toInt)*/
    }
    case GetFriends(userID) => {
      //println(clientID)
      var str: ArrayBuffer[String] = ArrayBuffer[String]()
      for (i <- 0 to users(userID.toInt).friendList.size - 1) {
        str += users(userID.toInt).friendList(i).name
      }
      //return ArrayList of String to client
      sender ! ConvertToJson.toJsonObject(str)
    }

    //sharique need to update these queries
    /*case numUsers:Int => {
      //println("NumUsers = " + numUsers)
      numOfUsers = numUsers
      val st:Long = System.currentTimeMillis()
      initialize(numOfUsers)
      val en:Long = System.currentTimeMillis()
      var dif:Double = (en - st)/1000
      println("Time taken to initialize : " + dif + " sec")
    }*/

    case Signature(userID: Int, nonce: String) => {
      println("signedNounce = " + nonce)
      var bytes: Array[Byte] = Base64.decodeBase64(nonce)
      var decryptedNonce: Array[Byte] = decryptRSA(bytes, users(userID).publicKey)
      println("decrypt = " + decryptedNonce.toString + " encrypt = " + users(userID).tempNonce.toString)

      var flag = true
      for (i <- 0 to decryptedNonce.size - 1) {
        if (decryptedNonce(i) != users(userID).tempNonce(i)) {
          flag = false;
        }
      }

      if (flag) {
        println("Login by " + users(userID).name + " succesful")
        sender ! "successful"
      }
      else {
        println("Login by " + users(userID).name + " failed")
        sender ! "failed"
      }
    }
    case SayHello(userID: Int, msg: String) => {
      var randomNo: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
      var l: Long = randomNo.nextLong()
      var bytes: Array[Byte] = l.toString().getBytes()
      println("bytes size = " + bytes.size)
      users(userID).tempNonce = new Array[Byte](bytes.size)
      for (i <- 0 to bytes.size - 1) {
        users(userID).tempNonce(i) = bytes(i)
      }


      val str: String = Base64.encodeBase64String(bytes)
      println("Sent random No for user " + userID + " = " + str)
      sender ! str
    }
    case AddKeys(userID, publicKey, secretKey) => {
      println("pubKey key : for " + userID + " = " + publicKey + " secretKey = " + secretKey)
      var publicBytes: Array[Byte] = Base64.decodeBase64(publicKey)
      users(userID.toInt).secretKey = secretKey
      // println("publicKey: " + publicBytes)
      var pubKey: PublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicBytes))
      users(userID.toInt).publicKey = pubKey

      sender ! "keys added for user " + userID + " at server"
    }
    case GetAesKey(userID: String, friendID:String) => {
      if(userID.equals(friendID) || users(friendID.toInt).friendList.contains(users(userID.toInt)))
        sender ! users(friendID.toInt).secretKey
      else
        sender ! " You are not a friend, So can't accces Aes key"
    }
    case AddFriend(usreID, requestedfreindID) => {
      if (users.contains(users(usreID.toInt))) {
        println("friend added already")
        sender ! "friend added already"
      }
      else {
        sender ! "friends added"
        users(requestedfreindID.toInt).addFriend(users(usreID.toInt))
        users(usreID.toInt).addFriend(users(requestedfreindID.toInt))
        println("friends added")

      }

    }
    case AddAlbum(userID, albumName) => {
      //we just need name to put an entry
      if (users(userID.toInt).albumNameIDmap.contains(albumName)) {
        sender ! "album name already exist"
        println("album name already exist")
      }
      else {
        sender ! "added album"
        users(userID.toInt).addAlbum(new RegularAlbum(albumName))
        println(users(userID.toInt))

      }

    }
    case AddPhoto(userID, albumName, photo) => {
      // inside which album?

      var photo1 = JstoCaseObject.toClassObjectPhoto(photo)
      if (users(userID.toInt).albumNameIDmap.contains(albumName)) {
        sender ! "photo added"
        users(userID.toInt).albums(users(userID.toInt).albumNameIDmap(albumName)).AddPictureInAlbum(photo1)

      }
      else
        sender ! "photo cannot be added because album is not created"
      //print("userID" + userID + "albumName" + albumName)
    }
    case GetAlbum(userID, albumName) => {
      // should return photo array
      if (users(userID.toInt).albumNameIDmap.contains(albumName)) {
        val album = users(userID.toInt).albums(users(userID.toInt).albumNameIDmap(albumName))
        println("album sent")
        sender ! "Total no of photos in " + albumName + " are " + album.album.size
      }
      else {
        sender ! "Album " + albumName + " doesn't exist"
      }

    }
    case GetPhoto(userID, albumName, photoIndex) => {
      if (users(userID.toInt).albumNameIDmap.contains(albumName)) {
        if (photoIndex.toInt < users(userID.toInt).albums(users(userID.toInt).albumNameIDmap(albumName)).GetSize()) {
          val pic: RegularPicture = users(userID.toInt).albums(users(userID.toInt).albumNameIDmap(albumName)).GetPictureInAlbum(photoIndex.toInt)
          sender ! ConvertToJson.toJsonObject(pic)
        }
        else {
          sender ! "No photo"
        }

      }
      else {
        sender ! "No photo"
      }


    }
    /*case GetFriendList(userID) => {
      var str: ArrayBuffer[String] = ArrayBuffer[String]()
      for (i <- 0 to users(userID.toInt).friendList.size - 1) {
        str += users(userID.toInt).friendList(i).name
      }
      //return ArrayList of String to client
      sender ! str
    }*/
    case RemoveFriend(userID, hisID) => {
      sender ! ("friend removed->" + hisID)
      users(hisID.toInt).deleteFriend(users(userID.toInt))
      users(userID.toInt).deleteFriend(users(hisID.toInt))
      /*var str: ArrayBuffer[String] = ArrayBuffer[String]()
      for (i <- 0 to users(userID.toInt).friendList.size - 1) {
        str += users(userID.toInt).friendList(i).name
      }
      var tempFriendlist = ConvertToJson.toJsonObject(str)*/
    }
    case PostOnProfile(userID, profileUserId, post) => {
      var tempPost = JstoCaseObject.toClassObjectPost(post)
      if (!users(profileUserId.toInt).friendList.isEmpty) {
        if (users(profileUserId.toInt).friendList.contains(users(userID.toInt))) {
          sender ! ("added Post on profile->" + tempPost)
          users(profileUserId.toInt).profile.AddPostinProfile(tempPost)
          println("added Post on profile->" + tempPost)
        }
        else {
          sender ! "can't post you are not friend"
        }
      }
      else
        sender ! " Post on profile cannot be added"
      println(" Post on profile cannot be added")


    }
    case PostOnPage(userID, pageName, post) => {
      //need index in array
      var tempPost = JstoCaseObject.toClassObjectPost(post)
      if (allPagesMap.contains(pageName)) {
        //if (allPagesMap(pageName).LikeListofUsers.contains(userID)) 
	{
          sender ! "posted on page"
          allPagesMap(pageName).AddPostinPage(tempPost)
        }
        /*else {
          sender ! "can't post as you have not liked the page"
        }*/
      }
      else {
        sender ! "pagename " + pageName + "doesn't exist"
      }
    }

    case CreatePage(userID, pageName) => {
      if (allPagesMap.contains(pageName)) {
        sender ! "pagename already present"
      }
      else {
        sender ! pageName + "page created"
        val page: RegularPage = new RegularPage(pageName)
        users(userID.toInt).addPage(page)
        allPagesMap += (pageName -> page)
      }
      println(allPagesMap)
    }

    case LikePage(userID, pageName) => {
      if (allPagesMap.contains(pageName)) {
        allPagesMap(pageName).Addlike(userID)
        sender ! "Liked page with name : " + pageName
      }
      else {
        sender ! "pagename : " + pageName + "doesn't exist"
      }
    }

    case UnLikePage(userID, pageName) => {
      if (allPagesMap.contains(pageName)) {
        if (allPagesMap(pageName).LikeListofUsers.contains(userID)) {
          sender ! "Unlike successful"
          allPagesMap(pageName).LikeListofUsers.remove(userID)
        }
        else {
          sender ! " Cannot unlike as you have not liked the page"
        }
      }
      else {
        sender ! "page " + pageName + " doesn't exist"
      }
    }

    case GetPostsInPage(userID, pageName) => {
      if (allPagesMap.contains(pageName) && allPagesMap(pageName).postsInPage.size > 0 ) {
        //if (allPagesMap(pageName).LikeListofUsers.contains(userID)) 
	{
          //send reply array of 5 posts
          var posts: ArrayBuffer[RegularPost] = ArrayBuffer[RegularPost]()
          var m: Int = math.min(allPagesMap(pageName).postsInPage.size, 5)

          for (i <- 0 to m - 1) {
            posts += allPagesMap(pageName).postsInPage(allPagesMap(pageName).postsInPage.size - i - 1)
          }

          sender ! m+"-"+ConvertToJson.toJsonObjectPostList(posts)

        }
        /*else {
          sender ! "can't read post as you have not liked the page"
        }*/
      }
      else {
        sender ! "page doesn't exist"
      }
    }
  }

  def encryptRSA(inpBytes: Array[Byte], key: Key): Array[Byte] = {
    var cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return cipher.doFinal(inpBytes)
  }

  def decryptRSA(inpBytes: Array[Byte], key: Key): Array[Byte] = {
    var cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")
    cipher.init(Cipher.DECRYPT_MODE, key)
    return cipher.doFinal(inpBytes)
  }

}

object ConvertToJson {

  //private implicit val formats = Serialization.formats(NoTypeHints)
  implicit val formats = native.Serialization.formats(ShortTypeHints(List(classOf[Profile])))

  def toJsonObject(profile: Profile): String = writePretty(profile)

  def toJsonObject(Friends: FriendsList): String = writePretty(Friends)

  def toJsonObject(profileClass: ProfileClass): String = writePretty(profileClass)

  def toJsonObject(postClass: PostClass): String = writePretty(postClass)

  def toJsonObject(page: Page): String = writePretty(page)

  def toJsonObject(FriendsArray: ArrayBuffer[String]): String = writePretty(FriendsArray)

  def toJsonIntObject(IntArray: ArrayBuffer[Int]): String = writePretty(IntArray)

  def toJsonObject(post: Posts): String = writePretty(post)

  def toJsonObjectPosts(ProfilePosts: ArrayBuffer[RegularPost]): String = {
    var jsonPostsString: ArrayBuffer[Posts] = ArrayBuffer[Posts]()
    if (!ProfilePosts.isEmpty) {
      for (i <- 0 to ProfilePosts.size - 1) {
        jsonPostsString += (Posts(ProfilePosts(i).postedBy, ProfilePosts(i).post, ProfilePosts(i).secretKey))

        println(Posts)
      }
      println(jsonPostsString)
      writePretty(jsonPostsString)
    }
    else
      writePretty(jsonPostsString)
  }

  def toJsonObject(pic: Photo): String = writePretty(pic)

  def toJsonObjectPostList(postsInpage: ArrayBuffer[RegularPost]): String = {
    var jString: String = ""
    var jsonPostsString:ArrayBuffer[Posts]=ArrayBuffer[Posts]()
    for (i <- 0 to postsInpage.size - 1)
      //jString += writePretty(new Posts(postsInpage(i).postedBy, postsInpage(i).post))
	jsonPostsString += (new Posts(postsInpage(i).postedBy, postsInpage(i).post,  postsInpage(i).secretKey))
    return writePretty(jsonPostsString)
  }

  def toJsonObject(prof: RegularUser): String = {
    var tempArray: ArrayBuffer[String] = ArrayBuffer[String]()
    if (!prof.friendList.isEmpty) {
      for (i <- 0 to prof.friendList.size - 1) {
        tempArray += (prof.friendList(i).name).toString()
      }
    }
    var tempPageArray: ArrayBuffer[String] = ArrayBuffer[String]()
    if (!prof.pages.isEmpty) {
      for (i <- 0 to prof.pages.size - 1) {
        tempPageArray += (prof.pages(i).name).toString()
      }
    }
    toJsonObject(ProfileClass(prof._id.toString, prof._name, tempArray, tempPageArray))

  }

  def toJsonObject(prof: RegularPicture): String = {
    val byteArray: Array[Byte] = prof.picture
    var base64str: String = Base64.encodeBase64String(byteArray)
    var ph: Photo = new Photo(prof.name, base64str, prof.picture.size)
    return writePretty(ph)
  }

  def toJsonObject(album: RegularAlbum): String = writePretty(album)
}

object JstoCaseObject {
  def toCaseObjectPage(pageJsonString: String): Page = {
    var json: JsValue = Json.parse(pageJsonString)

    var pageName = json.\("PageName").as[String]
    var pageId = json.\("PageId").as[String]
    var CreatedBy = json.\("createdBy").as[String]
    var LikedBy = (json.\("likedBy") \\ (pageName)).flatMap(_.as[ArrayBuffer[String]]).to[ArrayBuffer]
    val t = json.\("PageName").as[String]

    var deserializedPage = Page(pageName, pageId, CreatedBy, Map[String, ArrayBuffer[String]]() += (pageName -> LikedBy))
    deserializedPage
  }

  def toClassObjectPost(pageJsonString: String): RegularPost = {
    var json: JsValue = Json.parse(pageJsonString)
    var post = (json.\("post").as[String])
    var postedBy = (json.\("postedBy").as[String])
    var key = (json.\("secretKey").as[String])
    println("Post size: " + post.size)
    new RegularPost(post, postedBy, key)
  }

  def toClassObjectPhoto(pageJsonString: String): RegularPicture = {
    var json: JsValue = Json.parse(pageJsonString)
    var name = (json.\("name").as[String])
    var tempSize = (json.\("size")).toString.toInt
    var t2 = (json.\("photo")).as[String]

    val byteArray: Array[Byte] = Base64.decodeBase64(t2)

    /*var tempByteArray: ArrayBuffer[Byte] = ArrayBuffer[Byte]()
    for (i <- 0 to tempSize - 1)
      tempByteArray += t2(i).toString().toByte
    println(tempByteArray)*/
    //println((t2(1)))//.to[ArrayBuffer] )
    new RegularPicture(byteArray, name)
  }

  def returnString(x: String): String = {
    return x
  }

  def convertToBase64(fileName: String): String = {
    val source = scala.io.Source.fromFile(fileName)
    val byteArray = source.map(_.toByte).toArray
    source.close()
    return Base64.encodeBase64String(byteArray)
  }

}

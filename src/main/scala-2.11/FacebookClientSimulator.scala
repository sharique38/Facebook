

/**
 * @author Prateek

 */

import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.TimeUnit
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}
import play.api.libs.json._
import scala.concurrent.duration.Duration
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import java.security._
import javax.crypto._

import org.apache.commons.codec.binary.Base64
import scala.util.control.Breaks._
import scala.util.Sorting
import scala.collection.mutable.ArrayBuffer
import akka.actor._
import collection.mutable
import akka.routing.BalancingPool
import akka.routing._
import akka.util._
import com.typesafe.config.ConfigFactory
import java.io.{ByteArrayOutputStream, File}
import akka.pattern.Patterns._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import spray.http.{HttpRequest, HttpResponse}
import spray.client.pipelining.{Get, sendReceive}
import spray.client.pipelining.{Post, sendReceive}
import spray.client.pipelining.{Delete, sendReceive}
import spray.client.pipelining.{Put, sendReceive}
import spray.http._
import spray.json.DefaultJsonProtocol
import spray.httpx.encoding.{Gzip, Deflate}
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._
import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.util.Random
import scala.collection.immutable.ListMap
import collection.immutable.SortedMap

case class initializeClients(num: Int)

case class init(i: Int, num: Int, img: String)

object FacebookClientSimulator extends App {
  val configFile = getClass.getClassLoader.getResource("remote_application.conf").getFile
  //parse the config
  val config = ConfigFactory.parseFile(new File(configFile))
  // println(config)
  //create an actor system with that config
  //val system = ActorSystem("fbServerEnv",config)

  val clientSystem = ActorSystem("FbClientEnv", config)
  val master = clientSystem.actorOf(Props(new FbClientSimulatorMaster))
  println("enter no. of clients")
  var n = readInt()
  //var actor1 = clientSystem.actorOf(Props(new FbClientActor), name = "actor1")
  //var actor2= clientSystem.actorOf(Props(new FbClientActor), name = "actor2")


  /*val en:Long = System.currentTimeMillis()
  var dif:Double = (en - st)/1000.0
  println("Time taken to initialize : " + dif + " sec")*/
  master ! initializeClients(n)
}

class FbClientSimulatorMaster extends Actor {
  var inc: Int = 0
  var queries: Long = 0
  var startTime: Long = System.currentTimeMillis()
  var endTime: Long = System.currentTimeMillis()
  var GetProfileCounter = 0
  var keys: ArrayBuffer[KeyPair] = ArrayBuffer[KeyPair]()
  var aeskeys: ArrayBuffer[SecretKey] = ArrayBuffer[SecretKey]()
  val kpg: KeyPairGenerator = KeyPairGenerator.getInstance("RSA");
  kpg.initialize(1024);
  // 512 is the keysize.
  var kg: KeyGenerator = KeyGenerator.getInstance("AES");
  kg.init(128);

  var GetProfilePostsCounter = 0
  var AddFriendCounter = 0
  var GetFriendlistCounter = 0
  var PostOnProfileCounter = 0
  var PostOnPageCounter = 0
  var AddAlbumCounter = 0
  var AddPhotoCounter = 0
  var GetAlbumCounter = 0
  var UnFriendCounter = 0
  var GetPhotoCounter = 0
  var CreatePageCounter = 0
  var LikePageCounter = 0
  var UnLikePageCounter = 0


  var numberOfClients = 0
  var actor: ArrayBuffer[ActorRef] = new ArrayBuffer[ActorRef]()
  var clientSystem = ActorSystem("FbClientEnv")
  var image: String = Base64.encodeBase64String(readImageInBytes("image.png"))

  def readImageInBytes(filename: String): Array[Byte] = {
    var image: BufferedImage = ImageIO.read(new File("image.png"))
    var baos: ByteArrayOutputStream = new ByteArrayOutputStream()
    ImageIO.write(image, "png", baos)
    var byteArray: Array[Byte] = baos.toByteArray()
    return byteArray
  }

  def receive = {
    case "incrementGetProfileCounter" => {
      GetProfileCounter += 1
    }
    case "incrementAddFriendCounter" => {
      AddFriendCounter += 1
    }
    case "incrementGetProfilePostsCounter" => {
      GetProfilePostsCounter += 1
    }
    case "incrementGetFriendlistCounter" => {
      GetFriendlistCounter += 1
    }
    case "incrementPostOnProfileCounter" => {
      PostOnProfileCounter += 1
    }
    case "incrementPostOnPageCounter" => {
      PostOnPageCounter += 1
    }
    case "incrementAddAlbumCounter" => {
      AddAlbumCounter += 1
    }
    case "incrementAddPhotoCounter " => {
      AddPhotoCounter += 1
    }
    case "incrementGetAlbumCounter" => {
      GetAlbumCounter += 1
    }
    case "incrementUnFriendCounter" => {
      UnFriendCounter += 1
    }
    case "incrementGetPhotoCounter" => {
      GetPhotoCounter += 1
    }
    case "incrementCreatePageCounter" => {
      CreatePageCounter += 1

    }
    case "incrementLikePageCounter" => {

      LikePageCounter += 1
    }


    case "incrementUnLikePageCounter" => {

      UnLikePageCounter += 1
    }

    case "report" => {
      println("-------------------------------------------------------------")
      println("GetProfile Queries->" + GetProfileCounter)
      println("GetProfilePosts Queries->" + GetProfilePostsCounter)
      println("AddFriend Queries->" + AddFriendCounter)
      println("GetFriendlist Queries->" + GetFriendlistCounter)
      println("PostOnProfile Queries->" + PostOnProfileCounter)
      println("PostOnPage Queries->" + PostOnPageCounter)
      println("AddAlbum Queries->" + AddAlbumCounter)
      println("AddPicture Queries->" + AddPhotoCounter)
      println("GetAlbum Queries->" + GetAlbumCounter)
      println("UnFriend Queries->" + UnFriendCounter)
      println("GetPicture Queries->" + GetPhotoCounter)
      println("CreatePage Queries->" + CreatePageCounter)
      println("LikePage Queries->" + LikePageCounter)
      println("UnLikePage Queries->" + UnLikePageCounter)
      println("-------------------------------------------------------------")
      endTime = System.currentTimeMillis()
      if (endTime - startTime > 15000) {
        startTime = endTime
        var q: Long = GetProfileCounter + GetProfilePostsCounter + AddFriendCounter + GetFriendlistCounter + PostOnProfileCounter + PostOnPageCounter + UnLikePageCounter + LikePageCounter + CreatePageCounter + GetPhotoCounter + UnFriendCounter + GetAlbumCounter + AddPhotoCounter + AddAlbumCounter
        println("-------------------------------------------------------------")
        println("Total no. of number of queries executed = " + (q - queries) + " in last 15 seconds")
        println("-------------------------------------------------------------")
        queries = q
      }
    }

    case initializeClients(n) => {

      for (i <- 0 to n - 1) {
        keys += kpg.generateKeyPair()
        aeskeys += kg.generateKey()
      }

      for (i <- 0 to n - 1) {
        actor += clientSystem.actorOf(Props(new FbClientActor(self, keys, aeskeys)), name = "actor" + i)
      }

      //clientSystem.scheduler.schedule(60 seconds,1 seconds)(firequery())
      for (i <- 0 to n - 1) {
        //Thread sleep 1000
        actor(i) ! init(i, n, image)
      }
      var tick = context.system.scheduler.schedule(scala.concurrent.duration.Duration.create(1, java.util.concurrent.TimeUnit.MILLISECONDS), scala.concurrent.duration.Duration.create(10000, java.util.concurrent.TimeUnit.MILLISECONDS), self, "report")
      def firequery() = {
        actor(inc) ! init(inc, n, image)
        inc += 1
      }

    }


  }


}


class FbClientActor(server: ActorRef, rsakeys: ArrayBuffer[KeyPair], aeskeys: ArrayBuffer[SecretKey]) extends Actor {
  var master = server
  var tick: Cancellable = null
  var system = ActorSystem("FbClientEnv")
  val timeout1 = Timeout(60 minutes)
  val timeout2 = Timeout(1 minutes)
  var secRand: SecureRandom = new SecureRandom()

  var kg: KeyGenerator = KeyGenerator.getInstance("AES");
  kg.init(128);

  val ec = scala.concurrent.ExecutionContext.Implicits.global
  var requestsProcessed: Int = 0
  val getRequest: HttpRequest => Future[HttpResponse] = sendReceive(implicitly[ActorRefFactory],
    implicitly[ExecutionContext], timeout1)
  val postRequest: HttpRequest => Future[String] = (sendReceive(implicitly[ActorRefFactory],
    implicitly[ExecutionContext], timeout1) ~> unmarshal[String])
  val putRequest: HttpRequest => Future[HttpResponse] = sendReceive(implicitly[ActorRefFactory],
    implicitly[ExecutionContext], timeout1)
  val deleteRequest: HttpRequest => Future[HttpResponse] = sendReceive(implicitly[ActorRefFactory],
    implicitly[ExecutionContext], timeout1)


  def post(url: String, objJsonString: String): Future[String] = {
    val futureResult = postRequest(Post(url, objJsonString))
    futureResult
  }

  def get(url: String): Future[String] = {
    val futureResult = getRequest(Get(url))
    futureResult.map(_.entity.asString)
  }

  def delete(url: String): Future[String] = {
    val futureResult = deleteRequest(Delete(url))
    futureResult.map(_.entity.asString)
  }

  def put(url: String, objJsonString: String): Future[String] = {
    val futureResult = putRequest(Put(url, objJsonString))
    futureResult.map(_.entity.asString)
  }

  def GetProfile(userID: String) = {
    val futurePofile = get("http://127.0.0.1:8006/getProfile/" + userID)
    futurePofile.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementGetProfileCounter"
  }


  def AddFriend(userID: String, friendID: String) = {
    val futureFriendAddResponse = post("http://127.0.0.1:8006/addFriends/" + userID + "/" + friendID, "")
    futureFriendAddResponse.onComplete {
      case Success(response) => println("added friends" + response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementAddFriendCounter"
  }

  def GetFriendlist(userID: String) = {
    val futureFriendListResponse = get("http://127.0.0.1:8006/getFriendList/" + userID)
    futureFriendListResponse.onComplete {
      case Success(response) => println("user" + userID + " friendList" + response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementGetFriendlistCounter"
  }

  def deserializePostsArray(pageJsonString: String, userID: Int, secretKey:SecretKey) = {

    if(!pageJsonString.equalsIgnoreCase("user profile does not exist") && !pageJsonString.equalsIgnoreCase("page doesn't exist")){
      var jsonString = pageJsonString.split("-")
      println("----->" + jsonString(0))
      var len = jsonString(0).toInt
      var json: JsValue = Json.parse(jsonString(1))
      for (i <- 0 to len - 1) {
        var temp = (json(i).\\("post").flatMap {
          _.as[String]
        }).map(_.toString).toList.mkString
        var temp1 = (json(i).\\("postedBy").flatMap {
          _.as[String]
        }).map(_.toString).toList.mkString
        var encryptedKey = (json(i).\\("secretKey").flatMap {
          _.as[String]
        }).map(_.toString).toList.mkString

        var bytes: Array[Byte] = decryptRSA(Base64.decodeBase64(encryptedKey), rsakeys(userID.toInt).getPrivate())
        var secKey: SecretKey = new SecretKeySpec(bytes, 0, bytes.length, "AES")

        println("temp = " + temp)
        if (!temp.isEmpty) {
          val str: String = new String(decryptAES(Base64.decodeBase64(temp), secKey))
          println(ConvertToJson.toJsonObject(Posts(temp1, str, secKey.toString)))
        }

      }

      // json.as[ArrayBuffer[String]]

    }
    else
      println("user profile does not exist")
  }

	def GetPagePosts(pageName:String)={
	     var userID=pageName.substring(pageName.indexOf("r")+1, pageName.indexOf("p"))
	     println("userId--->"+userID+"pageName"+pageName)
	    val futurePofile = get("http://127.0.0.1:8006/getPagePosts/" + pageName)
	    futurePofile.onComplete {
	      case Success(response) => 
        
	        println("------------------Page Posts----------"+"\n"+deserializePostsArray(response, userID.toInt, aeskeys(userID.toInt)))
        
	      case Failure(error) => println("An error has occured: " + error.getMessage)
	    }(ec)
	   master ! "incrementGetProfilePostsCounter"
	 }

  def GetPagePostsUsingKey(pageName:String)={
    var userID=pageName.substring(pageName.indexOf("r")+1, pageName.indexOf("p"))

    val futureFriendAddResponse = get("http://127.0.0.1:8006/GetAesKey/" + userID + "/" + userID)
    futureFriendAddResponse.onComplete {
      case Success(response) => {
        if(!response.equals(" You are not a friend, So can't accces Aes key"))
        {
          //println("Get key : " + response)
          //println("Org key : " + Base64.encodeBase64String(encryptRSA(aeskeys(friendID.toInt).getEncoded(), rsakeys(friendID.toInt).getPublic())))
          var bytes: Array[Byte] = decryptRSA(Base64.decodeBase64(response), rsakeys(userID.toInt).getPrivate())
          var secretKey: SecretKey = new SecretKeySpec(bytes, 0, bytes.length, "AES")

          println("Get key : " + secretKey)
          //println("Org key : " + aeskeys(userID.toInt))

          val futurePofile = get("http://127.0.0.1:8006/getPagePosts/" + pageName)
          futurePofile.onComplete {
            case Success(response) =>

              println("------------------Page Posts----------"+"\n"+deserializePostsArray(response, userID.toInt, secretKey))

            case Failure(error) => println("An error has occured: " + error.getMessage)
          }(ec)
          master ! "incrementGetProfilePostsCounter"
        }
      }
      case Failure(error) => println("An error has occured while posting: " + error.getMessage)
    }(ec)

  }


  def GetProfilePosts(userID: String) = {
    val futurePofile = get("http://127.0.0.1:8006/getPostsProfile/" + userID)
    futurePofile.onComplete {
      case Success(response) => println(deserializePostsArray(response, userID.toInt, aeskeys(userID.toInt)))
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementGetProfilePostsCounter"
  }

  def GetProfilePostsUsingKey(userID: String) = {
    val futureFriendAddResponse = get("http://127.0.0.1:8006/GetAesKey/" + userID + "/" + userID)
    futureFriendAddResponse.onComplete {
      case Success(response) => {
        if(!response.equals(" You are not a friend, So can't accces Aes key"))
        {
          //println("Get key : " + response)
          //println("Org key : " + Base64.encodeBase64String(encryptRSA(aeskeys(friendID.toInt).getEncoded(), rsakeys(friendID.toInt).getPublic())))
          var bytes: Array[Byte] = decryptRSA(Base64.decodeBase64(response), rsakeys(userID.toInt).getPrivate())
          var secretKey: SecretKey = new SecretKeySpec(bytes, 0, bytes.length, "AES")

          println("Get key : " + secretKey)
          //println("Org key : " + aeskeys(userID.toInt))

          val futurePofile = get("http://127.0.0.1:8006/getPostsProfile/" + userID)
          futurePofile.onComplete {
            case Success(response) => println(deserializePostsArray(response, userID.toInt, secretKey))
            case Failure(error) => println("An error has occured: " + error.getMessage)
          }(ec)
          master ! "incrementGetProfilePostsCounter"
        }
      }
      case Failure(error) => println("An error has occured while posting: " + error.getMessage)
    }(ec)

  }

  def PostOnProfile(userID: String, FriendID: String, ObjString: String) = {
    val futurePostOnProfileResponse = post("http://127.0.0.1:8006/PostOnProfile/" + userID + "/" + FriendID, ObjString)
    futurePostOnProfileResponse.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementPostOnProfileCounter"
  }

  def PostOnProfileUsingKey(userID: String, friendID: String, kg: KeyGenerator) = {
    val futureFriendAddResponse = get("http://127.0.0.1:8006/GetAesKey/" + userID + "/" + friendID)
    futureFriendAddResponse.onComplete {
      case Success(response) => {
        if(!response.equals(" You are not a friend, So can't accces Aes key"))
          {
            //println("Get key : " + response)
            //println("Org key : " + Base64.encodeBase64String(encryptRSA(aeskeys(friendID.toInt).getEncoded(), rsakeys(friendID.toInt).getPublic())))
            var bytes: Array[Byte] = decryptRSA(Base64.decodeBase64(response), rsakeys(friendID.toInt).getPrivate())
            var secretKey: SecretKey = new SecretKeySpec(bytes, 0, bytes.length, "AES")

            println("Get key : " + secretKey)
            //println("Org key : " + aeskeys(friendID.toInt))

            //Now post on profile
            val secretKey1: SecretKey = kg.generateKey()
            val secKey: String = Base64.encodeBase64String(encryptRSA(secretKey1.getEncoded(), rsakeys(friendID.toInt).getPublic()))
            val ObjString: String = ConvertToJson.toJsonObject(Posts("5", Base64.encodeBase64String(encryptAES("Hi my name is prateek".getBytes(), secretKey1)), secKey))

            val futurePostOnProfileResponse = post("http://127.0.0.1:8006/PostOnProfile/" + userID + "/" + friendID, ObjString)
            futurePostOnProfileResponse.onComplete {
              case Success(response) => println(response)
              case Failure(error) => println("An error has occured: " + error.getMessage)
            }(ec)
            master ! "incrementPostOnProfileCounter"
          }
      }
      case Failure(error) => println("An error has occured while posting: " + error.getMessage)
    }(ec)
  }

  def PostOnPage(userID: String, PageID: String, JsonString: String) = {
    val futurePostOnPageResponse = post("http://127.0.0.1:8006/PostOnPage/" + userID + "/" + PageID, JsonString)
    futurePostOnPageResponse.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementPostOnPageCounter"
  }

  def PostOnPageUsingKey(userID: String, PageID: String, kg: KeyGenerator) = {
    val futureFriendAddResponse = get("http://127.0.0.1:8006/GetAesKey/" + userID + "/" + userID)
    futureFriendAddResponse.onComplete {
      case Success(response) => {
        if(!response.equals(" You are not a friend, So can't accces Aes key"))
        {
          //println("Get key : " + response)
          //println("Org key : " + Base64.encodeBase64String(encryptRSA(aeskeys(friendID.toInt).getEncoded(), rsakeys(friendID.toInt).getPublic())))
          var bytes: Array[Byte] = decryptRSA(Base64.decodeBase64(response), rsakeys(userID.toInt).getPrivate())
          var secretKey: SecretKey = new SecretKeySpec(bytes, 0, bytes.length, "AES")

          println("Get key : " + secretKey)
          //println("Org key : " + aeskeys(userID.toInt))

          //Now post on page
          val secretKey1: SecretKey = kg.generateKey()
          val secKey: String = Base64.encodeBase64String(encryptRSA(secretKey1.getEncoded(), rsakeys(userID.toInt).getPublic()))

          val ObjString: String = ConvertToJson.toJsonObject(Posts("6", Base64.encodeBase64String(encryptAES("Hi this is my page".getBytes(), secretKey1)), secKey))

          val futurePostOnPageResponse = post("http://127.0.0.1:8006/PostOnPage/" + userID + "/" + PageID, ObjString)
          futurePostOnPageResponse.onComplete {
            case Success(response) => println(response)
            case Failure(error) => println("An error has occured: " + error.getMessage)
          }(ec)
          master ! "incrementPostOnPageCounter"
        }
      }
      case Failure(error) => println("An error has occured while posting: " + error.getMessage)
    }(ec)
  }

  def AddAlbum(userID: String, AlbumName: String) = {
    val futureAddAlbumResponse = put("http://127.0.0.1:8006/addAlbum/" + userID + "/" + AlbumName, "")
    futureAddAlbumResponse.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)

    master ! "incrementAddAlbumCounter"
  }

  def AddPhotoUsingKey(userID: String, photoID: String, image: String) = {
    val futureFriendAddResponse = get("http://127.0.0.1:8006/GetAesKey/" + userID + "/" + userID)
    futureFriendAddResponse.onComplete {
      case Success(response) => {
        if(!response.equals(" You are not a friend, So can't accces Aes key"))
        {
          //println("Get key : " + response)
          //println("Org key : " + Base64.encodeBase64String(encryptRSA(aeskeys(friendID.toInt).getEncoded(), rsakeys(friendID.toInt).getPublic())))
          var bytes: Array[Byte] = decryptRSA(Base64.decodeBase64(response), rsakeys(userID.toInt).getPrivate())
          var secretKey: SecretKey = new SecretKeySpec(bytes, 0, bytes.length, "AES")

          println("Get key : " + secretKey)
          //println("Org key : " + aeskeys(userID.toInt))

          //Now post on profile
          val ObjString: String = ConvertToJson.toJsonObject(Photo("photo1", Base64.encodeBase64String(encryptAES(image.getBytes(), secretKey)), image.size))

          val futureAddPhotoResponse = put("http://127.0.0.1:8006/addPhoto/" + userID + "/" + photoID, ObjString)
          futureAddPhotoResponse.onComplete {
            case Success(response) => println(response)
            case Failure(error) => println("An error has occured: " + error.getMessage)
          }(ec)
          master ! "incrementAddPhotoCounter"
        }
      }
      case Failure(error) => println("An error has occured while posting: " + error.getMessage)
    }(ec)
  }

  def AddPhoto(userID: String, photoID: String, JsonString: String) = {
    val futureAddPhotoResponse = put("http://127.0.0.1:8006/addPhoto/" + userID + "/" + photoID, JsonString)
    futureAddPhotoResponse.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementAddPhotoCounter"
  }

  def GetPhotoUsingKey(userID: String, AlbumName: String, Photoindex: String) = {
    val futureFriendAddResponse = get("http://127.0.0.1:8006/GetAesKey/" + userID + "/" + userID)
    futureFriendAddResponse.onComplete {
      case Success(response) => {
        if(!response.equals(" You are not a friend, So can't accces Aes key"))
        {
          //println("Get key : " + response)
          //println("Org key : " + Base64.encodeBase64String(encryptRSA(aeskeys(friendID.toInt).getEncoded(), rsakeys(friendID.toInt).getPublic())))
          var bytes: Array[Byte] = decryptRSA(Base64.decodeBase64(response), rsakeys(userID.toInt).getPrivate())
          var secretKey: SecretKey = new SecretKeySpec(bytes, 0, bytes.length, "AES")

          println("Get key : " + secretKey)
          //println("Org key : " + aeskeys(userID.toInt))

          val futureGetAlbumResponse = get("http://127.0.0.1:8006/getPhoto/" + userID + "/" + AlbumName + "/" + Photoindex)
          futureGetAlbumResponse.onComplete {
            case Success(response) => println(response)
            case Failure(error) => println("An error has occured: " + error.getMessage)
          }(ec)
          master ! "incrementGetPhotoCounter"
        }
      }
      case Failure(error) => println("An error has occured while posting: " + error.getMessage)
    }(ec)
  }

  def GetPhoto(userID: String, AlbumName: String, Photoindex: String) = {
    val futureGetAlbumResponse = get("http://127.0.0.1:8006/getPhoto/" + userID + "/" + AlbumName + "/" + Photoindex)
    futureGetAlbumResponse.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementGetPhotoCounter"
  }

  def GetAlbum(userID: String, AlbumName: String) = {
    val futureGetAlbumResponse = get("http://127.0.0.1:8006/getAlbum/" + userID + "/" + AlbumName)
    futureGetAlbumResponse.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementGetAlbumCounter"

  }

  def CreatePage(userID: String, pageName: String) = {
    val futureGetAlbumResponse = put("http://127.0.0.1:8006/createPage/" + userID + "/" + pageName, "")
    futureGetAlbumResponse.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementCreatePageCounter"
  }

  def LikePage(userID: String, pageName: String) = {
    val futureLikePageResponse = post("http://127.0.0.1:8006/likePage/" + userID + "/" + pageName, "")
    futureLikePageResponse.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementLikePageCounter"
  }

  def UnLikePage(userID: String, pageName: String) = {
    val futureLikePageResponse = post("http://127.0.0.1:8006/unlikePage/" + userID + "/" + pageName, "")
    futureLikePageResponse.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementUnLikePageCounter"

  }


  def UnFriend(userID: String, FriendID: String) = {
    val futureUnfriendResponse = delete("http://127.0.0.1:8006/unFriend/" + userID + "/" + FriendID)
    // wait for Future to complete
    // val ec =  scala.concurrent.ExecutionContext.Implicits.global
    futureUnfriendResponse.onComplete {
      case Success(response) => println(response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
    master ! "incrementUnFriendCounter"
  }

  def AddKeys(userID: String, publicKey: String, secretKey: String) = {
    val futureFriendAddResponse = post("http://127.0.0.1:8006/addKeys/" + userID, publicKey + "-" + secretKey)
    futureFriendAddResponse.onComplete {
      case Success(response) => println("added keys at user" + response)
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
  }

  def SayHello(str: String, userID: Int): Unit = {
    val futureFriendAddResponse = post("http://127.0.0.1:8006/sayHello/" + userID, str)
    futureFriendAddResponse.onComplete {
      case Success(response) => {
        println("The random No for user " + userID + " = " + response.toString())
        var randomNum: Array[Byte] = Base64.decodeBase64(response.toString())
        val encryptBytes: Array[Byte] = encryptRSA(randomNum, rsakeys(userID).getPrivate())
        var signedNounce: String = Base64.encodeBase64String(encryptBytes)
        val decryptBytes: Array[Byte] = decryptRSA(encryptBytes, rsakeys(userID).getPublic())


        println("signedNounce = " + signedNounce)

        println("before : flag = " + userID + randomNum)
        println("after :  " + userID + decryptBytes)
        SendSignature(userID, signedNounce)
      }
      case Failure(error) => println("An error has occured: " + error.getMessage)
    }(ec)
  }

  def SendSignature(userID: Int, signedNounce: String): Unit = {
    val futureFriendAddResponse = post("http://127.0.0.1:8006/signature/" + userID, signedNounce)
    futureFriendAddResponse.onComplete {
      case Success(response) => println("Login result: " + response)
      case Failure(error) => println("An error has occured while Login: " + error.getMessage)
    }(ec)
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

  def encryptAES(inpBytes: Array[Byte], key: SecretKey): Array[Byte] = {
    var cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    var iv: Array[Byte] = new Array[Byte](16)
    secRand.nextBytes(iv)
    //println("encryptAES iv: " + iv.mkString(" "))
    var ips: IvParameterSpec = new IvParameterSpec(iv)
    cipher.init(Cipher.ENCRYPT_MODE, key, ips)
    var out: Array[Byte] = cipher.doFinal(inpBytes)
    return iv ++ out // appending iv in the beginning of cipher


  }

  def decryptAES(inpBytes: Array[Byte], key: SecretKey): Array[Byte] = {
    var cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    var iv: Array[Byte] = new Array[Byte](16)
    iv = inpBytes.slice(0, 16)
    //println("decryptAES iv: " + iv.mkString(" "))
    var ips: IvParameterSpec = new IvParameterSpec(iv)
    cipher.init(Cipher.DECRYPT_MODE, key, ips)
    //var out:Array[Byte] = inpBytes.drop(16)
    //println("decryptAES out: " + out.mkString(" "))
    return cipher.doFinal(inpBytes.drop(16))
  }

  def level(i: Int, n: Int): Int = {

    if (i >= 0 && i < n / 20)
      return 1; // 5% aggressive users
    else if (i >= n / 20 && i < n / 4)
      return 2 // 20% percent very active users
    else if (i >= n / 4 && i < n / 2)
      return 3 // 25% moderatly active users
    else if (i >= n / 2 && i < ((3 * n) / 4 + n / 10))
      return 4 //  35% passive users
    else if (i >= ((3 * n) / 4 + n / 10) && i < n)
      return 5
    else
      return 6
  }


  def receive = {
    case init(i, n, image) =>

      var userID = i.toString()
      var friendID = Random.nextInt(n).toString()
      while (n != 1 && friendID == userID) {
        friendID = Random.nextInt(n).toString()
      }

      //saving encrypted secret key at server
      val pubKey: String = Base64.encodeBase64String(rsakeys(i).getPublic().getEncoded())
      val secKey: String = Base64.encodeBase64String(encryptRSA(aeskeys(i).getEncoded(), rsakeys(i).getPublic()))
      AddKeys(userID, pubKey, secKey)

      println("pubKey key : for user " + userID + " = " + pubKey + ", enc secretKey = " + secKey)

      //Authentication
      SayHello("user" + i, i)


      var p = level(i, n)
      p match {
        case 1 => {
          // 5% aggressive users
          system.scheduler.schedule(15 seconds, (i % 20) + 15 seconds)(AddFriend(userID, friendID))
          system.scheduler.schedule(15 seconds, (i % 20) + 15 seconds)(CreatePage(userID, "user" + userID + "page" + 0))
          system.scheduler.schedule(15 seconds, (i % 20) + 10 seconds)(LikePage(userID, "user" + Random.nextInt(n) + "page" + 0))
          system.scheduler.schedule(15 seconds,(i%20)+10 seconds)(PostOnPageUsingKey(userID, "user"+userID+"page"+0, kg))
          system.scheduler.schedule(15 seconds, (i % 20) + 15 seconds)(GetPhotoUsingKey(userID, "user" + userID + "album" + 0, 0.toString))
          system.scheduler.schedule(15 seconds, (i % 30) + 10 seconds)(AddPhotoUsingKey(userID, "user" + userID + "album" + 1, image))
          system.scheduler.schedule(15 seconds, (i % 50) + 15 seconds)(PostOnProfileUsingKey(userID, friendID, kg))
          //system.scheduler.schedule(15 seconds, (i % 50) + 15 seconds)(PostOnProfile(userID, friendID, ConvertToJson.toJsonObject(Posts("5", Base64.encodeBase64String(encryptAES("Hi my name is prateek".getBytes(), aeskeys(friendID.toInt)))))))
          system.scheduler.schedule(15 seconds, (i % 20) + 120 seconds)(UnLikePage(userID, "user" + Random.nextInt(n) + "page" + 0))
          system.scheduler.schedule(15 seconds, (i % 20) + 120 seconds)(UnFriend(userID, friendID))
	  system.scheduler.schedule(15 seconds,(i%20)+120 seconds)(GetPagePostsUsingKey("user"+userID+"page"+"0"))
        }
        case 2 => { // 20% percent very active users
          //system.scheduler.schedule(15 seconds, (i % 50) + 15 seconds)(PostOnProfileUsingKey(userID, friendID))
         system.scheduler.schedule(15 seconds,(i%20)+60 seconds)(CreatePage(userID, "user"+userID+"page"+0))
          system.scheduler.schedule(15 seconds,(i%20)+15 seconds)(PostOnProfileUsingKey(userID, friendID, kg))
          system.scheduler.schedule(15 seconds,(i%20)+50 seconds)(GetPhoto(userID,"user" + userID + "album" + 0, 0.toString))
          system.scheduler.schedule(15 seconds,(i%20)+50 seconds)(PostOnPageUsingKey(userID,  "user"+userID+"page"+0, kg))
          system.scheduler.schedule(15 seconds,(i%20)+120 seconds)(GetPagePostsUsingKey("user"+userID+"page"+"0"))

        }
        case 3 =>{ // 25% moderatly active users
          system.scheduler.schedule(15 seconds, (i % 50) + 25 seconds)(PostOnProfileUsingKey(userID, friendID, kg))
          system.scheduler.schedule(15 seconds,(i%10)+120 seconds)(GetPhotoUsingKey(userID,"user" + userID + "album" + 0, 0.toString))
          system.scheduler.schedule(15 seconds,(i%10)+120 seconds)(AddAlbum(userID, "firstAlbum"))
          system.scheduler.schedule(15 seconds,(i%20)+60 seconds)(GetProfile(userID))
          system.scheduler.schedule(15 seconds,(i%20)+60 seconds)(GetFriendlist(userID))
         system.scheduler.schedule(15 seconds,(i%20)+50 seconds)(PostOnPageUsingKey(userID,  "user"+userID+"page"+0, kg))
          system.scheduler.schedule(15 seconds,(i%20)+120 seconds)(GetPagePostsUsingKey("user"+userID+"page"+"0"))
          system.scheduler.schedule(15 seconds,(i%20)+10 seconds)(GetProfilePostsUsingKey(userID))
        }
        case 4 => { // 35% passive users
          system.scheduler.schedule(15 seconds,(i%10)+180 seconds)(AddFriend(userID, friendID))
          system.scheduler.schedule(15 seconds,(i%10)+180 seconds)(GetAlbum(userID, "user" + userID + "album" + 0))
          system.scheduler.schedule(15 seconds,(i%20)+50 seconds)(PostOnPageUsingKey(userID, "user"+userID+"page"+0, kg))
          system.scheduler.schedule(15 seconds,(i%20)+20 seconds)(GetProfilePostsUsingKey(userID))
          system.scheduler.schedule(15 seconds,(i%20)+120 seconds)(GetPagePostsUsingKey("user"+userID+"page"+"0"))
        }
        case 5 => {
          // inactive users
          /*var str: String = "My name is Sharique. I am very go."
          var encBytes: Array[Byte] = encryptRSA(str.getBytes(), rsakeys(i).getPublic())
          val baseEnc: String = Base64.encodeBase64String(encBytes)
          //println("encBytes: " + encBytes.mkString(" "))
          var decBytes: Array[Byte] = decryptRSA(Base64.decodeBase64(baseEnc), rsakeys(i).getPrivate())
          println("encrypted : " + new String(str.getBytes))
          println("decrypted : " + new String(decBytes))*/
        }
        case _ => {}
      }
  }
}

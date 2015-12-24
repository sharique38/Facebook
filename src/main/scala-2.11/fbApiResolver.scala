

/**
 * @author Prateek
 */

import org.apache.commons.codec.binary.Base64

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
import scala.concurrent.ExecutionContext
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
import scala.collection.mutable.ArrayBuffer
import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.Serialization.{read, write, writePretty}
import play.api.libs.json._


class fbApiResolver(server: Array[ActorSelection]) extends Actor with SimpleRoutingApp {

  implicit val timeout: Timeout = 3600.second

  import context.dispatcher

  val ec = scala.concurrent.ExecutionContext.Implicits.global

  // println(server)
  def receive = {
    //implicit val timeout = 3600.second
    case _: Http.Connected =>
      sender ! Http.Register(self)
      println("connected")

    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/getProfile" => {
      val senderMachine = sender
      var clientId = path.split("/").last
      // println("Client Id For getting Profile "+clientId)
      var x: Int = 0

      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientId.toInt) ? GetProfile(clientId)).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println(HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)

    }
    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/getPagePosts" => {
      val senderMachine = sender
      var pageName = path.split("/").last
      // println("Client Id For getting Profile "+clientId)
      var x: Int = 0
      var userID=pageName.substring(pageName.indexOf("r")+1, pageName.indexOf("p"))
      println("Page user------->"+userID)
      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(userID.toInt) ? GetPostsInPage(userID, pageName)).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println(HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)

    }
case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/getPostsProfile" => {
      val senderMachine = sender
      var clientId = path.split("/").last
      // println("Client Id For getting Profile "+clientId)
      var x: Int = 0

      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientId.toInt) ? GetProfilePosts(clientId)).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println(HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)

    }
    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/getAlbum" => {
      val senderMachine = sender
      var clientId = path.split("/")
      println("userId and album index: " + clientId(2) + " " + clientId(3))
      var x: Int = 0

      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientId(2).toInt) ? GetAlbum(clientId(2), clientId(3))).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("sending album" + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println(HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)

    }


    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/getPhoto" => {
      val senderMachine = sender
      var clientId = path.split("/")
      println("userId and album index and photoindex: " + clientId(2) + " " + clientId(3) + " " + clientId(4))
      var x: Int = 0

      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientId(2).toInt) ? GetPhoto(clientId(2), clientId(3), clientId(4))).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("sending album" + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println(HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)

    }
    case HttpRequest(PUT, Uri.Path(path), _, _, _) if path startsWith "/addAlbum" => {
      val senderMachine = sender
      var clientId = path.split("/")
      println("Client Id For getting album: " + clientId)
      var x: Int = 0

      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientId(2).toInt) ? AddAlbum(clientId(2), clientId(3))).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println(HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)

    }
    case HttpRequest(DELETE, Uri.Path(path), _, _, _) if path startsWith "/unFriend" => {
      val senderMachine = sender
      var clientId = path.split("/")
      println("user Id to be unfriended: " + clientId(3))
      var x: Int = 0

      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientId(2).toInt) ? RemoveFriend(clientId(2), clientId(3))).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println(HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)

    }

    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/getFriendList" => {

      val senderMachine = sender
      var clientId = path.split("/").last
      println(" Id For retrieving friendlist: " + clientId)
      var x: Int = 0

      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientId.toInt) ? GetFriends(clientId)).mapTo[String]
      future.onSuccess {

        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println(HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }

    case HttpRequest(POST, Uri.Path(path), _, obj2, _) if path startsWith "/unlikePage" => {

      var clientID = path.split("/")


      val senderMachine = sender

      println("unliking page:->" + clientID(2) + clientID(3))

      var future: Future[String] = (server(clientID(2).toInt) ? UnLikePage(clientID(2), clientID(3))).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)
    }
    case HttpRequest(POST, Uri.Path(path), _, obj2, _) if path startsWith "/likePage" => {

      var clientID = path.split("/")


      val senderMachine = sender

      println("liking page:->" + clientID(2) + clientID(3))

      var future: Future[String] = (server(clientID(2).toInt) ? LikePage(clientID(2), clientID(3))).mapTo[String]
      future.onSuccess {

        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }
    case HttpRequest(PUT, Uri.Path(path), _, obj2, _) if path startsWith "/addPhoto" => {

      var clientID = path.split("/")


      val senderMachine = sender

      //var clientId= path.split("/").last
      println("adding photos: " + "obj2->" + obj2.data.asString)
      println(clientID(2) + "jjhbjhb" + clientID(3))
      //extract{obj2}
      //var x:Int =0
      println
      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientID(2).toInt) ? AddPhoto(clientID(2), clientID(3), obj2.data.asString)).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }

    case HttpRequest(POST, Uri.Path(path), _, obj2, _) if path startsWith "/PostOnProfile" => {

      var clientID = path.split("/")


      val senderMachine = sender

      //var clientId= path.split("/").last
      println("adding photos: " + "obj2->" + obj2.data.asString)
      println(clientID(2) + "jjhbjhb" + clientID(3))
      //extract{obj2}
      //var x:Int =0
      println
      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientID(2).toInt) ? PostOnProfile(clientID(2), clientID(3), obj2.data.asString)).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }
    case HttpRequest(POST, Uri.Path(path), _, obj2, _) if path startsWith "/PostOnPage" => {

      var clientID = path.split("/")


      val senderMachine = sender

      //var clientId= path.split("/").last
      println("adding photos: " + "obj2->" + obj2.data.asString)
      println(clientID(2) + "->" + clientID(3))
      //extract{obj2}
      //var x:Int =0
      println
      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientID(2).toInt) ? PostOnPage(clientID(2), clientID(3), obj2.data.asString)).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }
    case HttpRequest(POST, Uri.Path(path), _, _, _) if path startsWith "/addFriends" => {

      //var clientID= path.substring(path.lastIndexOf("=")+1)


      val senderMachine = sender

      var clientId = path.split("/")
      println("friends request : from" + clientId(2) + clientId(3))

      //extract{obj2}
      //var x:Int =0
      // println
      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientId(2).toInt) ? AddFriend(clientId(2), clientId(3))).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }

    case HttpRequest(POST, Uri.Path(path), _, obj2, _) if path startsWith "/addKeys" => {

      val senderMachine = sender

      var clientId = path.split("/")

      var pub_sec = obj2.data.asString.split("-")
      //println(clientId(0) + "  " + clientId(1) + "  " + clientId(2))
      println("pubKey key : for " + clientId(2) + " = " + pub_sec(0) + " secretKey = " + pub_sec(1))

      var future: Future[String] = (server(clientId(2).toInt) ? AddKeys(clientId(2), pub_sec(0), pub_sec(1))).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }

    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/GetAesKey" => {

      val senderMachine = sender

      var clientId = path.split("/")

      var future: Future[String] = (server(clientId(2).toInt) ? GetAesKey(clientId(2), clientId(3))).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }

    case HttpRequest(POST, Uri.Path(path), _, obj2, _) if path startsWith "/sayHello" => {

      val senderMachine = sender

      var clientId = path.split("/")

      var pub_sec = obj2.data.asString
      //println(clientId(0) + "  " + clientId(1) + "  " + clientId(2))
      println("Login : for " + clientId(2) + " with message " + pub_sec)

      var future: Future[String] = (server(clientId(2).toInt) ? SayHello(clientId(2).toInt, pub_sec)).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }

    case HttpRequest(POST, Uri.Path(path), _, obj2, _) if path startsWith "/signature" => {

      val senderMachine = sender

      var clientId = path.split("/")

      var pub_sec = obj2.data.asString
      //println(clientId(0) + "  " + clientId(1) + "  " + clientId(2))
      println("Encrypted Nonce : for " + clientId(2) + " is " + pub_sec)

      var future: Future[String] = (server(clientId(2).toInt) ? Signature(clientId(2).toInt, pub_sec)).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }

    case HttpRequest(PUT, Uri.Path(path), _, _, _) if path startsWith "/createPage" => {

      //var clientID= path.substring(path.lastIndexOf("=")+1)


      val senderMachine = sender

      var clientId = path.split("/")
      println("Create Page" + clientId(2) + clientId(3))

      //extract{obj2}
      //var x:Int =0
      // println
      // var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
      var future: Future[String] = (server(clientId(2).toInt) ? CreatePage(clientId(2), clientId(3))).mapTo[String]
      future.onSuccess {
        // case result:Profile =>
        case result: String =>
          println("test output.." + result)
          val body = HttpEntity(ContentTypes.`application/json`, result)
          println("result the--->" + HttpResponse(entity = body))
          senderMachine ! HttpResponse(entity = body) //entity = body)
      }(ec)


    }

  }


}




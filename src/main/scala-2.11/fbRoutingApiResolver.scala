

/**
 * @author Prateek

 */
import scala.concurrent.duration.Duration
import scala.util.Try
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.can.server.Stats
import scala.util.{Try, Success, Failure}
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
import org.json4s.native.Serialization.{read, write,writePretty}
import ExecutionContext.Implicits.global
//
//object fbRoutingApiResolver extends App {
//  
//}
object fbApiResolver1 extends App with SimpleRoutingApp {
  

val ec =  scala.concurrent.ExecutionContext.Implicits.global
//  implicit val system = ActorSystem("my-system")
var requestedProfile:String=null
implicit val configSystem = ActorSystem("HTTPServer", ConfigFactory.load(ConfigFactory.parseString("""{ "akka" : { "actor" : { "provider" : "akka.remote.RemoteActorRefProvider" }, "remote" : { "enabled-transports" : [ "akka.remote.netty.tcp" ], "netty" : { "tcp" : { "port" : 5185 , "maximum-frame-size" : 12800000b } } } } } """)))
 var server= configSystem.actorSelection("akka.tcp://fbServerEnv@127.0.0.1:5000/user/fbServer") 
//server ! GetProfile("dfsddf")
println(server)
 implicit val timeout = akka.util.Timeout(5000)

  startServer(interface = "localhost", port = 8080) {
  myRoute
  } 

 lazy val myRoute= path("getProfile"/Segment) { customerId =>
        get {
          respondWithMediaType(`application/json`){var pro:Profile=null
            complete {
        // server!GetProfile("dfsddf")
            var future: Future[String] = (server ? GetProfile(customerId)).mapTo[String]
  // JsonUtil.toJson(future.result(  scala.concurrent.duration.Duration(500000)))
            
//           future.onSuccess
//       {
//               case result:String=>println(result)
//               result    
//       }(ec)
     
    requestedProfile
          }}
          
        }
      }

//    val myRoute=  path("/getProfile") {
//      println("asdda")
//      get{
//            onComplete(getProfile("hgvv")) {
//          //  var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
//        //future.onSuccess
//       {
//               case Success(result)=>{
//                 println(result)
//               val  p = Profile(ClientID = "vhgv", 
//                                    Name = result.Name, 
//                                    Birthday =result.Birthday ,
//                                    Sex=result.Sex)
//       complete(JsonUtil.toJson(pro))}
//               case Failure(e)=>{
//                 println("dfdsfdsf")
//               complete("adfadfaf")
//               }
//       }
//        }
//    }
// 
//    }
    
   // def getProfile(clientId:String): Future[Profile]={(server ? GetProfile(clientId)).mapTo[Profile]}
    
//    }      respondWithMediaType(`application/json`){
//            
//            var future: Future[Profile] = (server ? GetProfile(clientId)).mapTo[Profile]
//        future.onSuccess
//       {
//               case result:Profile=>
//             val  p = Profile(ClientID = clientId, 
//                                    Name = result.Name, 
//                                    Birthday =result.Birthday ,
//                                    Sex=result.Sex)
//        pro=p
//      }(ec)
//            
//            complete {
//         
//            
// 
//                        JsonUtil.toJson(pro)  
//          
//          }}
          
        
      


  

}




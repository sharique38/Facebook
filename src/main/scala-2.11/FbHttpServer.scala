

/**
 * @author Prateek
 */

import akka.event.Logging
import akka.io.IO
import spray.can.Http
import spray.routing.HttpService
import akka.actor._
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport
import akka.remote._
import akka.pattern.ask
import akka.util.Timeout
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
import java.io.File




object FbHttpServer  {

  def main(args: Array[String]) {
    
     val configFile = getClass.getClassLoader.getResource("http_server.conf").getFile
//parse the config
    val config = ConfigFactory.parseFile(new File(configFile))
//create an actor system with that config

    println("enter number of users ")

    val n = readInt()

    
    implicit val HttpServerSystem = ActorSystem("HTTPServerSystem",config )//ConfigFactory.load(ConfigFactory.parseString("""{ "akka" : { "actor" : { "provider" : "akka.remote.RemoteActorRefProvider" }, "remote" : { "enabled-transports" : [ "akka.remote.netty.tcp" ], "netty" : { "tcp" : { "port" : 5185 , "maximum-frame-size" : 12800000b } } } } } """)))


    val server = new Array[ActorSelection](n)
    for(i <-0 to n-1)
    {
      server(i) = HttpServerSystem.actorSelection("akka.tcp://fbServerEnv@127.0.0.1:5000/user/fbServer"+i)
    }

     //var  server = HttpServerSystem.actorSelection("akka.tcp://fbServerEnv@127.0.0.1:5000/user/fbServer")
       
     
      val fbHttpApiResolver = HttpServerSystem.actorOf(Props(new fbApiResolver(server)), name = "fbApiResolver")
      
      IO(Http) ! Http.Bind(fbHttpApiResolver, interface = "127.0.0.1", port = 8006)
      
}

}
name := "Facebook"

version := "0.1"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= {
  val akkaV = "2.3.13"
  val sprayV = "1.3.3"
  Seq(
    "org.json4s" 	  %%	"json4s-native" % "3.2.10",
    "io.spray"            %% "spray-client"   % sprayV,
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-json"    % "1.3.2",
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "com.typesafe.akka" %% "akka-remote" % akkaV  ,
    "com.typesafe.play" %% "play-json" % "2.3.4" ,
    "commons-codec" %% "commons-codec" % "1.10"
  )
}




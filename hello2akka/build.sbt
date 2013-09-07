organization := "com.okosmos"

name         := "hello2akka"

version      := "0.1"

scalaVersion := "2.10.2"

scalacOptions := Seq("-unchecked",
                    "-deprecation",
                    "-encoding",
                    "UTF-8",
                    "-feature",
                    "-language:postfixOps")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.0-RC1",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.0-RC1" % "test",
  "org.scalatest"     %% "scalatest"    % "2.0.M5b" % "test"
)

parallelExecution in Test := false

ideaExcludeFolders ++= Seq(".idea", ".idea_modules")
  
  

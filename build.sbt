import com.typesafe.sbt.SbtScalariform

lazy val commons = Seq(
  organization := "it.datatoknowledge",
  version := "0.1.0",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-target:jvm-1.7", "-feature"),
  resolvers ++= Seq(
    "spray repo" at "http://repo.spray.io",
    Resolver.sonatypeRepo("public"),
    Resolver.typesafeRepo("releases"),
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Maven" at "https://repo1.maven.org/maven2/",
    Resolver.mavenLocal,
    Resolver.bintrayRepo("hseeberger", "maven")
  )
)

lazy val root = (project in file("."))
  .enablePlugins(SbtScalariform, DockerPlugin, JavaAppPackaging)
  .settings(commons: _*)
  .settings(
    name := "wheretolive-api",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-core" % "2.4.2",
      "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.6.2",
      "org.slf4j" % "slf4j-simple" % "1.7.14",
      "de.heikoseeberger" %% "akka-http-json4s" % "1.5.0"
    )
  ) dependsOn algocore

lazy val algocore = (project in file("./algocore"))
  .settings(commons: _*)
  .settings(name := "algocore")

Revolver.settings
fork in Test := true
fork := true


maintainer in Docker := "info@datatotknowledge.it"
version in Docker := version.value
dockerBaseImage := "java:8-jre"
dockerExposedPorts := Seq(9000)
dockerExposedVolumes := Seq("/opt/docker/logs")
dockerRepository := Option("data2knowledge")
import com.typesafe.sbt.SbtScalariform

lazy val commons = Seq(
  organization := "it.datatoknowledge",
  version := "0.7.0",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-target:jvm-1.8", "-feature"),
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
      "com.typesafe.akka" %% "akka-http-core" % "2.4.4",
      "com.typesafe.akka" %% "akka-slf4j" % "2.4.4",
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.6.2",
      "de.heikoseeberger" %% "akka-http-json4s" % "1.5.3",
      "ch.megard" %% "akka-http-cors" % "0.1.0"
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

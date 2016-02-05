name := "swagger-akka-http-sample"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Maven" at "https://repo1.maven.org/maven2/",
  Resolver.mavenLocal,
  Resolver.bintrayRepo("hseeberger", "maven")
)

libraryDependencies ++= Seq(
  "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.6.2",
  "org.slf4j" % "slf4j-simple" % "1.7.14",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.5.0"
)

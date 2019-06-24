import java.io.File

name := "Voteban't"

version := "0.1"

scalaVersion := "2.13.0"

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.2.0"

// JDA
resolvers += "jcenter-bintray" at "http://jcenter.bintray.com"
libraryDependencies += "net.dv8tion" % "JDA" % "3.8.3_463"

// log4j Logger
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.22"

//Custom tasks
lazy val RESOURCES_FILE = new File("src/main/resources")
lazy val ADDITIONAL_RESOURCES: Seq[File] = Seq()
lazy val include = TaskKey[Unit]("include", "Copies the resources that should be packed with the jar to the resources directory")
include := SBTUtillity.includeResources(streams.value.log, RESOURCES_FILE, ADDITIONAL_RESOURCES)
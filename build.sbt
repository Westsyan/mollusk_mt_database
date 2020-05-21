name := "mollusk_mt_database"
 
version := "1.0" 
      
lazy val `mollusk_mt_database` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies += "commons-io" % "commons-io" % "2.5"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.6"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "3.0.3"

libraryDependencies += "com.typesafe.slick" %% "slick-codegen" % "3.3.0"

libraryDependencies += "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.0"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.47"

// https://mvnrepository.com/artifact/com.alibaba/fastjson
libraryDependencies += "com.alibaba" % "fastjson" % "1.2.47"

libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.15"

      
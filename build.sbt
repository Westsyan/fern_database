name := "fern_database"
 
version := "1.0" 
      
lazy val `fern_database` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.11.11"

libraryDependencies ++= Seq( jdbc , cache , ws , specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies += "com.typesafe.slick" % "slick-codegen_2.11" % "3.2.0"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.43"

libraryDependencies += "com.typesafe.play" % "play-slick_2.11" % "2.1.0"

libraryDependencies += "com.github.tototoshi" % "slick-joda-mapper_2.11" % "2.3.0"

libraryDependencies += "commons-io" % "commons-io" % "2.5"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.6"

libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"

// https://mvnrepository.com/artifact/com.alibaba/fastjson
libraryDependencies += "com.alibaba" % "fastjson" % "1.2.47"


// https://mvnrepository.com/artifact/org.apache.poi/poi
libraryDependencies += "org.apache.poi" % "poi" % "3.17"

// https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.17"

// https://mvnrepository.com/artifact/net.sourceforge.jexcelapi/jxl
libraryDependencies += "net.sourceforge.jexcelapi" % "jxl" % "2.6.12"

// https://mvnrepository.com/artifact/javax.mail/mail
libraryDependencies += "javax.mail" % "mail" % "1.5.0-b01"

// https://mvnrepository.com/artifact/cn.edu.hfut.dmic.webcollector/WebCollector
libraryDependencies += "cn.edu.hfut.dmic.webcollector" % "WebCollector" % "2.71"









      
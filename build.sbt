name := "http-benchmark"

version := "1.0"

scalaVersion := "2.9.1"

libraryDependencies += "joda-time" % "joda-time" % "2.0"

libraryDependencies += "org.joda" % "joda-convert" % "1.2"

libraryDependencies +=  "org.clapper" % "argot_2.9.1" % "0.3.5"

libraryDependencies +=  "com.google.guava" % "guava" % "11.0.1"

libraryDependencies += "com.google.inject" % "guice" % "3.0"

libraryDependencies += "net.databinder" % "dispatch-http_2.9.1" % "0.8.8"

libraryDependencies += "net.databinder" % "dispatch-nio_2.9.1" % "0.8.8"

libraryDependencies += "net.databinder" % "dispatch-json_2.9.1" % "0.8.8"

libraryDependencies += "org.apache.commons" % "commons-math" % "2.2"

seq(assemblySettings: _*)
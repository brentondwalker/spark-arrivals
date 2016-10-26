name         := "spark-arrivals"
version      := "1.0"
organization := "properbounds"
scalaVersion := "2.10.6"
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1" % "provided"
libraryDependencies += "commons-cli" % "commons-cli" % "1.2" % "provided"
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"

resolvers += Resolver.mavenLocal

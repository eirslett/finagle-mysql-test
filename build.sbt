name := "Finagle-MySQL Test"

name := "finagle-mysql-test"

version := "0.1"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-mysql" % "6.2.1",
  "org.mockito" % "mockito-core" % "1.9.5",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)
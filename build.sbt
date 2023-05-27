ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = project in file(".") settings (name := "RSITradingView")

libraryDependencies += "org.scalatest" % "scalatest_3" % "3.2.14"
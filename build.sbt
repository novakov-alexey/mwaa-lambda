Global / onChangedBuildSource := ReloadOnSourceChanges

enablePlugins(ScalaJSBundlerPlugin, UniversalPlugin, LambdaJSPlugin)

name := "mwaa-lambda"
maintainer := "alex"

scalaVersion := "2.13.7"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  // Explain type errors in more detail.
  "-explaintypes",
  // Warn when we use advanced language features
  "-feature",
  // Give more information on type erasure warning
  "-unchecked",
  // Enable warnings and lint
  "-Ywarn-unused",
  "-Xlint"
)
webpack / version := "4.46.0"
useYarn := false
webpackConfigFile := Some(baseDirectory.value / "webpack.config.js")
startWebpackDevServer / version := "3.1.4"

// Optional: Disable source maps to speed up compile times
scalaJSLinkerConfig ~= { _.withSourceMap(false) }

// Incluce type defintion for aws lambda handlers
val awsSdkVersion = "2.892.0"
val awsSdkScalajsFacadeVersion = s"0.33.0-v${awsSdkVersion}"
val http4sVersion = "0.23.7"
val natchezVersion = "0.1.6"
val feralVersion = "0.1.0-M1"

libraryDependencies ++= Seq(
  // Feral example deps:
  "io.circe" %%% "circe-parser" % "0.15.0-M1",
  "org.typelevel" %% "feral-lambda" % feralVersion,
  "org.typelevel" %%% "feral-lambda-http4s" % feralVersion,
  "org.http4s" %%% "http4s-dsl" % http4sVersion,
  "org.http4s" %%% "http4s-server" % http4sVersion,
  "org.http4s" %%% "http4s-ember-client" % http4sVersion,
  "org.tpolecat" %%% "natchez-xray" % natchezVersion,
  "org.tpolecat" %%% "natchez-http4s" % "0.2.1",
  //Include some nodejs types (useful for, say, accessing the env)
  "net.exoego" %%% "scala-js-nodejs-v12" % "0.14.0",
  "net.exoego" %%% "aws-sdk-scalajs-facade-mwaa" % awsSdkScalajsFacadeVersion,
  "org.scala-js" %%% "scala-js-macrotask-executor" % "1.0.0"
)

Compile / npmDependencies ++= Seq(
  "aws-sdk" -> awsSdkVersion
)
// Package lambda as a zip. Use `universal:packageBin` to create the zip
topLevelDirectory := None
Universal / mappings ++= (Compile / fullOptJS / webpack).value.map { f =>
  // remove the bundler suffix from the file names
  f.data -> f.data.getName().replace("-opt-bundle", "")
}

// scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
scalaJSUseMainModuleInitializer := true

npmPackageStage := org.scalajs.sbtplugin.Stage.FullOpt

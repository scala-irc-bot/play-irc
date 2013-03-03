import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-irc"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.sisioh" %% "scala-dddbase-core" % "0.0.1",
    "net.mtgto" %% "scala-irc-bot" % "0.2.0-SNAPSHOT"
  )

  def customLessEntryPoints(base: File): PathFinder = (
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "responsive.less") +++
    (base / "app" / "assets" / "stylesheets" * "*.less")
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-encoding", "UTF8"),
    resolvers += Resolver.file("Local Ivy Repository", file(Path.userHome.absolutePath+"/.ivy2/local"))(Resolver.ivyStylePatterns),
    resolvers += "mtgto repos" at "http://scala-irc-bot.github.com/scala-irc-bot/maven/",
    resolvers += "twitter repos" at "http://maven.twttr.com",
    lessEntryPoints <<= baseDirectory(customLessEntryPoints)
  )

}

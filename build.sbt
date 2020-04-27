name := "messages_controller"
version := "1.0"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
resolvers += Resolver.jcenterRepo
scalaVersion := "2.12.2"

lazy val `messages_controller` = (project in file(".")).enablePlugins(PlayScala)

val slickPGExtensionsVersion = "0.17.2"
val macwireWiringVersion = "2.3.3"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.9",
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "org.typelevel" %% "cats-core" % "2.0.0",
  "org.typelevel" %% "cats-effect" % "2.0.0",

  "com.github.tminglei" %% "slick-pg" % slickPGExtensionsVersion,
  "com.github.tminglei" %% "slick-pg_play-json" % slickPGExtensionsVersion,

  "com.softwaremill.macwire" %% "macros" % macwireWiringVersion % "provided",
  "com.softwaremill.macwire" %% "macrosakka" % macwireWiringVersion % "provided",
  "com.softwaremill.macwire" %% "util" % macwireWiringVersion,
  "com.softwaremill.macwire" %% "proxy" % macwireWiringVersion,

  "com.typesafe.akka" %% "akka-persistence" % "2.6.4",
  "com.hootsuite" %% "akka-persistence-redis" % "0.9.0",
  "com.safety-data" %% "akka-persistence-redis" % "0.4.2",
  "net.debasishg" %% "redisclient" % "3.20"
)
class Plugins(info: sbt.ProjectInfo) extends sbt.PluginDefinition(info) {
  val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
  val assemblySBT = "com.codahale" % "assembly-sbt" % "0.1"
  val proguard = "org.scala-tools.sbt" % "sbt-proguard-plugin" % "0.0.5"
}

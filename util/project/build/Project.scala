import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) with assembly.AssemblyBuilder with ProguardProject {
  override def proguardInJars = super.proguardInJars +++ scalaLibraryPath
  override def proguardOptions = "-dontoptimize" :: "-dontobfuscate" :: proguardKeepAllScala :: proguardKeepMain("*") :: Nil

  override def ivyXML =
    <dependencies>
      <exclude module="paranamer"/>
    </dependencies>

  val lift_json = "net.liftweb" %% "lift-json" % "2.3"
  val specs2 = "org.specs2" %% "specs2" % "1.3" % "test"
  val scalaz = "org.specs2" %% "scalaz-core" % "5.1-SNAPSHOT" % "test"

  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)

  val scalaToolsSnapshots      = "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots"
  val scalaToolsNexusSnapshots = "Scala Tools Nexus Snapshots" at "http://nexus.scala-tools.org/content/repositories/snapshots/"
  val scalaToolsNexusReleases  = "Scala Tools Nexus Releases" at "http://nexus.scala-tools.org/content/repositories/releases/"
}

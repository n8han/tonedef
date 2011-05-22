import sbt._

class Project(info: ProjectInfo)
extends AndroidProject(info: ProjectInfo) {    
  def androidPlatformName="android-8"
  val util = "com.eed3si9n" %% "tonedef" % "1.0-SNAPSHOT"
}

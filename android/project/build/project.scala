import sbt._

class Project(info: ProjectInfo)
extends AndroidProject(info: ProjectInfo) {    
  def androidPlatformName="android-8"
}

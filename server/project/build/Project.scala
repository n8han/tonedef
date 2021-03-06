import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  val uf_version = "0.3.1"
  
  // unfiltered
  lazy val ufn = "net.databinder" %% "unfiltered-netty" % uf_version
  lazy val ufw = "net.databinder" %% "unfiltered-websockets" % uf_version

  // netty dependencies
  lazy val jboss = "JBoss repository" at 
    "http://repository.jboss.org/maven2"

  // testing
  lazy val uf_spec = "net.databinder" %% "unfiltered-spec" % uf_version % "test"
  
  // logging
  val javaNetRepo = "Java.net Repository for Maven" at "http://download.java.net/maven/2"
  val newReleaseToolsRepository = ScalaToolsSnapshots
  val avsl = "org.clapper" %% "avsl" % "0.3.1"

  lazy val repo = "jboss repo" at 
    "http://repository.jboss.org/nexus/content/groups/public-jboss/"
}

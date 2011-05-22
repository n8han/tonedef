package tonedef.util

import net.liftweb.json.JsonAST._

class Jsoner {
  def toJson(m: Music): String = {
    import net.liftweb.json.{compact}
    compact(render(musicToJson(m)))
  }

  def musicToJson(that: Music): JValue = {
    import net.liftweb.json.JsonDSL._
    import scala.collection.JavaConversions._
    ("music" ->
      ("name" -> that.name) ~
      ("tracks" ->
        (that.tracks.toList map { case (key, value) =>
          (key -> trackToJson(value))
        })) // tracks
    ) // music
  }

  def trackToJson(that: Track): JValue = {
    import net.liftweb.json.JsonDSL._
    import scala.collection.JavaConversions._
    ("active" -> that.active) ~
    ("instrument" -> that.instrument) ~
    ("notes" ->
      (that.notes.toList map { case (key, value) =>
        (key, noteToJson(value))
      })) // notes
  }

  def noteToJson(that: Note): JValue = {
    import net.liftweb.json.JsonDSL._
    import scala.collection.JavaConversions._
    ("tones" -> JArray(that.tones.toList map { x => JInt(BigInt(x.toString)) })) ~
    ("duration" -> that.duration)
  }
}

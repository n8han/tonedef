package tonedef.util

import net.liftweb.json.JsonAST
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
      ("name" -> (if (that.name == "") None else Some(that.name))) ~
      ("tracks" ->
        {
          val tracks = that.tracks.toList map { case (key, value) =>
            JField(key, trackToJson(value))
          }
          JObject(tracks)
        }
    )) // music
  }

  def trackToJson(that: Track): JValue = {
    import net.liftweb.json.JsonDSL._
    import scala.collection.JavaConversions._
    ("active" -> that.active) ~
    ("instrument" -> that.instrument) ~
    ("notes" ->
      {
        val notes = that.notes.toList map { case (key, value) =>
          JField(key, noteToJson(value))
        }
        JObject(notes)
      }
    ) // notes
  }

  def noteToJson(that: Note): JValue = {
    import net.liftweb.json.JsonDSL._
    import scala.collection.JavaConversions._
    ("tones" -> JArray(that.tones.toList map { x => JInt(BigInt(x.toString)) })) ~
    ("duration" -> that.duration)
  }
}

package tonedef.util

import net.liftweb.json.JsonAST._
import scala.collection.mutable

class Parser {
  def parse(s: String): Music = {
    import net.liftweb.json.{parse => parseJValue}
    val json = parseJValue(s)
    parseMusic(json)
  }

  def parseMusic(json: JValue): Music = {
    import scala.collection.JavaConversions._

    val JObject(music) = json \ "music"
    val JString(name) = JObject(music) \ "name"

    val tracks = Map((for {
      JField("tracks", JObject(tracks)) <- music
      JField(trackName, JObject(track)) <- tracks
    } yield (trackName, track)) map { case (key, value) =>
      (key, parseTrack(JObject(value)))
    } :_*)

    new Music(name, tracks)
  }

  def parseTrack(json: JValue): Track = {
    import scala.collection.JavaConversions._
    val notes = Map((for {
      JObject(notes) <- json \ "notes"
      JField(noteKey, JObject(note)) <- notes
    } yield (noteKey, note)) map { case (key, value) =>
      (key, parseNote(JObject(value)))
    } :_*)
    new Track(notes)
  }

  def parseNote(json: JValue): Note = {
    val tone = for {
      JField("tone", JInt(tone)) <- json
    } yield tone

    val duration = for {
      JField("duration", JInt(duration)) <- json
    } yield duration

    new Note(tone.head.toInt, duration.headOption map {_.toInt} getOrElse {1})
  }
}

package tonedef.util

import net.liftweb.json.JsonAST._
import scala.collection.mutable

class Parser {
  def flattenNotes(music: Music): List[(String, String, Note)] = {
    import scala.collection.JavaConversions._
    music.tracks.toList flatMap { case (trackName, track) =>
      track.notes.toList flatMap { case (noteName, note) =>
        List((trackName, noteName, note))
      }
    }
  }

  def parse(s: String): Music = {
    import net.liftweb.json.{parse => parseJValue}
    val json = parseJValue(s)
    parseMusic(json)
  }

  def parseMusic(json: JValue): Music = {
    import scala.collection.JavaConversions._

    val JObject(music) = json \ "music"
    val name = (for {
      JField("name", JString(name)) <- music
    } yield name).headOption getOrElse {""}

    val tracks = mutable.Map((for {
      JField("tracks", JObject(tracks)) <- music
      JField(trackName, JObject(track)) <- tracks
    } yield (trackName, track)) map { case (key, value) =>
      (key, parseTrack(JObject(value)))
    } :_*)

    new Music(name, tracks)
  }

  def parseTrack(json: JValue): Track = {
    import scala.collection.JavaConversions._
    val active: Boolean = (for {
      JField("active", JBool(active)) <- json
    } yield active).headOption getOrElse {false}

    val notes = mutable.Map((for {
      JObject(notes) <- json \ "notes"
      JField(noteKey, JObject(note)) <- notes
    } yield (noteKey, note)) map { case (key, value) =>
      (key, parseNote(JObject(value)))
    } :_*)
    new Track(active, "", notes)
  }

  def parseNote(json: JValue): Note = {
    import java.lang.{Integer => JLInteger}
    import scala.collection.JavaConversions._
    val tones = for {
      JField("tones", JArray(tones)) <- json
      JInt(tone) <- tones
    } yield new JLInteger(tone.toInt)

    val duration = for {
      JField("duration", JInt(duration)) <- json
    } yield duration

    new Note(tones, duration.headOption map {_.toInt} getOrElse {1})
  }
}

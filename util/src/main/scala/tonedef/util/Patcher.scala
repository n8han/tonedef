package tonedef.util

import net.liftweb.json.JsonAST._

class Patcher {
  val parser = new Parser()
  val jsoner = new Jsoner()

  def patchString(original: String, diff: String): String = {
    import net.liftweb.json.{parse, compact}
    compact(render(patch(parse(original), parse(diff))))
  }

  def patch(original: JValue, diff: JValue): JValue = {
    val merged = original merge diff transform {
      case JField(x, JNull) => JField(x, JNothing)
    }
    val m = parser.parseMusic(merged)
    parser.flattenNotes(parser.parseMusic(diff)) map { case (trackName, noteName, note) =>
      if (note.tones.size == 0) m.tracks.get(trackName).notes.remove(noteName)
      else m.tracks.get(trackName).notes.get(noteName).tones = note.tones
    }
    jsoner.musicToJson(m)
  }
}
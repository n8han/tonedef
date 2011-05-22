package tonedef.util

import net.liftweb.json.JsonAST._

class Patcher {
  val parser = new Parser()
  val jsoner = new Jsoner()

  def patchString(original: String, diff: String): String = {
    import net.liftweb.json.{parse, compact}
    compact(render(patchJson(parse(original), parse(diff))))
  }

  def patchMusic(original: Music, diff: String): Music = {
    import net.liftweb.json.{parse}
    val js = patchJson(jsoner.musicToJson(original), parse(diff))
    parser.parseMusic(js)
  }

  def patchJson(original: JValue, diff: JValue): JValue = {
    val merged = original merge diff

    val m = parser.parseMusic(merged)
    parser.flattenNotes(parser.parseMusic(diff)) map { case (trackName, noteName, note) =>
      if (note.tones.size == 0) Option(m.tracks.get(trackName)) map { track =>
        track.notes.remove(noteName) }
      else Option(m.tracks.get(trackName)) map { track =>
        Option(track.notes.get(noteName)) map { note =>
          note.tones = note.tones
        }
      }
    }
    jsoner.musicToJson(m)
  }
}

import net.liftweb.json.JsonAST._
import org.specs2.mutable._
import tonedef.util.{Parser, Patcher, Note}
import scala.collection.mutable.{Map => MMap}
import scala.collection.JavaConversions._

class PatcherSpec extends Specification {
  "the parser" should {
    "parse music" in {
      val parser = new Parser()
      val music = parser.parseMusic(original)

      music.name must_== "foo"
      music.tracks.size must_== 1
      val track0 = music.tracks.get("0")
      track0.notes.size must_== 2
      val notes: MMap[String, Note] = track0.notes
      notes.toString must_== "Map(1 -> Note(Tones(0,), 1), 0 -> Note(Tones(3,), 3))"
    }
  }

  "the patcher" should {
    import net.liftweb.json.compact
    val patcher = new Patcher()
    val after = patcher patch(original, diff)
    val s = compact(render(after))
    println(s)

    "merge a patch into an existing music" in {
      s must =~ (""".*5.*""")
    }

    "delete a note when tones are empty" in {
      s must not =~ (""".*"8":\{"tones".*""")
    }
  }

  def diff: JObject = {
    import net.liftweb.json.JsonDSL._
    ("music" ->
      ("tracks" ->
        ("0" ->
          ("notes" ->
            ("1" ->
              ("tones" -> JArray(Nil))
            ) ~
            ("5" ->
              ("tones" -> JArray(List(JInt(4))))
            )
          ) // notes
        ) // "0"
      ) // tracks
    ) // music
  }

  def original: JObject = {
    import net.liftweb.json.JsonDSL._
    ("music" ->
      ("name" -> "foo") ~
      ("tracks" ->
        ("0" ->
          ("active" -> true) ~
          ("instrument" -> "piano") ~
          ("notes" ->
            ("1" ->
              ("tones" -> JArray(List(JInt(0))))
            ) ~ // note at 1
            ("0" ->
              ("tones" -> JArray(List(JInt(3)))) ~
              ("duration" -> 3)
            ) // note at 0
          ) // notes
        ) // "0"
      ) // tracks
    ) // music
  }
}

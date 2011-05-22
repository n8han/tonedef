import net.liftweb.json.JsonAST._
import org.specs2.mutable._
import scala.collection.mutable.{Map => MMap}
import scala.collection.JavaConversions._
import tonedef.util.{Jsoner, Parser, Patcher, Note}

class PatcherSpec extends Specification {
  "the parser" should {
    val parser = new Parser()
    "parse string" in {
      val music = parser.parse(ryansStuff)
      val jsoner = new Jsoner()
      val s = jsoner.toJson(music)
      println(s)
      music.name must_== "foo"
    }

    "parse music" in {
      val music = parser.parseMusic(original)

      music.name must_== "foo"
      music.tracks.size must_== 1
      val track0 = music.tracks.get("0")
      track0.notes.size must_== 2
      val notes: MMap[String, Note] = track0.notes
      notes.toString must_== "Map(0 -> Note(Tones(3,), 3), 1 -> Note(Tones(0,), 1))"
    }
  }

  "the patcher" should {
    import net.liftweb.json.compact
    val patcher = new Patcher()
    val after = patcher patchJson(original, diff)
    val s = compact(render(after))
    println(s)

    "merge a patch into an existing music" in {
      s must =~ (""".*5.*""")
    }

    "delete a note when tones are empty" in {
      s must not =~ (""".*"1":\{"tones".*""")
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

  def ryansStuff: String = """{"music":{"name":"foo","tracks":{"0":{"notes":{"5":{"tones":[5],"duration":1}},"instrument":"0","active":true}}}}"""

  def originalString: String = """{ "music": {
    "name": "foo",
    "tracks": {
      "0": {
        "active": true,
        "instrument": "piano",
        "notes": {
          "1":{
            "tones": [0, 1]
            },
          "0": {
            "tones": [3],
            "duration":3
          }
        }
      } } }}"""
}

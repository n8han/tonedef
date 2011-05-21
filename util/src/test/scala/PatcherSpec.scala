import org.specs2.mutable._
import tonedef.util.Patcher
import net.liftweb.json.JsonAST._

class PatcherSpec extends Specification {
  "the patcher" should {
    "merge a patch into an existing music" in {
      import net.liftweb.json.compact
      val patcher = new Patcher()
      val after = patcher patch(original, diff)
      val s = compact(render(after))
      s must =~ (""".*5.*""")
      s must not =~ (""".*"1":{"tone".*""")
    }
  }

  def diff: JObject = {
    import net.liftweb.json.JsonDSL._
    ("music" ->
      ("tracks" ->
        ("0" ->
          ("notes" ->
            ("1" -> JNull) ~
            ("5" ->
              ("tone" -> 4)
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
              ("tone" -> 0)
            ) ~ // note at 1
            ("0" ->
              ("tone" -> 3) ~
              ("duration" -> 3)
            ) // note at 0
          ) // notes
        ) // "0"
      ) // tracks
    ) // music
  }
}
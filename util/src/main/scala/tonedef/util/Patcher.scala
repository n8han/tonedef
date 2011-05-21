package tonedef.util

import net.liftweb.json.JsonAST._

class Patcher {
  def patchString(original: String, diff: String): String = {
    import net.liftweb.json.{parse, compact}
    compact(render(patch(parse(original), parse(diff))))
  }

  def patch(original: JValue, diff: JValue): JValue = {
    val merged = original merge diff
    merged transform {
      case JField(x, JNull) => JField(x, JNothing)
    }
  }
}
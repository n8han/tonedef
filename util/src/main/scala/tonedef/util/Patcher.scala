package tonedef.util

import net.liftweb.json.JsonAST._

class Patcher {
  def patch(original: JObject, diff: JObject): JValue = {
    import net.liftweb.json.JsonDSL._
    val merged = original merge diff
    merged
  }
}
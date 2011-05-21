package tonedef.util

import net.liftweb.json.JsonAST._

class Patcher {
  def patch(original: JObject, diff: JObject): JObject = {
    import net.liftweb.json.JsonDSL._
    original
  }
}
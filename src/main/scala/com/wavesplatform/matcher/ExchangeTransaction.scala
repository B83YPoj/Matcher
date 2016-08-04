package com.wavesplatform.matcher

import play.api.libs.json.JsObject

trait ExchangeTransaction {

  val json: JsObject
}

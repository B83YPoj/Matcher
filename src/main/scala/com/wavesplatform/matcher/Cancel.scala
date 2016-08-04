package com.wavesplatform.matcher

case class Cancel(SpendAddress: String, OrderID: Long, Signature: String) {

  lazy val isValid: Boolean = {
    //TODO check signature
    ???
  }
}



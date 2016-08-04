package com.wavesplatform.matcher

case class Order(spendAddress: String, spendTokenID: String, receiveTokenID: String, price: Int, amount: Long,
                 signature: String) {

  lazy val isValid: Boolean = {
    //TODO check signature
    ???
  }

}

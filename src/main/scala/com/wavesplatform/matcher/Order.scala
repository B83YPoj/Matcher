package com.wavesplatform.matcher

import scorex.account.PublicKeyAccount

import scala.util.Try

case class Order(spendAddress: PublicKeyAccount, matcherAddress: PublicKeyAccount, spendTokenID: Array[Byte],
                 receiveTokenID: Array[Byte], price: Long, amount: Long, signature: Array[Byte]) {
  lazy val isValid: Boolean = {
    //TODO check signature
    ???
  }

}

case class OrderJS(spendAddress: String, matcherAddress: String, spendTokenID: String, receiveTokenID: String,
                   price: Int, amount: Long, signature: String) {

  lazy val order: Try[Order] = ???

}

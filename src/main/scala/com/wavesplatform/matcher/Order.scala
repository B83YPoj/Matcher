package com.wavesplatform.matcher

import com.google.common.primitives.Longs
import scorex.account.PublicKeyAccount
import scorex.crypto.EllipticCurveImpl
import scorex.crypto.encode.Base58

import scala.util.Try

case class Order(spendAddress: PublicKeyAccount, matcherAddress: PublicKeyAccount, spendTokenID: Array[Byte],
                 receiveTokenID: Array[Byte], price: Long, amount: Long, signature: Array[Byte]) {

  lazy val isValid: Boolean = EllipticCurveImpl.verify(signature, content, spendAddress.publicKey)

  private lazy val content: Array[Byte] = {
    spendAddress.bytes ++ matcherAddress.bytes ++ spendTokenID ++ receiveTokenID ++ Longs.toByteArray(price) ++
      Longs.toByteArray(amount) ++ signature
  }

}

case class OrderJS(spendAddress: String, matcherAddress: String, spendTokenID: String, receiveTokenID: String,
                   price: Int, amount: Long, signature: String) {

  lazy val order: Try[Order] = Try {
    val add = new PublicKeyAccount(Base58.decode(spendAddress).get)
    val matcher = new PublicKeyAccount(Base58.decode(matcherAddress).get)
    val spendToken = Base58.decode(spendTokenID).get
    val receiveToken = Base58.decode(receiveTokenID).get
    val sig = Base58.decode(signature).get
    Order(add, matcher, spendToken, receiveToken, price, amount, sig)
  }

}

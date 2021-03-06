package com.wavesplatform.matcher

import com.google.common.primitives.Longs
import scorex.account.PublicKeyAccount
import scorex.crypto.EllipticCurveImpl
import scorex.crypto.encode.Base58

import scala.util.Try

case class Cancel(spendAddress: PublicKeyAccount, orderID: Long, signature: Array[Byte]) {


  lazy val isValid: Boolean = EllipticCurveImpl.verify(signature, content, spendAddress.publicKey)

  private lazy val content: Array[Byte] = spendAddress.bytes ++ Longs.toByteArray(orderID)
}


case class CancelJS(spendAddress: String, orderID: Long, signature: String) {
  lazy val cancel: Try[Cancel] = Try {
    val add = new PublicKeyAccount(Base58.decode(spendAddress).get)
    val sig = Base58.decode(signature).get
    Cancel(add, orderID, sig)
  }

}

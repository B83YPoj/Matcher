package com.wavesplatform.matcher

import play.api.libs.json.JsObject
import scorex.network.message.Message._
import scorex.network.message.MessageSpec
import scorex.serialization.Deser
import scorex.transaction.Transaction

import scala.util.Try

trait ExchangeTransaction extends Transaction {

  val json: JsObject

  val isCompleted: Boolean
}

object ExchangeTransaction extends Deser[ExchangeTransaction] {
  override def parseBytes(bytes: Array[MessageCode]): Try[ExchangeTransaction] = ???
}

object ExchangeTransactionMessageSpec extends MessageSpec[Transaction] {
  override val messageCode: MessageCode = 111: Byte

  override val messageName: String = "Transaction message"

  override def deserializeData(bytes: Array[MessageCode]): Try[ExchangeTransaction] =
    ExchangeTransaction.parseBytes(bytes)

  override def serializeData(tx: Transaction): Array[MessageCode] = tx.bytes
}

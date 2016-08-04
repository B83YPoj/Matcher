package com.wavesplatform.matcher

import scala.util.Try

class Matcher {

  /**
   * Cancel order by id
   */
  def cancel(c: Cancel): Boolean = ???

  /**
   * Place a new order.
   * Return unsigned exchange transactions if something matched
   */
  def place(o: Order): Seq[ExchangeTransaction] = ???

  /**
   * Return unsigned exchange transactions for current address
   */
  def transactionsToSign(address: String): Seq[ExchangeTransaction] = ???

  /**
   * Check, that signature is valid and sign transaction from pool
   */
  def sign(txId: Array[Byte], signature: Array[Byte]): Try[ExchangeTransaction] = ???


  //TODO service that collects old unsigned transactions and replace orders of valid addresses to orders pool
}



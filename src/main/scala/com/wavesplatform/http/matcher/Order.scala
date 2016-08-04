package com.wavesplatform.http.matcher

case class Order(spendAddress: String, spendTokenID: String, receiveTokenID: String, price: Int, amount: Long, 
                 signature: String)

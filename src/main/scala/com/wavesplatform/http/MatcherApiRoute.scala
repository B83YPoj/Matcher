package com.wavesplatform.http

import javax.ws.rs.Path

import akka.actor.ActorRefFactory
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import com.wavesplatform.matcher.{Cancel, Matcher, Order}
import io.swagger.annotations._
import play.api.libs.json.{JsSuccess, JsError, Json}
import scorex.api.http._
import scorex.app.RunnableApplication
import scorex.consensus.mining.BlockGeneratorController._
import scorex.crypto.encode.Base58
import scorex.network.BlockchainSynchronizer
import scorex.transaction.LagonakiTransaction.ValidationResult
import scorex.transaction.state.wallet.Payment
import scorex.utils.ScorexLogging
import scorex.waves.settings.Constants
import scorex.waves.settings.Constants
import scorex.waves.transaction.SignedPayment

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

@Path("/matcher")
@Api(value = "matcher")
case class MatcherApiRoute(override val application: RunnableApplication)(implicit val context: ActorRefFactory)
  extends ApiRoute with CommonApiFunctions with ScorexLogging {

  override lazy val route =
    pathPrefix("matcher") {
      place ~ cancel ~ getUnsigned
    }

  val matcher = new Matcher

  @Path("/order/cancel")
  @ApiOperation(value = "Cancel",
    notes = "Calncel your order",
    httpMethod = "POST",
    produces = "application/json",
    consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      value = "Json with data",
      required = true,
      paramType = "body",
      dataType = "com.wavesplatform.matcher.Cancel",
      defaultValue = "{\n\t\"spendAddress\":\"spendAddress\",\n\t\"OrderID\":0,\n\t\"signature\":\"signature\"\n}"
    )
  ))
  def cancel: Route = path("order/cancel") {
    withCors {
      entity(as[String]) { body =>
        postJsonRoute {
          Try(Json.parse(body)).map { js =>
            js.validate[Cancel] match {
              case err: JsError =>
                WrongTransactionJson(err).response
              case JsSuccess(cancelOrder: Cancel, _) =>
                if (cancelOrder.isValid) {
                  JsonResponse(Json.obj("cancelled" -> matcher.cancel(cancelOrder)), StatusCodes.OK)
                } else InvalidSignature.response
            }
          }.getOrElse(WrongJson.response)
        }
      }
    }
  }


  @Path("/order/place")
  @ApiOperation(value = "Place",
    notes = "Place new order",
    httpMethod = "POST",
    produces = "application/json",
    consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      value = "Json with data",
      required = true,
      paramType = "body",
      dataType = "com.wavesplatform.matcher.Payment",
      defaultValue = "{\n\t\"spendAddress\":\"spendAddress\",\n\t\"spendTokenID\":\"spendTokenID\",\n\t\"receiveTokenID\":\"receiveTokenID\",\n\t\"price\":1,\n\t\"amount\":1,\n\t\"signature\":\"signature\"\n}"
    )
  ))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Json with response or error")))
  def place: Route = path("order/place") {
    withCors {
      entity(as[String]) { body =>
        postJsonRoute {
          Try(Json.parse(body)).map { js =>
            js.validate[Order] match {
              case err: JsError =>
                WrongTransactionJson(err).response
              case JsSuccess(order: Order, _) =>
                if (order.isValid) {
                  val txs = matcher.place(order)
                  JsonResponse(Json.obj("matched" -> txs.map(_.json)), StatusCodes.OK)
                } else InvalidSignature.response
            }
          }.getOrElse(WrongJson.response)
        }
      }
    }
  }

  @Path("/transaction/{address}")
  @ApiOperation(value = "Transactions", notes = "Get transactions to sign", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Json Waves node version")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "address", value = "Address", required = true, dataType = "String", paramType = "path")
  ))
  def getUnsigned: Route = {
    path("transaction" / Segment) { case address =>
      getJsonRoute {
        JsonResponse(Json.obj("version" -> matcher.transactionsToSign(address).map(_.json)), StatusCodes.OK)
      }
    }
  }

  @Path("/transaction/sign")
  @ApiOperation(value = "Sign",
    notes = "Sign matched transaction",
    httpMethod = "POST",
    produces = "application/json",
    consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      value = "Json with data",
      required = true,
      paramType = "body",
      defaultValue = "{\n\t\"transactionId\":\"transactionId\",\n\t\"signature\":\"signature\"\n}"
    )
  ))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Json with response or error")))
  def sign: Route = path("transaction/sign") {
    withCors {
      entity(as[String]) { body =>
        postJsonRoute {
          Try {
            val js = Json.parse(body)
            val signature = Base58.decode((js \ "signature").as[String]).get
            val transactionId = Base58.decode((js \ "transactionId").as[String]).get
            val signedTx = matcher.sign(transactionId, signature)
            //TODO check, whether all signatures are presented
            JsonResponse(Json.obj("signed" -> signedTx.isSuccess), StatusCodes.OK)
          }.getOrElse(WrongJson.response)
        }
      }
    }
  }


}

case object OrderNotFound extends ApiError {
  override val id = 901
  override val code = StatusCodes.BadRequest
  override val message = "Order not found"
}

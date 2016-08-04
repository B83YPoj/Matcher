package com.wavesplatform.http

import javax.ws.rs.Path

import akka.actor.ActorRefFactory
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import com.wavesplatform.http.matcher.Order
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
import scorex.waves.transaction.SignedPayment

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

@Path("/order")
@Api(value = "matcher")
case class MatcherApiRoute(override val application: RunnableApplication)(implicit val context: ActorRefFactory)
  extends ApiRoute with CommonApiFunctions with ScorexLogging {

  override lazy val route =
    pathPrefix("node") {
      place ~ cancel
    }

  @Path("/cancel")
  @ApiOperation(value = "Cancel", notes = "Cancel order", httpMethod = "POST")
  def cancel: Route = path("cancel") {
    postJsonRoute {
      val response = ???
      JsonResponse(response, StatusCodes.OK)
    }
  }


  @Path("/place")
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
      dataType = "com.wavesplatform.http.matcher.Payment",
      defaultValue = "{\n\t\"spendAddress\":\"spendAddress\",\n\t\"spendTokenID\":\"spendTokenID\",\n\t\"receiveTokenID\":\"receiveTokenID\",\n\t\"price\":1,\n\t\"amount\":1,\n\t\"signature\":\"signature\"\n}"
    )
  ))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Json with response or error")))
  def place: Route = path("place") {
    withCors {
      entity(as[String]) { body =>
        postJsonRoute {
          Try(Json.parse(body)).map { js =>
            js.validate[Order] match {
              case err: JsError =>
                WrongTransactionJson(err).response
              case JsSuccess(payment: Order, _) =>
                ???
            }
          }.getOrElse(WrongJson.response)
        }
      }
    }
  }

}

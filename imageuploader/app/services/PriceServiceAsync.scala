package services

import java.util.Calendar

import javax.inject.{Inject, _}
import play.api.Configuration
import play.api.i18n.{Langs, MessagesApi}
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait PriceServiceAsync extends PriceBase {
  def uploadImages(urls: Seq[String]): String
  def salesTrend(): Future[String]
}

@Singleton
class PriceServiceAsyncImpl @Inject()(langs: Langs,
                                 messagesApi: MessagesApi,
                                 config: Configuration,
                                 ws: WSClient
                                ) (implicit ec: ExecutionContext) extends PriceServiceAsync {

  private var sessionId = ""

  override def uploadImages(urls: Seq[String]): String =  {
    val result = ws.url(s"http://localhost:9000${config.get[String]("play.assets.urlPrefix")}/data/metrics.json")
      .addHttpHeaders(Seq(("Content-Type","application/json"), "Charset"->"UTF-8"):_*)
      .addQueryStringParameters(Seq(("search","play")):_*)
      .withRequestTimeout(10000.millis)
      .get().map(res => res.body)


    "xxx"
  }

  override def salesTrend(): Future[String] =  {

    val metricsResponse: Future[String] = for {
      sId <- getSessionIdAsync
      view <- loadViewAsync("performance")
      metrics <- getMetricsAsync(Seq("slsu", "slss"))
    } yield metrics
    metricsResponse

  }

  private def getMetricsAsync(metrics: Seq[String]): Future[String] = {
    val resultFuture: Future[WSResponse] = callAsync("metrics", Seq(
      ("path", "[]"),
      ("columns", Json.stringify(Json.toJson(metrics)))
    ))
    resultFuture.map{ response =>
      val json = Json.parse(response.body)
      Json.stringify(Json.toJson(Map("slss"->json("data")("slss"), "slsu"->json("data")("slsu"))))
    }
  }

  private def callAsync(method: String, postData: Seq[(String,String)]): Future[WSResponse] = {
    val result = ws.url(s"${"xxx"}/${"yyy"}/api")
      .addHttpHeaders(getHeader.toSeq:_*)
      .addQueryStringParameters(getCommParams.toSeq:_*)
      .addQueryStringParameters("method"-> method)
      .withRequestTimeout(10000.millis)
      .post(postData.toMap)
    result
  }

  private def loadViewAsync(view: String): Future[WSResponse] = {
    val resultFuture: Future[WSResponse] = callAsync("loadView", Seq(("id", view)))
    resultFuture
  }

  private def getSessionIdAsync: Future[String] = {
    if (sessionId == null || sessionId.isEmpty) {
      val resultFuture = ws.url(s"${"xxx"}/${"yyy"}/session")
        .withRequestTimeout(10000.millis)
        .get().map { response =>
          val json = Json.parse(response.body)
          sessionId = json("data")("session").as[String]
          sessionId
        }
      resultFuture
    }else{
      Future {sessionId}
    }
  }

  def getCommParams = {
    Map("hsession" -> sessionId,
      "org" -> "yyy",
      "user" -> "zzz")
  }

}

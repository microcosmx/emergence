package services

import java.io._
import java.net.{HttpURLConnection, URL}
import java.util.Calendar

import scala.io.Source
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

    urls.foreach(url => {
      this.downloadFile(url)
    })

    "xxx"
  }

  def downloadFile(url:String) {
    try {

      val urlConn = new URL(url)
      val connection = urlConn.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      val input: InputStream = connection.getInputStream
      val fileToDownloadAs = new java.io.File("data/test.jpg")
      val output: OutputStream = new BufferedOutputStream(new FileOutputStream(fileToDownloadAs))

      val bytes = new Array[Byte](1024) //1024 bytes - Buffer size
      Iterator
        .continually (input.read(bytes))
        .takeWhile (-1 !=)
        .foreach (read=>output.write(bytes,0,read))
      output.close()

    } catch {
      case e => println(e.getMessage)
    }
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

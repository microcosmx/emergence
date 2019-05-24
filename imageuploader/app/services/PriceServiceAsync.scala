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
import scalaj.http.{Http, MultiPart}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait PriceServiceAsync extends PriceBase {
  def uploadImages(urls: Seq[String]): Future[Seq[String]]
}

@Singleton
class PriceServiceAsyncImpl @Inject()(langs: Langs,
                                 messagesApi: MessagesApi,
                                 config: Configuration,
                                 ws: WSClient
                                ) (implicit ec: ExecutionContext) extends PriceServiceAsync {

  private var targetURL = "https://api.imgbb.com/1/upload?key=2fe994af301dd57725f9f1a4ddcd8a5d"

  override def uploadImages(urls: Seq[String]): Future[Seq[String]] =  {

    val result = urls.map(url => Future {
      this.downAndUploadFile(url)
    })

    Future.sequence(result)
  }

  def downAndUploadFile(url:String): String = {
    try {

      val urlConn = new URL(url)
      val connection = urlConn.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      val input: InputStream = connection.getInputStream

      val fileName = urlConn.getFile
      val fileToDownloadAs = new java.io.File(s"data/${fileName.substring(fileName.lastIndexOf("/"))}")

      val output: OutputStream = new BufferedOutputStream(new FileOutputStream(fileToDownloadAs))
      val bytes = new Array[Byte](1024) //1024 bytes - Buffer size
      Iterator
        .continually (input.read(bytes))
        .takeWhile (-1 !=)
        .foreach (read=>output.write(bytes,0,read))
      output.close()

      val result = callAsync(fileToDownloadAs.getName, input)
      val json = Json.parse(result)
      json("data")("url_viewer").toString()

    } catch {
      case e => e.getMessage
    }
  }


  private def callAsync(name: String, input: InputStream) = {
//    val result = ws.url(s"https://api.imgbb.com/1/upload?key=xgzx123")
//        .withBody(Source.fromURL(url).toStream)
//        .execute("POST")

    val bytesInStream = 1024
    val result = Http(targetURL).postMulti(MultiPart("photo", name, "image/png", input, bytesInStream,
      lenWritten => {
        println(s"Wrote $lenWritten bytes out of $bytesInStream total for headshot.png")
      })).asString

    result.body

  }



}

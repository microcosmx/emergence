package services

import java.io._
import java.net.{HttpURLConnection, URL}
import java.util.Calendar

import scala.io.Source
import javax.inject.{Inject, _}
import play.api.{Configuration, Logger}
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

  private var targetURL = "https://api.imgbb.com/1/upload?key=aec45697733b86cd6335cd95b03c223b"

  private val logger = Logger(this.getClass)

  override def uploadImages(urls: Seq[String]): Future[Seq[String]] =  {

    val result = urls.map(url => Future {
      downloadFile(url)
      uploadFile(url)
    })

    Future.sequence(result)
  }

  def downloadFile(url:String) = {
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

    } catch {
      case e => e.getMessage
    }
  }

  def uploadFile(url:String): String = {
    try {

      val urlConn = new URL(url)
      val connection = urlConn.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      val input: InputStream = connection.getInputStream
      val fileName = urlConn.getFile

      val bytes2 = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte).toArray
      logger.info(bytes2.length.toString)
      val result = callAsync2(fileName, bytes2)
      println(result)
      logger.info(result)
      val json = Json.parse(result)
      json("data")("url_viewer").toString()

    } catch {
      case e => e.getMessage
    }
  }


  private def callAsync(name: String, fileBytes: Array[Byte]) = {

    val result = Http(targetURL).postData(fileBytes)
      .header("Content-Type", "application/pdf")
      .header("Content-Transfer-Encoding", "base64")
      .asString

    result.body

  }

  private def callAsync2(name: String, fileBytes: Array[Byte]) = {

    val result = Http(targetURL).postMulti(MultiPart("photo", name, "image/jpeg", fileBytes)).asString
    result.body

  }





}

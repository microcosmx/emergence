package controllers

import javax.inject.{Inject, _}
import play.api.i18n._
import play.api.libs.json._
import play.api.mvc._
import services._
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DashboardController @Inject() (cc: ControllerComponents,
                                     psa: PriceServiceAsync
                                    ) (implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  implicit val timeout: Timeout = 360.seconds

  import java.util.concurrent.atomic.AtomicInteger

  private val counter = new AtomicInteger

  def ping() = Action {
    Ok("server is ready!")
  }

  def uploadImage() = Action.async { implicit request =>
    val urlText = request.body.asText.get
    val urls = Json.parse(urlText)("urls")
    val urlResult: JsResult[Seq[String]] = urls.validate[Seq[String]]

    urlResult match {
      case s: JsSuccess[Seq[String]] => {
        val urls = s.get
        val result = psa.uploadImages(urls)
        Future{
          Ok(Json.obj(
            "jobId" -> counter.incrementAndGet()
          ))
        }
      }
      case e: JsError => Future{
        BadRequest(Json.obj(
          "jobId" -> "Failed"
        ))
      }
    }



  }

  

}

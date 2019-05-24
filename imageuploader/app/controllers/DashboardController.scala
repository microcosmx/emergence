package controllers

import javax.inject.{Inject, _}
import play.api.i18n._
import play.api.libs.json.{JsObject, JsValue, Json}
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

  def ping() = Action {
    Ok("server is ready!")
  }

  def uploadImage() = Action.async { implicit request =>
    Future{
      val preferences = request.body.asFormUrlEncoded.get("preferences").head
      val result = psa.uploadImages(Seq("xxx","xxx1"))
      Ok(Json.obj(
        "jobId" -> result
      ))
    }

  }

  

}

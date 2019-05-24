import play.api.{Configuration, Environment}
import services._
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class Module(environment: Environment,
             configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bind(classOf[PriceServiceAsync]).to(classOf[PriceServiceAsyncImpl])
  }
}

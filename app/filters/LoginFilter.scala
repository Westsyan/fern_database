package filters

import javax.inject.Inject

import akka.stream.Materializer
import controllers.routes
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LoginFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  override def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {

    if (rh.session.get("id").isEmpty && rh.path.contains("/back") && !rh.path.contains("/assets/")) {
        Future.successful(Results.Redirect(routes.IndexController.loginBefore()).flashing("info" -> "请先登录!"))
    } else {
      f(rh)
    }
  }
}

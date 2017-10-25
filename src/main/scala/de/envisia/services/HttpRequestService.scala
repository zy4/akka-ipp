package de.envisia.services

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait HttpRequestService {
  implicit def ec: ExecutionContext
  def execute(request: HttpRequest): Future[HttpResponse]
}

package com.lpc.services

import akka.http.scaladsl.util.FastFuture
import cats.{Applicative, Functor}
import cats.data.EitherT
import play.api.mvc.Results

import scala.concurrent.Future

package object services {
  type AsyncServiceResult[T] = EitherT[Future, ServiceError, T]

  object AsyncServiceResult {

    val Done = Right(())

    def apply()(implicit F: Functor[Future]): AsyncServiceResult[Unit] =
      EitherT.right[ServiceError](FastFuture.successful(()))

    def apply[T](value: Future[Either[ServiceError, T]]): AsyncServiceResult[T] =
      EitherT(value)

    def pure[T](t: T): AsyncServiceResult[T] =
      EitherT[Future, ServiceError, T](FastFuture.successful(Right(t)))

    def action: AsyncServiceResult[Unit] =
      EitherT[Future, ServiceError, Unit](FastFuture.successful(Done))

    def error[T](error: ServiceError)(implicit F: Applicative[Future]): AsyncServiceResult[T] =
      EitherT.leftT[Future, T](error)

    def liftF[T](future: Future[T])(implicit F: Functor[Future]): AsyncServiceResult[T] =
      EitherT.liftF(future)

    def fromOption[T](option: Option[T], ifNone: => ServiceError = NotFoundError())(
      implicit F: Applicative[Future]
    ): AsyncServiceResult[T] = EitherT.fromOption[Future](option, ifNone)

    def fromEither[T](either: Either[ServiceError, T])(implicit F: Applicative[Future]): AsyncServiceResult[T] =
      EitherT.fromEither[Future](either)

  }

  sealed trait ServiceError {
    def statusCode: Results#Status
    def message: String
  }

  case class GenericServiceError(statusCode: Results#Status, message: String) extends ServiceError

  case class NotFoundError(
    statusCode: Results#Status = Results.InternalServerError,
    message: String = "Internal error")
    extends ServiceError
}

package com.lpc.services

import akka.http.scaladsl.util.FastFuture
import cats.{Applicative, Functor, Monad}
import cats.data.{EitherT, OptionT}
import play.api.mvc.Results

import scala.concurrent.{ExecutionContext, Future, blocking}

package object services {
  import cats.implicits._

  type ServiceResult[T] = Either[ServiceError, T]
  type AsyncServiceResult[T] = EitherT[Future, ServiceError, T]
  type AsyncFunction[R] = () => AsyncServiceResult[R]

  object AsyncServiceResult {

    val Done = Right(())

    // TODO: Implement method to convert List[AsyncResult[T]] to AsyncResult[List[T]] using Traverse
    /*
    val list: List[AsyncServiceResult[Int]] = Nil
    val test = Traverse[List].sequence[AsyncServiceResult, Int](list)
     */

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

    def fromOption[T](option: Option[T], ifNone: => ServiceError)(
      implicit F: Applicative[Future]
    ): AsyncServiceResult[T] = EitherT.fromOption[Future](option, ifNone)

    def fromBlocking[T](call: () => T)(implicit ec: ExecutionContext): AsyncServiceResult[T] =
      liftF(Future(blocking(call())))

    def fromEither[T](either: Either[ServiceError, T])(implicit F: Applicative[Future]): AsyncServiceResult[T] =
      EitherT.fromEither[Future](either)

    implicit class FutureAsyncResultOps[T](future: Future[T])(implicit ec: ExecutionContext) extends AnyRef {

      def asAsyncResult: AsyncServiceResult[T] =
        EitherT(future.map[Either[ServiceError, T]](Right(_)))

      def asAction: AsyncServiceResult[Unit] =
        EitherT(future.map[Either[ServiceError, Unit]](_ => Done))

      // TODO: ...
      /*def onValid[D, R](validation: T => ValidationResult[D])(f: D => AsyncServiceResult[R])
        (implicit F: Applicative[Future]): AsyncServiceResult[T]  = {
        future
          .map(validation)
          .flatMap {
            case Valid(data) => f(data)
            case Invalid(errors) => validationError(errors)
          }
      }*/
    }

    implicit class FutureOptionAsAsyncResult[T](futureOpt: Future[Option[T]])(implicit ec: ExecutionContext)
      extends AnyRef {

      def orNotFound(error: ServiceError)(implicit F: Applicative[Future]): AsyncServiceResult[T] =
        OptionT(futureOpt).toRight(error)
    }

    implicit class AsyncResultOptionOps[T](optionResult: Future[Either[ServiceError, Option[T]]])(
      implicit ec: ExecutionContext
    ) extends AnyRef {

      def orNotFound(error: ServiceError): AsyncServiceResult[T] = {
        EitherT(optionResult map {
          case Right(option) =>
            option match {
              case None => Left(error)
              case Some(value) => Right(value)
            }
          case Left(err) => Left(err)
        })
      }
    }

    implicit class CatsAsyncResultOps[T](eitherT: EitherT[Future, ServiceError, T])(
      implicit ec: ExecutionContext
    ) extends AnyRef {

      def logFailure(msg: ServiceError => String)(implicit F: Monad[Future]): AsyncServiceResult[T] = {
        eitherT recoverWith {
          case err =>
            println(msg(err))
            eitherT
        }
      }

      def logInfo(msg: T => String)(implicit F: Monad[Future]): AsyncServiceResult[T] = {
        eitherT flatMap { value =>
          println(msg(value))
          eitherT
        }
      }
    }

    implicit class RichResult[T](result: Either[ServiceError, T])(implicit ec: ExecutionContext)
      extends AnyRef {

      def asAsyncResult(implicit F: Applicative[Future]): AsyncServiceResult[T] =
        EitherT.fromEither[Future](result)

      def logFailure(msg: ServiceError => String): Either[ServiceError, T] = {
        result recoverWith {
          case err =>
            println(msg(err))
            result
        }
      }

      def logInfo(msg: T => String): Either[ServiceError, T] = {
        result flatMap { value =>
          println(msg(value))
          result
        }
      }
    }

    implicit class RichFutureResult[T](result: Future[Either[ServiceError, T]])(
      implicit ec: ExecutionContext
    ) extends AnyRef {

      def recoverLogLeft: Future[Either[ServiceError, T]] = {
        result.recover[Either[ServiceError, T]] {
          case err =>
            println(err.getMessage)
            Left(GenericServiceError(Results.Status(500), err.getMessage))
        }
      }
    }
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

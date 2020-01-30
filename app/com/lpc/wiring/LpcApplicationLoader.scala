package com.lpc.wiring

import com.lpc.controller.ChatController
import com.softwaremill.macwire.wire
import controllers.AssetsComponents
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import router.Routes

class LpcApplicationLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application = new LpcComponent(context).application
}

class LpcComponent (context: Context) extends BuiltInComponentsFromContext(context)
  with GlobalModule
  with AssetsComponents
  with I18nComponents
  with HttpFiltersComponents {
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment, context.initialConfiguration, Map.empty)
  }

  lazy val router: Router = {
    val prefix: String = "/"
    wire[Routes]
  }

  lazy val ChatController: ChatController = wire[ChatController]
}

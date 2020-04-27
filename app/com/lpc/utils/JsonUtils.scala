package com.lpc.utils

import play.api.libs.json.{Json, Writes}

object JsonUtils {
  def stringify[T](`object`: T)(implicit tjs: Writes[T]): String = {
    Json.stringify(Json.toJson(`object`))
  }
}

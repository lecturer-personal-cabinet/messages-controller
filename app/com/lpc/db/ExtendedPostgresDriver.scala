package com.lpc.db

import com.github.tminglei.slickpg._
import com.github.tminglei.slickpg.agg.PgAggFuncSupport
import com.github.tminglei.slickpg.trgm.PgTrgmSupport
import com.github.tminglei.slickpg.window.PgWindowFuncSupport

trait ExtendedPostgresDriver extends ExPostgresProfile
  with PgArraySupport
  with PgAggFuncSupport
  with PgEnumSupport
  with PgTrgmSupport
  with PgPlayJsonSupport
  with PgWindowFuncSupport {
  def pgjson = "jsonb"

  override val api = new API
    with JsonImplicits
    with ArrayImplicits
    with PgTrgmImplicits
    with GeneralAggFunctions
    with WindowFunctions {}
}

object ExtendedPostgresDriver extends ExtendedPostgresDriver
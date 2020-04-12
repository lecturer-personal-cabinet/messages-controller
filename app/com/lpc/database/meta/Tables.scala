package com.lpc.database.meta

import com.lpc.database.meta.mappings.{DialogMessageTable, DialogParticipantTable, DialogTable}
import slick.lifted.TableQuery

object Tables {
  lazy val DialogTable = TableQuery[DialogTable]
  lazy val DialogParticipantTable = TableQuery[DialogParticipantTable]
  lazy val DialogMessageTable = TableQuery[DialogMessageTable]
}

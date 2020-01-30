package com.lpc

import com.lpc.db.Mappings.{DialogMessageTable, DialogTable}
import slick.lifted.TableQuery

package object tables {
  lazy val DialogTable = TableQuery[DialogTable]
  lazy val DialogMessageTable = TableQuery[DialogMessageTable]
}

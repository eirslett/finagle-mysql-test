package com.github.eirslett.finagle.mysql

import org.mockito.Matchers._
import com.twitter.finagle.exp.mysql._
import com.twitter.finagle.exp.mysql.protocol
import java.sql.{Timestamp, Date => SQLDate}
import org.mockito.Matchers._
import scala.Some
import com.twitter.finagle.exp.mysql.Row
import com.twitter.finagle.exp.mysql.Value
import com.twitter.finagle.exp.mysql.StringValue
import com.twitter.finagle.exp.mysql.BooleanValue
import com.twitter.finagle.exp.mysql.ByteValue
import com.twitter.finagle.exp.mysql.ShortValue
import com.twitter.finagle.exp.mysql.IntValue
import com.twitter.finagle.exp.mysql.LongValue
import com.twitter.finagle.exp.mysql.FloatValue
import com.twitter.finagle.exp.mysql.DoubleValue
import com.twitter.finagle.exp.mysql.TimestampValue
import com.twitter.finagle.exp.mysql.DateValue
import com.twitter.finagle.exp.mysql.NullValue
import com.twitter.finagle.exp.mysql.Field
import com.twitter.finagle.exp.mysql.SimpleResultSet

trait MySQLTestHelper {
  def anyRowMapper[T] = any(classOf[Row => T])

  def toValue(a: Any) : Value = a match {
    case s: String => StringValue(s)
    case b: Boolean => BooleanValue(b)
    case b: Byte => ByteValue(b)
    case s: Short => ShortValue(s)
    case i: Int => IntValue(i)
    case l: Long => LongValue(l)
    case f: Float => FloatValue(f)
    case d: Double => DoubleValue(d)
    case t: Timestamp => TimestampValue(t)
    case d: SQLDate => DateValue(d)
    case None => NullValue
  }

  def mockResultSet(fields: Seq[String], rows: Seq[Any]*) = {
    val _fields = fields
    val resultFields = IndexedSeq[Field]()
    val resultRows = (rows map { row : Seq[Any] =>
      new Row {
        val fields = Nil
        val values = (row map toValue).toIndexedSeq

        def indexOf(columnName: String) = {
          val index = _fields.indexOf(columnName)
          index match {
            case -1 => None
            case i: Int => Some(i)
          }
        }
      }
    }).toIndexedSeq

    new SimpleResultSet(resultFields, resultRows)
  }
}

object MySQLTestHelper extends MySQLTestHelper
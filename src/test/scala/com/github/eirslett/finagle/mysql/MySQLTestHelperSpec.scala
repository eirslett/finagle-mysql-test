package com.github.eirslett.finagle.mysql

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import com.twitter.finagle.exp.mysql.{BooleanValue, IntValue, StringValue, Client}
import com.twitter.util.Future
import org.scalatest.matchers.ShouldMatchers

class MySQLTestHelperSpec extends FunSuite with MockitoSugar with ShouldMatchers with MySQLTestHelper  {

  test("Mocking a select query (without testing the rowmapper)") {
    val client = mock[Client]
    when(client.select[String](anyString)(anyRowMapper)).thenReturn(Future(Seq("Chuck Norris")))

    val result = client.select("SELECT name FROM users WHERE id=123") { row =>
      val StringValue(name) = row("name").get
      name
    }
    result.get().head should equal ("Chuck Norris")
  }

  test("Mocking a select query and testing the row mapper (by providing a dummy resultset)") {
    val client = mock[Client]
    when(client.select(anyString)(anyRowMapper)).thenCallRealMethod()
    when(client.query(anyString)).thenReturn(Future(mockResultSet(
      Seq("id", "name", "status", "awake"),
      Seq(0,    "John", "Happy",  true),
      Seq(1,    "Kent", None,     false)
    )))

    case class User(
                     id: Int,
                     name: String,
                     status: Option[String],
                     awake: Boolean)

    val users = client.select("SELECT * FROM users") { row =>
      val IntValue(id) = row("id").get
      val StringValue(name) = row("name").get
      val status : Option[String] = row("status") match {
        case Some(StringValue(s)) => Some(s)
        case _ => None
      }
      val BooleanValue(awake) = row("awake").get
      User(id,name,status,awake)
    }

    val expected = Seq(
      User(id=0, name="John", status=Some("Happy"), awake=true),
      User(id=1, name="Kent", status=None, awake=false)
    )
    users.get() should equal (expected)
  }
}

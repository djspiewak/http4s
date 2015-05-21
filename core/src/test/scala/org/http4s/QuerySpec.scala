package org.http4s

import org.scalacheck.Prop._
import org.scalacheck.Properties


class QuerySpec extends Http4sSpec {

  import Query.KeyValue

  "fromString(query.toString) == query if query.nonEmpty" in prop { query: Query =>
    (query.nonEmpty) ==> (Query.fromString(query.toString) == query)
  }

  "Query Builder" can {
    "build a query from a Query" in prop { query: Query =>
      val b = Query.newBuilder
      query.foreach(b.+=)
      b.result() must_== query
    }

    "build a query from a Seq" in prop { elems: Seq[KeyValue] =>
      val q = (Query.newBuilder ++= elems).result()
      q must beAnInstanceOf[Query]
      q.toSeq must_== elems
    }
  }

  "Query" can {
    val elem = ("k", Some("v"))

    "append a query param" in prop { (p: KeyValue, q: Query) =>
      val q2 = q :+ p
      q2 must beAnInstanceOf[Query]
      q2.length must_== q.length + 1
      q2.last must_== p
      q2.toList must_== (q.toList :+ p)

    }

    "append a single other element" in prop { (s: String, q: Query) =>
      val q2 = q :+ s
      q2.isInstanceOf[Query] must_== false
      q2.length must_== q.length + 1
      q2.last must_== s
      q2.toList must_==(q.toList :+ s)
    }

    "prepend a single Pair element" in prop { (q: Query, elem: KeyValue) =>
      val q2 = elem +: q
      q2.length must_== q.length + 1
      q2 must beAnInstanceOf[Query]
      q2.head must_== elem
      q2.toList must_==(elem::q.toList)

    }

    "prepend a single other element" in prop { (q: Query, elem: String) =>
      val q2 = elem +: q
      q2.length must_== q.length + 1
      q2.head must_== elem
      q2.isInstanceOf[Query] must_== false
      q2.toList must_==(elem::q.toList)
    }

    "append many KeyValue elements" in prop { (q: Query, elems: Seq[KeyValue]) =>
      val q2 = q ++ elems
      q2 must beAnInstanceOf[Query]
      q2.length must_== q.length + elems.length
      q2.toList must_== (q.toList ::: elems.toList)
    }

    "append many other elements" in prop { (q: Query, elems: Seq[String]) =>
      val q2 = q ++ elems
      q2.isInstanceOf[Query] must_== false
      q2.length must_== q.length + elems.length
      q2.toList must_== (q.toList ::: elems.toList)
    }

    "Drop a head element" in prop { q: Query =>
      val q2 = q.drop(1)
      q2 must beAnInstanceOf[Query]
      q2.toList must_== q.toList.drop(1)
    }

    "Drop a tail element" in prop { q: Query =>
      val q2 = q.dropRight(1)
      q2 must beAnInstanceOf[Query]
      q2.toList must_== q.toList.dropRight(1)
    }

    "get a tail for non-empty Query" in prop { q: Query =>
      (q.nonEmpty) ==> (q.tail.toList == q.toList.tail)
    }

    "Encode special chars in the query" in {
      val u = Query("foo" -> Some(" !$&'()*+,;=:/?@~"), "bar" -> Some("biz"))
      u.renderString must_== "foo=%20%21%24%26%27%28%29%2A%2B%2C%3B%3D%3A%2F%3F%40~&bar=biz"
      Query.fromString(u.renderString) must_== u
    }
  }

}

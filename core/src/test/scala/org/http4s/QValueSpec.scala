package org.http4s

// import scalaz.scalacheck.ScalazProperties

import scalaz.syntax.either._

class QValueSpec extends Http4sSpec {
  import QValue._

  // checkAll(ScalazProperties.order.laws[QValue])

  // checkAll(ScalazProperties.equal.laws[QValue])

  "sort by descending q-value" in {
    prop { (x: QValue, y: QValue) =>
      x.thousandths > y.thousandths ==> (x > y)
    }
  }

  "fromDouble should be consistent with fromThousandths" in {
    forall (0 to 1000) { i =>
      fromDouble(i / 1000.0) must_== fromThousandths(i)
    }
  }

  "fromString should be consistent with fromThousandths" in {
    forall (0 to 1000) { i =>
      fromString((i / 1000.0).toString) must_== fromThousandths(i)
    }
  }

  "literal syntax should be consistent with successful fromDouble" in {
    q(1.0).right must_== fromDouble(1.0)
    q(0.5).right must_== fromDouble(0.5)
    q(0.0).right must_== fromDouble(0.0)
    // q(2.0) // doesn't compile: out of range
    // q(0.5 + 0.1) // doesn't compile, not a literal
  }
}

package org.justinhj

// import org.scalatest.flatspec.AnyFlatSpec

// import cats.syntax.applicative._
// import cats.instances.map._
import org.scalacheck.ScalacheckShapeless._
import cats.tests.CatsSuite
import cats.laws.discipline.ApplicativeTests
import org.scalacheck.Arbitrary
import cats.Eq
import FocusedGrid._

class FocusedGridLawTestsCats extends CatsSuite {
  implicit def eqFocusedGrid[A: FocusedGrid]: Eq[FocusedGrid[A]] = Eq.fromUniversalEquals

  //checkAll("FocusedGrid.ApplicativeLaws", ApplicativeTests[FocusedGrid].applicative[Int,Int,Int])
}

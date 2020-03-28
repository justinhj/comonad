package justinhj

import cats.implicits._
import org.scalatest.wordspec.AnyWordSpec
import org.justinhj.{FocusedGridArray => FG}

class FocusedGridArrayTest extends AnyWordSpec {

  val sampleGrid = FG.filledFocusGrid[Int](10, 30, 20)
  val sampleGrid2 = FG.filledFocusGrid[Int](10, 30, 20)
  val sampleGrid3 = FG.filledFocusGrid[Int](11, 30, 20)

  "A FocusedGridArray" can  {
    "eq" should {
      "identify equal correctly" in {
        assert(sampleGrid.eqv(sampleGrid2))
      }
      "identify not equal correctly" in {
        assert(sampleGrid.neqv(sampleGrid3))
      }
    }

    "map" should {
      "not change a grid mapped with identity" in {
        val mapped = sampleGrid.map(identity)
        assert(mapped.eqv(sampleGrid))
      }
      "map correctly" in {
        val mapped = sampleGrid.map(_ + 1)
        assert(mapped.eqv(sampleGrid3))
      }
    }

  }


}

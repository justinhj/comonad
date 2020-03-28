package org.justinhj

import cats._
import cats.implicits._

object FocusedGridArray {

  // FocusedGridArray is a 2d array like Array[Array[A]] and a focus point that is a row
  // and column index into the grid
  case class FocusedGridArray[A](focus: Tuple2[Int, Int], grid: Array[Array[A]])

  // Create a focused grid of the specified size, filling each point with the
  // specified value
  def filledFocusGrid[A](a: A, width: Int, height: Int) = {
    val grid = Array.ofDim[Object](height, width)

    for (
      r <- 0 until height;
      h <- 0 until width
    ) grid(r)(h) = a.asInstanceOf[Object]

    FocusedGridArray((0,0), grid.asInstanceOf[Array[Array[A]]])
  }

  implicit def eqFocusedGridArray[A] = new Eq[FocusedGridArray[A]] {
    def eqv(x: FocusedGridArray[A], y: FocusedGridArray[A]): Boolean = {
      val focusSame = x.focus.eqv(y.focus)
      val elementsSame = x.grid.zip(y.grid).forall {
        case (xGrid, yGrid) =>
          xGrid.sameElements(yGrid)
      }
      focusSame && elementsSame
    }
  }

  // Implementation of Show that gives us a type safe way to display
  // a FocusedGridArray of any type A (which also has a Show instance)
  implicit def focusedGridArrayShow[A: Show] = new Show[FocusedGridArray[A]] {
    def show(fg: FocusedGridArray[A]): String =
      fg.grid
        .map { row =>
          row.iterator.map(_.show).mkString(", ")
        }
        .mkString("\n")
  }

  // Implement Comonad for FocusedGridArray. Note that this gives us map as well since
  // Comonads are Functors
  // Note we're also extending Apply which requires us to implement ap
  implicit val focusedGridArrayComonad = new Comonad[FocusedGridArray] with Applicative[FocusedGridArray] {
    override def map[A, B](fa: FocusedGridArray[A])(f: A => B): FocusedGridArray[B] =  {
      val width = fa.grid(0).size
      val height = fa.grid.size

      val newGrid = Array.ofDim[Object](fa.grid.size, fa.grid(0).size)
      for (
        r <- 0 until height;
        c <- 0 until width
      ) newGrid(r)(c) = f(fa.grid(r)(c)).asInstanceOf[Object]

      FocusedGridArray(fa.focus, newGrid.asInstanceOf[Array[Array[B]]])
    }

    override def coflatten[A](fa: FocusedGridArray[A]): FocusedGridArray[FocusedGridArray[A]] = {
      //val grid = fa.grid.mapWithIndex((row, ri) => row.mapWithIndex((col, ci) => FocusedGridArray((ri, ci), fa.grid)))
      val width = fa.grid(0).size
      val height = fa.grid.size
      val newGrid: Array[Array[FocusedGridArray[A]]] = Array.ofDim(fa.grid.size, fa.grid(0).size)
      for (
        r <- 0 until height;
        c <- 0 until width
      ) newGrid(r)(c) = FocusedGridArray((r, c), fa.grid)

      FocusedGridArray(fa.focus, newGrid)
    }

    // Gives us all of the possible foci for this grid
    def coflatMap[A, B](fa: FocusedGridArray[A])(f: FocusedGridArray[A] => B): FocusedGridArray[B] = {
      val grid: Array[Array[Object]] = coflatten(fa).grid.map(_.map(col => f(col).asInstanceOf[Object]))
      FocusedGridArray(fa.focus, grid.asInstanceOf[Array[Array[B]]])
    }

    // extract simply returns the A at the focus
    def extract[A](fa: FocusedGridArray[A]): A = fa.grid(fa.focus._1)(fa.focus._2)

    def pure[A](a: A): FocusedGridArray[A] = {
      val grid : Array[Array[Object]] = Array(Array(a.asInstanceOf[Object]))
      FocusedGridArray((0,0), grid.asInstanceOf[Array[Array[A]]])
    }

    // This is an optimized map2 that avoids creating intermediate structures
    // by using iterators
    override def map2[A, B, Z](fa: FocusedGridArray[A], fb: FocusedGridArray[B])(f: (A, B) => Z): FocusedGridArray[Z] = {
      assert(fa.grid.size == fb.grid.size)
      assert(fa.grid(0).size == fb.grid(0).size)

      val width = fa.grid(0).size
      val height = fa.grid.size
      val newGrid: Array[Array[Object]] = Array.ofDim(fa.grid.size, fa.grid(0).size)
      for (
        r <- 0 until height;
        c <- 0 until width
      ) newGrid(r)(c) = f(fa.grid(r)(c), fb.grid(r)(c)).asInstanceOf[Object]

      FocusedGridArray(fa.focus, newGrid.asInstanceOf[Array[Array[Z]]])
    }

    // Note that we have an efficient implementation of map2 for this type class
    // instance, so I use that to implement ap
    def ap[A, B](fab: FocusedGridArray[A => B])(fa: FocusedGridArray[A]): FocusedGridArray[B] = {
      map2(fab, fa){
        (f,a) =>
          f(a)
      }
    }
  }
}

package org.justinhj

import cats._
import cats.implicits._

object FocusedGrid {

  // FocusedGrid is a 2d array like Vector[Vector[A]] and a focus point that is a row
  // and column index into the grid
  case class FocusedGrid[A](focus: Tuple2[Int, Int], grid: Vector[Vector[A]])

  // Create a focused grid of the specified size, filling each point with the
  // specified value
  def filledFocusGrid[A](a: A, width: Int, height: Int) = {
    val row = Vector.fill(width)(a)
    FocusedGrid((0,0), Vector.fill(height)(row))
  }

  implicit def eqFocusedGrid[A: FocusedGrid]: Eq[FocusedGrid[A]] = Eq.fromUniversalEquals

  // Implementation of Show that gives us a type safe way to display
  // a FocusedGrid of any type A (which also has a Show instance)
  implicit def focusedGridShow[A: Show] = new Show[FocusedGrid[A]] {
    def show(fg: FocusedGrid[A]): String =
      fg.grid
        .map { row =>
          row.iterator.map(_.show).mkString(", ")
        }
        .mkString("\n")
  }

  // Implement Comonad for FocusedGrid. Note that this gives us map as well since
  // Comonads are Functors
  // Note we're also extending Apply which requires us to implement ap
  implicit val focusedGridComonad = new Comonad[FocusedGrid] with Applicative[FocusedGrid]  {
    override def map[A, B](fa: FocusedGrid[A])(f: A => B): FocusedGrid[B] =  {
      FocusedGrid(fa.focus, fa.grid.map(row => row.map(a => f(a))))
    }

    override def coflatten[A](fa: FocusedGrid[A]): FocusedGrid[FocusedGrid[A]] = {
      val grid = fa.grid.mapWithIndex((row, ri) => row.mapWithIndex((col, ci) => FocusedGrid((ri, ci), fa.grid)))
      FocusedGrid(fa.focus, grid)
    }

    // Gives us all of the possible foci for this grid
    def coflatMap[A, B](fa: FocusedGrid[A])(f: FocusedGrid[A] => B): FocusedGrid[B] = {
      val grid = coflatten(fa).grid.map(_.map(col => f(col)))
      FocusedGrid(fa.focus, grid)
    }

    // extract simply returns the A at the focus
    def extract[A](fa: FocusedGrid[A]): A = fa.grid(fa.focus._1)(fa.focus._2)

    def pure[A](a: A): FocusedGrid[A] = FocusedGrid((0,0), Vector(Vector(a)))

    // Ap is required for Applicative so we get map2, product and more
    def ap[A, B](ff: FocusedGrid[A => B])(fa: FocusedGrid[A]): FocusedGrid[B] = {
      val newGrid = ff.grid.mapWithIndex {
        (row, i) =>
          row.zip(fa.grid(i)).map {
            case (f, a) =>
              f(a)
          }
      }
      FocusedGrid(ff.focus, newGrid)
    }
  }
}

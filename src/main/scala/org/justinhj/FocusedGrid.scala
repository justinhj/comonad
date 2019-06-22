package org.justinhj

import cats._
import cats.implicits._

object FocusedGrid {

  // FocusedGrid is a 2d array like Vector[Vector[A]] and a focus point that is a row
  // and column index into the grid
  case class FocusedGrid[A](focus: Tuple2[Int,Int], grid : Vector[Vector[A]])
  
  // Implementation of Show that gives us a type safe way to display 
  // a FocusedGrid of any type A (which also has a Show instance)
  implicit def focusedGridShow[A : Show] = new Show[FocusedGrid[A]] {
    def show(fg: FocusedGrid[A]): String = {
      fg.grid.map{
        row => row.iterator.map(_.show).mkString("")
      }.mkString("\n")
    }
  }

  // Implement Comonadfor FocusedGrid
  implicit val focusedGridComonad = new Comonad[FocusedGrid] {
    override def map[A, B](fa: FocusedGrid[A])(f: A => B) : FocusedGrid[B] = {
      FocusedGrid(fa.focus, fa.grid.map(row => row.map(a => f(a))))
    }

    override def coflatten[A](fa: FocusedGrid[A]): FocusedGrid[FocusedGrid[A]] = {
      val grid = fa.grid.mapWithIndex((row, ri) => 
        row.mapWithIndex((col, ci) => 
          FocusedGrid((ri,ci), fa.grid)))
      FocusedGrid((0,0), grid)
    }

    // Gives us all of the possible foci for this grid
    def coflatMap[A, B](fa: FocusedGrid[A])(f: FocusedGrid[A] => B): FocusedGrid[B] = {
     val grid = coflatten(fa).grid.map(_.map(col => f(col)))
      FocusedGrid(fa.focus,  grid)
    }

    // extract simply returns the A at the focus
    def extract[A](fa: FocusedGrid[A]): A = fa.grid(fa.focus._1)(fa.focus._2)
  }
}

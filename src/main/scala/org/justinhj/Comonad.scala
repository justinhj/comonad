package org.justinhj

// Created by https://github.com/justinhj/fp-starter-pack.g8
// Comonad example

import cats._
import cats.data._
import cats.implicits._
import cats.instances._

object Comonad {

  // // NonEmptyPrevNextList augments NonEmptyList with the ability to get
  // // previous and next elements of a list (as options)
  // case class NonEmptyPrevNextList[A](nel: NonEmptyList[A])

  // A 2d array like Array[Array[A]] and a focus point 0,0
  // extract gives the focus point
  // duplicate gives all the foci
  // so we can use coflatmap to get the sum of the neighbours etc

  case class FocusedGrid[A](focus: Tuple2[Int,Int], grid : Vector[Vector[A]])

  // Get the sum of the focus and the values around it
  def localSum(fg : FocusedGrid[Int]) : Int = {
    def getAt(point : Tuple2[Int,Int]) : Int = {
      val what = fg.grid.get(point._1).flatMap(row => row.get(point._2))
      what.getOrElse(0)
    }
    
    getAt((fg.focus._1 - 1, fg.focus._2 - 1)) +
    getAt((fg.focus._1, fg.focus._2 - 1)) +
    getAt((fg.focus._1 + 1, fg.focus._2 - 1)) +
    getAt((fg.focus._1 - 1, fg.focus._2)) +
    getAt((fg.focus._1, fg.focus._2)) +
    getAt((fg.focus._1 + 1, fg.focus._2)) +
    getAt((fg.focus._1 - 1, fg.focus._2 + 1)) +
    getAt((fg.focus._1, fg.focus._2 + 1)) +
    getAt((fg.focus._1 + 1, fg.focus._2 + 1))
  }
  
  implicit val focusedGridComonad = new Comonad[FocusedGrid] {

    override def map[A, B](fa: FocusedGrid[A])(f: A => B) : FocusedGrid[B] = {
      FocusedGrid(fa.focus, fa.grid.map(row => row.map(a => f(a))))
    }

    // coflatmap for Nel creates a Nel where each element is a Nel which kinda makes sense
    // so we must do the same for coflatten

    override def coflatten[A](fa: FocusedGrid[A]): FocusedGrid[FocusedGrid[A]] = {
      val grid = fa.grid.mapWithIndex((row, ri) => 
        row.mapWithIndex((col, ci) => 
          FocusedGrid((ri,ci), fa.grid)))
      FocusedGrid((0,0), grid)
    }

    // Gives us all of the possible foci for this grid
    def coflatMap[A, B](fa: FocusedGrid[A])(f: FocusedGrid[A] => B): FocusedGrid[B] = {
      val thing = coflatten(fa)

      val grid = thing.grid.map(row => 
        row.map(col => f(col)))
      FocusedGrid(fa.focus,  grid)
    }

    // extract simply returns the A at the focus
    def extract[A](fa: FocusedGrid[A]): A = fa.grid(fa.focus._1)(fa.focus._2)

  }

  val g1 = Vector[Int](1,2,3)
  val g2 = Vector[Int](4,5,6)
  val g3 = Vector[Int](7,8,9)

  val fg1 = FocusedGrid((1,1), Vector(g1,g2,g3))

  //@ fg1.coflatMap(localSum(_)) 
  // LOL!
  //res6: FocusedGrid[Int] = FocusedGrid((1, 1), Vector(Vector(12, 21, 16), Vector(27, 45, 33), Vector(24, 39, 28)))

  def main(args : Array[String]) : Unit = {
    println("Hello, mate!")


    // functor 
    println(fg1.map(f => f + 1))
  }

}

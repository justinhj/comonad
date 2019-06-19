package org.justinhj

// Created by https://github.com/justinhj/fp-starter-pack.g8
// Comonad example

import cats._
import cats.data._
import cats.implicits._
import cats.instances._
//import cats.syntax.show._

object Comonad {

  // // NonEmptyPrevNextList augments NonEmptyList with the ability to get
  // // previous and next elements of a list (as options)
  // case class NonEmptyPrevNextList[A](nel: NonEmptyList[A])

  // A 2d array like Array[Array[A]] and a focus point 0,0
  // extract gives the focus point
  // duplicate gives all the foci
  // so we can use coflatmap to get the sum of the neighbours etc

  case class FocusedGrid[A](focus: Tuple2[Int,Int], grid : Vector[Vector[A]])

  def getAt(fg : FocusedGrid[Int], point : Tuple2[Int,Int]) : Int = {
    fg.grid.get(point._1).flatMap(row => row.get(point._2)).getOrElse(0)
  }

  // Get the sum of the values around the focus
  def localSum(fg : FocusedGrid[Int]) : Int = {  
    getAt(fg, (fg.focus._1 - 1, fg.focus._2 - 1)) +
    getAt(fg, (fg.focus._1, fg.focus._2 - 1)) +
    getAt(fg, (fg.focus._1 + 1, fg.focus._2 - 1)) +
    getAt(fg, (fg.focus._1 - 1, fg.focus._2)) +
    //getAt((fg.focus._1, fg.focus._2)) +
    getAt(fg, (fg.focus._1 + 1, fg.focus._2)) +
    getAt(fg, (fg.focus._1 - 1, fg.focus._2 + 1)) +
    getAt(fg, (fg.focus._1, fg.focus._2 + 1)) +
    getAt(fg, (fg.focus._1 + 1, fg.focus._2 + 1))
  }
  
  implicit def focusedGridShow[A : Show] = new Show[FocusedGrid[A]] {
    def show(fg: FocusedGrid[A]): String = {
      val wai = fg.grid.map{
        row => 
          row.iterator.map(_.show).mkString(" ")
      }
      wai.mkString("\n")
    }
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

  val blinker = Vector(
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,1,1,1,0),
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,0,0,0,0))

  val b1 = FocusedGrid((0,0), blinker)

  def conwayStep(fg: FocusedGrid[Int]) : Int = {
    val liveNeighbours = localSum(fg)
    val live = getAt(fg, fg.focus)

    if(live == 1) {
      if(liveNeighbours >= 2 && liveNeighbours <=3) 1 else 0
    }
    else {
      if(liveNeighbours == 3) 1 else 0 
    }
  }

  //@ fg1.coflatMap(localSum(_)) 
  // LOL!
  //res6: FocusedGrid[Int] = FocusedGrid((1, 1), Vector(Vector(12, 21, 16), Vector(27, 45, 33), Vector(24, 39, 28)))

  def main(args : Array[String]) : Unit = {

    println(b1.show)

    println(b1.coflatMap(conwayStep).show)
  }

}

package justinhj

// Created by https://github.com/justinhj/fp-starter-pack.g8
// Comonad example

import cats._
import cats.data._
import cats.implicits._
import cats.instances._

object Comonad {

  // NonEmptyPrevNextList augments NonEmptyList with the ability to get
  // previous and next elements of a list (as options)
  case class NonEmptyPrevNextList[A](nel: NonEmptyList[A])

  // A 2d array like Array[Array[A]] and a focus point 0,0
  // extract gives the focus point
  // duplicate gives all the foci
  // so we can use coflatmap to get the sum of the neighbours etc

  case class FocusedGrid[A](focus: Tuple2[Int,Int], grid : Array[Array[A]])

  trait FocusedGridFunctor extends Functor[FocusedGrid] {
    def map[A, B](fa: FocusedGrid[A])(f: A => B) : FocusedGrid[B] = {
      //val newGrid : Array[Array[B]] = ???
      FocusedGrid(fa.focus, Functor[Array].map(row => row.map(a => f(a))) )
      ???
    }
  }

  implicit def focusedGridComonad = new Comonad[FocusedGrid] with FocusedGridFunctor {

    def coflatMap[A, B](fa: justinhj.Comonad.FocusedGrid[A])(f: justinhj.Comonad.FocusedGrid[A] => B): justinhj.Comonad.FocusedGrid[B] = ???

    def extract[A](x: justinhj.Comonad.FocusedGrid[A]): A = ???

  }

  val g1 = Array[Int](1,2,3)
  val g2 = Array[Int](4,5,6)
  val g3 = Array[Int](7,8,9)

  val fg1 = FocusedGrid((1,1), Array(g1,g2,g3))



  def main(args : Array[String]) : Unit = {
    println("Hello, mate!")
  }

}

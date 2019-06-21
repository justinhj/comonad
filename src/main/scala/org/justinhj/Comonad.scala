package org.justinhj

// Created by https://github.com/justinhj/fp-starter-pack.g8
// Comonad example

import cats._
import cats.implicits._

object Comonad {

  // FocusedGrid is a 2d array like Array[Array[A]] and a focus point that is a row
  // and column index into the grid
  case class FocusedGrid[A](focus: Tuple2[Int,Int], grid : Vector[Vector[A]])

  def getAt(fg : FocusedGrid[Int], point : Tuple2[Int,Int]) : Int = {
    val row = if(point._1 >= fg.grid.size)
                point._1 - fg.grid.size
              else {
                if(point._1 < 0)
                  point._1 + fg.grid.size
                else
                  point._1
              }
    val col = if(point._2 >= fg.grid(0).size)
                point._2 - fg.grid(0).size
              else {
                if(point._2 < 0)
                  point._2 + fg.grid(0).size
                else
                  point._2
              }
    val wrapped = (row, col)
    fg.grid.get(wrapped._1).flatMap(row => row.get(wrapped._2)).getOrElse(0)
  }

  // Get the sum of the values around the focus
  def localSum(fg : FocusedGrid[Int]) : Int = {  

    val points = List(-1,0,1)
    (points,points).mapN{case (a : Tuple2[Int,Int]) => identity(a)}.filter{
      case (0,0) => false
      case _ => true
    }.map(coord => getAt(fg, coord |+| fg.focus)).sum
  }
  
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

  val blinker = Vector(
    Vector[Int](0,1,1,1,0),
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,1,1,1,0),
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,1,1,1,0),
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,0,0,0,0),
    Vector[Int](0,0,0,0,0))

  val glider = Vector(
      Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
      Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))

    val dieHard = Vector(
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,1,0,0,0,1,1,1,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
        Vector[Int](0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))

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

  // Convert the digits of the FocusedGrid to more pleasing characters
  def prettify(i : Int) : Char = {
    i match {
      case 1 => 0x2593.toChar
      case 0 => 0x2591.toChar
    }
  }

  def ansiMoveUp(n : Int) = s"\u001b[${n}A"

  def animate(start: FocusedGrid[Int], steps: Int) : Unit = {

    println(start.map(a => prettify(a)).show)

    Thread.sleep(120)
    
    if(steps > 0) {
      print(ansiMoveUp(start.grid.size))
      animate(start.coflatMap(conwayStep), steps - 1)
    }
  }

  def main(args : Array[String]) : Unit = {

    //val b = FocusedGrid((0,0), blinker)
    //val b = FocusedGrid((0,0), glider)
    val b = FocusedGrid((0,0), dieHard)

    animate(b, 135)
  }

}

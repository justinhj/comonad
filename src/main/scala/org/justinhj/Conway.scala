package org.justinhj

// This project was created by https://github.com/justinhj/fp-starter-pack.g8
// Implementation of Conway's game of life with Comonads

import cats._
import cats.implicits._

object Conway {

  import FocusedGrid._

  def getAt(fg: FocusedGrid[Int], point: Tuple2[Int, Int]): Int = {
    val row = if (point._1 >= 0) point._1 % fg.grid.size else ((point._1 % fg.grid.size) + fg.grid.size)
    val col = if (point._2 >= 0) point._2 % fg.grid(0).size else ((point._2 % fg.grid(0).size) + fg.grid(0).size)
    val wrapped = (row, col)
    fg.grid.get(wrapped._1).flatMap(row => row.get(wrapped._2)).getOrElse(0)
  }

  // Get the sum of the values around the focus
  def localSum(fg: FocusedGrid[Int]): Int = {
    val points = List(-1, 0, 1)
    Applicative[List]
      .map2(points, points) { case (a: Tuple2[Int, Int]) => identity(a) }
      .filter {
        case (0, 0) => false
        case _      => true
      }
      .map(coord => getAt(fg, coord |+| fg.focus))
      .sum
  }

  val blinker = Vector(
    Vector[Int](0, 1, 1, 1, 0),
    Vector[Int](0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0),
    Vector[Int](0, 1, 1, 1, 0),
    Vector[Int](0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0),
    Vector[Int](0, 1, 1, 1, 0),
    Vector[Int](0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0)
  )

  val glider = Vector(
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
  )

  val dieHard = Vector(
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    Vector[Int](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
  )

  def conwayStep(fg: FocusedGrid[Int]): Int = {
    val liveNeighbours = localSum(fg)
    val live = getAt(fg, fg.focus)

    if (live == 1) {
      if (liveNeighbours >= 2 && liveNeighbours <= 3) 1 else 0
    } else {
      if (liveNeighbours == 3) 1 else 0
    }
  }

  // Convert the digits of the FocusedGrid to more pleasing characters
  def prettify(i: Int): Char =
    i match {
      case 1 => 0x2593.toChar
      case 0 => 0x2591.toChar
    }

  def ansiMoveUp(n: Int) = s"\u001b[${n}A"

  def animate(start: FocusedGrid[Int], steps: Int): Unit = {
    println(start.map(a => prettify(a)).show)

    Thread.sleep(80)

    if (steps > 0) {
      print(ansiMoveUp(start.grid.size))
      animate(start.coflatMap(conwayStep), steps - 1)
    }
  }

  def main(args: Array[String]): Unit = {
    // Simple blinker example
    //val b = FocusedGrid((0,0), blinker)

    // Three gliders flying together
    val b = FocusedGrid((0, 0), glider)

    // Diehard takes 130 steps to stabilize
    //val b = FocusedGrid((0,0), dieHard)

    animate(b, 135)
  }

}

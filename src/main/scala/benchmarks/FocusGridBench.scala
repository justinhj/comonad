package benchmarks

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._
import org.justinhj.{FocusedGrid => FG}
import cats.Applicative

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
class FocusedGridBench {

  var smallImage: FG.FocusedGrid[(Int, Int, Int)] = _
  var largeImage: FG.FocusedGrid[(Int, Int, Int)] = _

  def blend(a: (Int, Int, Int), b: (Int, Int, Int)): (Int, Int, Int) =
    ((a._1 + b._1) / 2, (a._2 + b._2) / 2, (a._3 + b._3) / 2)

  def blend2(a: (Int, Int, Int))(b: (Int, Int, Int)): (Int, Int, Int) =
    ((a._1 + b._1) / 2, (a._2 + b._2) / 2, (a._3 + b._3) / 2)

  @Setup
  def setup(): Unit = {
    smallImage = FG.filledFocusGrid((128,128,128),50,50)
    largeImage = FG.filledFocusGrid((128,128,128),2000,2000)
  }

  // Map2 is written using iterators and Vector builders
  @Benchmark
  def withMap2Small = Applicative[FG.FocusedGrid].map2(smallImage, smallImage)(blend _)

  @Benchmark
  def withMap2Large = Applicative[FG.FocusedGrid].map2(largeImage, largeImage)(blend _)

  // Use the slow ap
  @Benchmark
  def withSlowApSmall = {
    val ff = FG.filledFocusGrid(blend2 _, smallImage.grid(0).size, smallImage.grid.size)
    val step1 = FG.focusedGridComonad.apSlow(ff)(smallImage)
    FG.focusedGridComonad.apSlow(step1)(smallImage)
  }

  @Benchmark
  def withSlowApLarge = {
    val ff = FG.filledFocusGrid(blend2 _, largeImage.grid(0).size, largeImage.grid.size)
    val step1 = FG.focusedGridComonad.apSlow(ff)(largeImage)
    FG.focusedGridComonad.apSlow(step1)(largeImage)
  }

}
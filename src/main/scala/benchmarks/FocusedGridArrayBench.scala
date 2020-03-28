package benchmarks

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._
import org.justinhj.{FocusedGridArray => FG}
import cats.Applicative

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
class FocusedGridArrayBench {

  var smallImage: FG.FocusedGridArray[(Int, Int, Int)] = _
  var largeImage: FG.FocusedGridArray[(Int, Int, Int)] = _

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
  def withMap2Small = Applicative[FG.FocusedGridArray].map2(smallImage, smallImage)(blend _)

  @Benchmark
  def withMap2Large = Applicative[FG.FocusedGridArray].map2(largeImage, largeImage)(blend _)
}
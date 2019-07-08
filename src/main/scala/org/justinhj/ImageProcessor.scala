package org.justinhj

// Comonad example to do some image processing

import cats._, data._
import cats.implicits._
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

object ImageProcessor {

  import FocusedGrid._

  // Safely get the value at the focus, returning a default value if we go off the grid
  def getAt(fg: FocusedGrid[(Int, Int, Int)], point: Tuple2[Int, Int], default: (Int, Int, Int)): (Int, Int, Int) =
    fg.grid.get(point._1).flatMap(row => row.get(point._2)).getOrElse(default)

  // Get the sum of the values around the focus
  def localSum(fg: FocusedGrid[(Int, Int, Int)], default: (Int, Int, Int), width: Int): (Int, Int, Int) = {
    val points = (-width / 2 to width / 2).toList
    Applicative[List].map2(points, points) { case (r, c) => getAt(fg, (r, c) |+| fg.focus, default) }.combineAll
  }

// Copies the image data from the focused grid back to the image
// Note the image is mutable here
  def focusedGridToImage(fg: FocusedGrid[(Int, Int, Int)]): BufferedImage = {
    val w = fg.grid(0).size
    val h = fg.grid.size

    // Create a new image
    val img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)

    for (x <- 0 until w)
      for (y <- 0 until h) {
        val rgb = fg.grid(y)(x)
        img.setRGB(x, y, rgb._1 << 16 | rgb._2 << 8 | rgb._3)
      }
    img
  }

  def colourToTuple(rgb: Int): (Int, Int, Int) = {
    val red = (rgb >> 16) & 0x000000FF
    val green = (rgb >> 8) & 0x000000FF
    val blue = (rgb) & 0x000000FF

    (red, green, blue)
  }

  def imageToFocusedGrid(img: BufferedImage): FocusedGrid[(Int, Int, Int)] = {
    val w = img.getWidth
    val h = img.getHeight

    val grid = (0 until h).foldLeft(Vector.empty[Vector[(Int, Int, Int)]]) {
      case (acc1, y) =>
        acc1 :+ (0 until w).foldLeft(Vector.empty[(Int, Int, Int)]) {
          case (acc, x) =>
            acc :+ colourToTuple(img.getRGB(x, y))
        }
    }

    FocusedGrid((0, 0), grid)
  }

  def boxFilter(width: Int): FocusedGrid[(Int, Int, Int)] => (Int, Int, Int) = { fg =>
    val widthSqr = width * width
    val sum = localSum(fg, (255, 255, 255), width)
    ((sum._1 / widthSqr).toInt, (sum._2 / widthSqr).toInt, (sum._3 / widthSqr).toInt)
  }

  def mirrorHorizontal(fg: FocusedGrid[(Int, Int, Int)]): (Int, Int, Int) = {
    val mirrorX = (fg.grid(0).size - 1) - fg.focus._2
    fg.grid(fg.focus._1)(mirrorX)
  }

  def mirrorVertical(fg: FocusedGrid[(Int, Int, Int)]): (Int, Int, Int) = {
    val mirrorY = (fg.grid.size - 1) - fg.focus._1
    fg.grid(mirrorY)(fg.focus._2)
  }

  def identity(fg: FocusedGrid[(Int, Int, Int)]): (Int, Int, Int) = {
    fg.extract
  }
  
  def compose[A,B,C,D](fg : FocusedGrid[A], f1 : FocusedGrid[A] => B, f2 : FocusedGrid[A] => C)(cf : (B,C) => D) : D = {
    val v1 = f1(fg)
    val v2 = f2(fg)
    cf(v1, v2)
  }

  def blend(a : (Int, Int, Int), b: (Int, Int, Int)) : (Int, Int, Int) = {
    ((a._1 + b._1)/2,
      (a._2 + b._2)/2,
      (a._3 + b._3)/2)  
  }

  def blendTuple(a : ((Int, Int, Int), (Int, Int, Int))) : (Int, Int, Int) = {
    ((a._1._1 + a._2._1)/2,
      (a._1._2 + a._2._2)/2,
      (a._1._3 + a._2._3)/2)  
  }

  def main(args: Array[String]): Unit = {
    val image = ImageIO.read(new File("./images/girl.png"))

    val originalImage = imageToFocusedGrid(image)

    // Use a box filter to blur then mirror by doing two coflatmaps in sequence
    val processed = originalImage.coflatMap(boxFilter(9)).coflatMap(mirrorHorizontal)
    val processedImage = focusedGridToImage(processed)
    ImageIO.write(processedImage, "png", new File("./images/mirrorandblur.png"))

    // Blend the mirror and identity functions together
    val composedProcess = originalImage.coflatMap(fg => compose(fg, mirrorHorizontal, identity)(blend))
    val processedComposedImage = focusedGridToImage(composedProcess)
    ImageIO.write(processedComposedImage, "png", new File("./images/mirrorandidentity.png"))

    // Same as previous but encode as a Cokleisli
    val ck1 = Cokleisli(mirrorVertical)
    val ck2 = Cokleisli(mirrorHorizontal)

    val ck1ck2Compose = ck1.
                          product(ck2).
                          map(blendTuple)

    val ck1ck2ComposedProcess = originalImage.coflatMap(ck1ck2Compose.run)
    val ck1ck2ComposedProcessImage = focusedGridToImage(ck1ck2ComposedProcess)
    
    ImageIO.write(ck1ck2ComposedProcessImage, "png", new File("./images/ck1ck2mirrorandidentity.png"))
  }
}

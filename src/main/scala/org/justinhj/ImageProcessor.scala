package org.justinhj

// This project was created by https://github.com/justinhj/fp-starter-pack.g8
// Comonad example to do some image processing

import cats._
import cats.implicits._
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import cats.data.Cokleisli

object ImageProcessor {

  import FocusedGrid._

  // Safely get the value at the focus, returning a default value if we go off the grid
  def getAt(fg : FocusedGrid[(Int,Int,Int)], point : Tuple2[Int,Int], default: (Int,Int,Int)) : (Int,Int,Int) = {
    fg.grid.get(point._1).flatMap(row => row.get(point._2)).getOrElse(default)
  }

  // Get the sum of the values around the focus
  def localSum(fg : FocusedGrid[(Int,Int,Int)], default: (Int,Int,Int), width: Int) : (Int,Int,Int) = {  
    val points = (-width/2 to width/2).toList
    Applicative[List].map2(points,points){case (r,c) => getAt(fg, (r,c) |+| fg.focus, default)}.combineAll
  }

// Copies the image data from the focused grid back to the image
// Note the image is mutable here
def focusedGridToImage(fg : FocusedGrid[(Int,Int,Int)], img: BufferedImage) : Unit = {
  val w = img.getWidth
  val h = img.getHeight

  require(fg.grid.size == h)
  require(fg.grid(0).size == w)

  for (x <- 0 until w)
    for (y <- 0 until h) {
      val rgb = fg.grid(y)(x)
      img.setRGB(x,y,rgb._1 << 16 | rgb._2 << 8 | rgb._3)
    }
}

def colourToTuple(rgb: Int) : (Int,Int,Int) = {
  val red = (rgb >> 16) & 0x000000FF
  val green = (rgb >>8 ) & 0x000000FF
  val blue = (rgb) & 0x000000FF

  (red, green, blue)
}

def imageToFocusedGrid(img: BufferedImage) : FocusedGrid[(Int,Int,Int)] = {
  val w = img.getWidth
  val h = img.getHeight

  val grid = (0 until h).foldLeft(Vector.empty[Vector[(Int,Int,Int)]]) {
    case (acc1, y) =>
      acc1 :+ (0 until w).foldLeft(Vector.empty[(Int,Int,Int)]) {
      case (acc, x) =>
        acc :+ colourToTuple(img.getRGB(x,y))
    }
  }

  FocusedGrid((0,0), grid)
}   

def processImage(img: BufferedImage, f: FocusedGrid[(Int,Int,Int)] => (Int,Int,Int)): BufferedImage = {
  val w = img.getWidth
  val h = img.getHeight

  // create new image of the same size
  val out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)

  // Copy the image data from the original
  for (x <- 0 until w)
    for (y <- 0 until h) {
      val rgb = img.getRGB(x,y)
      out.setRGB(x,y,rgb)
    }

  val fg = imageToFocusedGrid(out)
  val processed = fg.coflatMap(f)
  focusedGridToImage(processed, out)

  out
}
  def main(args : Array[String]) : Unit = {

    val photo1 = ImageIO.read(new File("./images/noisygirl.png"))

    println(photo1.getHeight())
        
    def boxFilter(width: Int) : FocusedGrid[(Int,Int,Int)] => (Int,Int,Int) = {
      fg => 
        val widthSqr = width * width
        val sum = localSum(fg, (255,255,255), width)
        ((sum._1 / widthSqr).toInt, (sum._2 / widthSqr).toInt, (sum._3 / widthSqr).toInt)
    }

    def mirrorHorizontal(fg: FocusedGrid[(Int,Int,Int)]) : (Int,Int,Int) = {
      val mirrorX = (fg.grid(0).size - 1) - fg.focus._2
      fg.grid(fg.focus._1)(mirrorX)
    }

    // val bf = Cokleisli[FocusedGrid, (Int,Int,Int), (Int,Int,Int)](fg => boxFilter(fg))
    // val mh = Cokleisli[FocusedGrid, (Int,Int,Int), (Int,Int,Int)](fg => mirrorHorizontal(fg))

    // val composed = bf.andThen(mh)

    // val photo2 = processImage(photo1, composed.run) 

    //val photo2 = processImage(photo1, mirrorHorizontal) 

    val photo2 = processImage(photo1, boxFilter(9))

    ImageIO.write(photo2, "png", new File("./images/noisygirlsmoothed.png"))

  }

}

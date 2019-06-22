package org.justinhj

// This project was created by https://github.com/justinhj/fp-starter-pack.g8
// Comonad example to do some image processing

import cats._
import cats.implicits._
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

object ImageProcessor {

  import FocusedGrid._

  // Safely get the value at the focus, returning a default value if we go off the grid
  def getAt(fg : FocusedGrid[Int], point : Tuple2[Int,Int], default: Int) : Int = {
    fg.grid.get(fg.focus._1).flatMap(row => row.get(fg.focus._2)).getOrElse(default)
  }

  // Get the sum of the values around the focus
  def localSum(fg : FocusedGrid[Int], default: Int) : Int = {  
    val points = List(-1,0,1)
    Applicative[List].map2(points,points){case (a : Tuple2[Int,Int]) => identity(a)}.
      map(coord => getAt(fg, coord |+| fg.focus, default)).sum
  }

// Copies the image data from the focused grid back to the image
// Note the image is mutable here
def focusedGridToImage(fg : FocusedGrid[Int], img: BufferedImage) : Unit = {
  val w = img.getWidth
  val h = img.getHeight

  require(fg.grid.size == h)
  require(fg.grid(0).size == w)

  for (x <- 0 until w)
    for (y <- 0 until h) {
      img.setRGB(x,y,fg.grid(y)(x))
    }
}

def imageToFocusedGrid(img: BufferedImage) : FocusedGrid[Int] = {
  val w = img.getWidth
  val h = img.getHeight

  val grid = (0 until h).foldLeft(Vector.empty[Vector[Int]]) {
    case (acc1, y) =>
      acc1 :+ (0 until w).foldLeft(Vector.empty[Int]) {
      case (acc, x) =>
        acc :+ (img.getRGB(x,y) & 0xff)
    }
  }

  FocusedGrid((0,0), grid)
}   

def processImage(img: BufferedImage, f: FocusedGrid[Int] => Int): BufferedImage = {
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

  // Convert the image to a grey scale 
  for (x <- 0 until w)
    for (y <- 0 until h) {
      val rgb = img.getRGB(x,y)
      val red = (((rgb >> 16) & 0x000000FF) * 0.3).toInt
      val green = (((rgb >>8 ) & 0x000000FF) * 0.59).toInt
      val blue = (((rgb) & 0x000000FF) * 0.11).toInt
      val brightness = (red + blue + green)/3
      val newRGB = (brightness << 16) | (brightness << 8) | brightness
      out.setRGB(x,y,newRGB)
    }

    val fg = imageToFocusedGrid(out)
    val processed = fg.coflatMap(f)
    focusedGridToImage(processed, out)

    println(fg.focus)
    println(fg.grid.size)

    
  out
}
  def main(args : Array[String]) : Unit = {

    val photo1 = ImageIO.read(new File("./images/girl.png"))

    println(photo1.getHeight())
        
    def boxFilter(fg: FocusedGrid[Int]) : Int = {
      val sum = localSum(fg, 1)
      (sum / 9.0).toInt
    }

    val photo2 = processImage(photo1, boxFilter) 

    ImageIO.write(photo2, "png", new File("./images/girlsmoothed.png"))

  }

}

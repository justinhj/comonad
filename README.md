# Comonads

Some examples of Comonad usage in programs

See this blog post for the full details: [http://http://justinhj.github.io/2019/06/19/comonads-for-life.html](http://http://justinhj.github.io/2019/06/19/comonads-for-life.html)

# Image Processor

Comonad operations are nice fit for image processing methods where the value of a pixel is modified based on the values of other pixels in the image. Some examples are included such as box filtering and mirroring.

# Conway's game of life

Conway's game of life involves running a rule on each cell in a grid to determine the future state of that cell, which is nicely encoded in a function that can be run with coFlatMap

# K-Means clustering

Using the example of image compression, comonads are used in the iteration of the naive k-means algorithm to find colour use in a picture

## Running

`sbt run`

## fp-starter-pack

This project was created by https://github.com/justinhj/fp-starter-pack.g8






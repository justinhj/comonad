# Comonads

Some examples of Comonad usage in programs

See this blog post for the full details: [http://http://justinhj.github.io/2019/06/19/comonads-for-life.html](http://http://justinhj.github.io/2019/06/19/comonads-for-life.html)

# Image Processor

Comonad operations are nice fit for image processing methods where the value of a pixel is modified based on the values of other pixels in the image. Some examples are included such as box filtering and mirroring.

# Conway's game of life

Conway's game of life involves running a rule on each cell in a grid to determine the future state of that cell, which is nicely encoded in a function that can be run with coFlatMap

## Running

This is an sbt project; build and run as follows, then choose which program to run.

`sbt run`

## fp-starter-pack

This project was created by https://github.com/justinhj/fp-starter-pack.g8






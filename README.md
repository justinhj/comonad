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

## Benchmarks

[info] Result "benchmarks.FocusedGridBench.withSlowApSmall":
[info]   119.935 ±(99.9%) 7.673 us/op [Average]
[info]   (min, avg, max) = (116.598, 119.935, 121.628), stdev = 1.993
[info]   CI (99.9%): [112.262, 127.608] (assumes normal distribution)
[info] # Run complete. Total time: 00:06:47
[info] REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
[info] why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
[info] experiments, perform baseline and negative tests that provide experimental control, make sure
[info] the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
[info] Do not assume the numbers tell you what you want them to tell.
[info] Benchmark                         Mode  Cnt       Score        Error  Units
[info] FocusedGridBench.withMap2Large    avgt    5  120145.615 ±  14792.890  us/op
[info] FocusedGridBench.withMap2Small    avgt    5      52.240 ±      3.186  us/op
[info] FocusedGridBench.withSlowApLarge  avgt    5  442488.978 ± 226548.518  us/op
[info] FocusedGridBench.withSlowApSmall  avgt    5     119.935 ±      7.673  us/op
[success] Total time: 410 s, completed 27-Mar-2020 8:10:42 PM





# Comonads

Some examples of Comonad usage in programs

See this blog post for the full details: [https://justinhj.github.io/2019/06/20/comonads-for-life.html](https://justinhj.github.io/2019/06/20/comonads-for-life.html)

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

There are performed on a 2017 Macbook Pro under non-ideal conditions (bunch of Apps running)

CPU 2.5 GHz Dual-Core Intel Core i7
RAM 16 GB 2133 MHz LPDDR3

Initial benchmark

```
[info] Benchmark                         Mode  Cnt       Score        Error  Units
[info] FocusedGridBench.withMap2Large    avgt    5  120145.615 ±  14792.890  us/op
[info] FocusedGridBench.withMap2Small    avgt    5      52.240 ±      3.186  us/op
[info] FocusedGridBench.withSlowApLarge  avgt    5  442488.978 ± 226548.518  us/op
[info] FocusedGridBench.withSlowApSmall  avgt    5     119.935 ±      7.673  us/op
[success] Total time: 410 s, completed 27-Mar-2020 8:10:42 PM

Implemented and benchmarked array version

[info] Benchmark                            Mode  Cnt       Score        Error  Units
[info] FocusedGridArrayBench.withMap2Large  avgt    5  171099.587 ±  65471.687  us/op
[info] FocusedGridArrayBench.withMap2Small  avgt    5      54.418 ±     25.669  us/op
[info] FocusedGridBench.withMap2Large       avgt    5  152831.655 ±  40709.521  us/op
[info] FocusedGridBench.withMap2Small       avgt    5      59.168 ±     15.346  us/op
[info] FocusedGridBench.withSlowApLarge     avgt    5  649442.291 ± 215248.298  us/op
[info] FocusedGridBench.withSlowApSmall     avgt    5     141.025 ±     78.928  us/op

Notable that optimized map2 is 4x faster on the large image than slowAp and
the array version is actually slower than the vector version.
```

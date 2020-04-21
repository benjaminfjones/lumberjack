# lumberjack

![Java CI with Gradle](https://github.com/benjaminfjones/lumberjack/workflows/Java%20CI%20with%20Gradle/badge.svg)

Solution to a toy problem about lumberjacks.

## Problem

A lumberjack works in a forest (2d grid of integers). `0` represents flat
ground, `-1` represents an impassable trench, and `n > 0` represents a tree of
height `n`. From a flat starting position, the lumberjack must traverse the
forest moving only in cardinal directions across flat ground. Their goal is to
level the forest by chopping down trees, in order, from shortest to tallest
(there may be more than one tree of a given height). They must move adjacent
to a tree in order to chop it down, at which point the grid value becomes `0`
and the position is able to be occupied.

Given a forest and a starting position, what is the minimum distance required
to traverse the forest while cutting down all the trees?

## Implementation

The implementation here is based on Dijkstra's algorithm for path finding (see
`Grid::minDistance`) in the forest and a simple pruning breadth first search
(see `Solver::detailedSolve`) over the space of paths.

The test cases in `SolveTest.java` demonstrate the solution.

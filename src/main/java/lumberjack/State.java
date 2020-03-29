package lumberjack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import lumberjack.Coord;
import lumberjack.Grid;


/**
 * Models the state of the lumberjacks forest.
 *
 * The forest is represented by a 2d grid of integers. Flat spots are 0,
 * impassable trenches are -1, and trees of height n greater than 0 are `n`.
 * The current position of the lumberjack is (x,y) where x,y are valid
 * coordinates on the grid.
 */
class State implements Iterable<Coord3> {

    // 2d grid of integers
    private Grid grid;

    // lumberjack position
    private Coord pos;

    /**
     * Create a new state given a grid and a lumberjack position.
     *
     * The "rows" of the grid correspond to depth and the "columns" correspond
     * to width. When x,y coordinates are used, x measures depth and y
     * measures width. Thus coordinate (x,y) on the grid has height
     * `grid[x][y]`.
     *
     * A copy of the grid is made.
     *
     * @param grid an initial grid
     * @param p position of the lumberjack (checked to be on the grid)
     */
    public State(int[][] grid, Coord p) {

        // validate that grid entries have height >= -1
        this.grid = new Grid(grid, h -> { return h >= -1; });

        if (!this.grid.onGrid(p)) {
            throw new IndexOutOfBoundsException("lumberjack position is not on grid");
        }
        this.pos = p;
    }

    @Override
    public String toString() {
        return this.grid.toString() + "pos = " + this.pos;
    }

    public int getDepth() {
        return this.grid.getDepth();
    }

    public int getWidth() {
        return this.grid.getWidth();
    }

    public int getHeight(Coord p) throws NoSuchElementException {
        return this.grid.getValue(p);
    }

    @Override
    public Iterator<Coord3> iterator() {
        return this.grid.iterator();
    }

    /**
     * Find the shortest path from the lumberjack's position to the given
     * coordinate and return the length of the path.
     *
     * A valid path is a sequence of moves one unit in a cardinal direction on
     * the grid. Moves must pass over flat ground (height 0) only, no trenches
     * or trees.
     *
     * @param to the destination coordinate
     * @return minimum distance to the destination, or nothing if there is no
     * path.
     */
    public Optional<Integer> findPath(Coord to) {
        // unimplemented
        throw new UnsupportedOperationException();
    }

    /**
     * Return a set of trees in the forest that can be cut down next.
     *
     * By definition, these are the trees of minimum height. Note that it may
     * not actually be possible to cut them down due to path finding
     * constraints (trenches and taller trees).
     *
     * For now the implementation traverses the grid every time, collecting
     * trees. A better approach would be to cache the set of currently
     * choppable trees, refreshing when needed.
     *
     * @return set of trees that could be cut down next
     */
    public Set<Coord> nextTrees() {
        int minHeight = 0;
        for (Coord3 c : this) {
            int h = c.getZ();
            if (h > 0 && (minHeight == 0 || h < minHeight)) {
                minHeight = h;
            }
        }

        if (minHeight == 0) {
            return new HashSet<>();
        }

        return this.getContour(minHeight);
    }

    /**
     * Return the set of positions in the forest of a given height.
     *
     * For now the implementation traverses the grid every time.
     *
     * @return set of grid coordinates having the given height
     */
    public Set<Coord> getContour(int height) {
        Set<Coord> res = new HashSet<>();
        for (Coord3 c : this) {
            if (c.getZ() == height) {
                res.add(c.projectXY());
            }
        }
        return res;
    }
}

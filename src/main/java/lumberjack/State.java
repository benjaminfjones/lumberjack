package lumberjack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import lumberjack.Coord;
import lumberjack.StateIterator;


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
    private int[][] grid;

    // lumberjack position
    private Coord pos;

    /**
     * Create a new state given a grid and a lumberjack position.
     *
     * The "rows" of the grid correspond to depth and the "columns" correspond
     * to width. When x,y coordinates are used, y measures depth and x
     * measures width. Thus coordinate (x,y) on the grid has height
     * `grid[y][x]`.
     *
     * A copy of the grid is made.
     *
     * @param grid a 2d array of integers, #rows > 0, #cols > 0
     * @param p position of the lumberjack (assumed to be on the grid)
     */
    public State(int[][] grid, Coord p) {
        int n = grid.length;
        assert(n > 0);
        int m = grid[0].length;
        assert(m > 0);

        this.grid = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] < -1) {
                    throw new RuntimeException("invalid grid entry < -1");
                }
                this.grid[i][j] = grid[i][j];
            }
        }

        if (!(0 <= p.getX() && p.getX() < n && 0 <= p.getY() && p.getY() < m)) {
            throw new IndexOutOfBoundsException("lumberjack position is not on grid");
        }
        this.pos = p;
    }

    public String toString() {
        int maxWidth = 0;
        for (int i = 0; i < this.grid.length; i++) {
            for (int j = 0; j < this.grid[0].length; j++) {
                int entryWidth = String.format("%d", this.grid[i][j]).length();
                if (entryWidth > maxWidth) {
                    maxWidth = entryWidth;
                }
            }
        }
        assert(maxWidth > 0);

        String res = "";
        for (int i = 0; i < this.grid.length; i++) {
            for (int j = 0; j < this.grid[0].length; j++) {
                if (pos.getX() == j && pos.getY() == i) {
                    res += String.format(" %" + maxWidth + "s", "X");
                } else {
                    res += String.format(" %" + maxWidth + "d", this.grid[i][j]);
                }
            }
            res += "\n";
        }

        return res;
    }

    public int getDepth() {
        return grid.length;
    }

    public int getWidth() {
        return grid[0].length;
    }

    public int getHeight(Coord p) throws NoSuchElementException {
        int x = p.getX();
        int y = p.getY();
        if (x < 0 || y < 0 || y >= grid.length | x >= grid[0].length) {
            throw new NoSuchElementException("invalid grid position");
        }

        return grid[y][x];
    }

    @Override
    public Iterator<Coord3> iterator() {
        return new StateIterator(this.grid);
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
     * Return a list of trees in the forest that can be cut down next.
     *
     * By definition, these are the trees of minimum height. Note that it may
     * not actually be possible to cut them down due to path finding
     * constraints (trenches and taller trees).
     *
     * For now the implementation traverses the grid every time, collecting
     * trees. A better approach would be to cache the set of currently
     * choppable trees, refreshing when needed.
     *
     * @return list of trees that could be cut down next
     */
    public List<Coord> nextTrees() {
        int minHeight = 0;
        for (Coord3 c : this) {
            int h = c.getZ();
            if (h > 0 && (minHeight == 0 || h < minHeight)) {
                minHeight = h;
            }
        }

        if (minHeight == 0) {
            return new ArrayList<>();
        }

        List<Coord> res = new ArrayList<>();
        for (Coord3 c : this) {
            if (c.getZ() == minHeight) {
                res.add(c.projectXY());
            }
        }
        return res;
    }

}

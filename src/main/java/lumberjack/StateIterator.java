package lumberjack;

import java.util.Iterator;

import lumberjack.Coord3;


/**
 * Iterate over a State by returning grid entries across columns first, then
 * down rows.
 */
class StateIterator implements Iterator<Coord3> {
    // position to return next
    private int x;
    private int y;
    private int[][] grid;

    public StateIterator(int[][] grid) {
        this.x = 0;
        this.y = 0;
        this.grid = grid;
    }

    @Override
    public boolean hasNext() {
        // we assume that (x,y) is a valid grid position, unless there are no
        // more elements to traverse in which case we're at x == 0 && y == #rows
        if (y == grid.length && x == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Coord3 next() {
        Coord3 res = new Coord3(x, y, grid[y][x]);

        if (this.x == grid[0].length - 1) {
            this.x = 0;
            this.y++;
        } else {
            this.x++;
        }
        return res;
    }
}

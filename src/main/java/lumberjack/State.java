package lumberjack;

import lumberjack.Coord;


/**
 * Models the state of the lumberjacks forest.
 *
 * The forest is represented by a 2d grid of integers. Flat spots are 0,
 * impassable trenches are -1, and trees of height n greater than 0 are `n`.
 * The current position of the lumberjack is (x,y) where x,y are valid
 * coordinates on the grid.
 */
class State {

    // 2d grid of integers
    private int[][] grid;

    // lumberjack position
    private Coord pos;

    /**
     * Create a new state given a grid and a lumberjack position.
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
                this.grid[i][j] = grid[i][j];
            }
        }

        assert(0 <= p.getX() && p.getX() < n && 0 <= p.getY() && p.getY() < m);
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

}

package lumberjack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
//
import lumberjack.Coord3;


/**
 * A 2d grid of integers.
 */
class Grid implements Iterable<Coord3> {

    // backing array for the grid
    private int[][] grid;

    public Grid(Grid g) {
        this(g.grid);
    }

    /**
     * Construct a new Grid from a 2d array of ints.
     *
     * The input array must be at least 1x1 in size. A copy of the input is
     * made.
     *
     * @param grid input array
     */
    public Grid(int[][] grid) {
        this(grid, h -> {
            return true;
        });
    }

    /**
     * Construct a new Grid from a 2d array of ints.
     *
     * The input array must be at least 1x1 in size. A copy of the input is
     * made.
     *
     * @param grid input array
     * @param p predicate to validate entry values with; p(valid entry) = true
     */
    public Grid(int[][] grid, Predicate<Integer> p) {
        int n = grid.length;
        assert n > 0;
        int m = grid[0].length;
        assert m > 0;

        this.grid = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!p.test(grid[i][j])) {
                    throw new RuntimeException("invalid grid entry");
                }
                this.grid[i][j] = grid[i][j];
            }
        }
    }

    /**
     * Return the depth (#rows) in the grid.
     */
    public int getDepth() {
        return this.grid.length;
    }

    /**
     * Return the width (#cols) in the grid.
     */
    public int getWidth() {
        return this.grid[0].length;
    }

    /**
     * Check whether Coord `p` is a valid grid position.
     *
     * Note: the X-dimension corresponds to rows, the Y to columns.
     */
    public boolean onGrid(Coord p) {
        return (
            0 <= p.getX() && p.getX() < this.getDepth()
            && 0 <= p.getY() && p.getY() < this.getWidth());
    }

    public Predicate<Coord> onGridPredicate() {
        return this::onGrid;
    }

    public String toString() {
        return this.annotateGrid(new Coord(0, 0), "");
    }

    /**
     * Format a grid into a multiline string, including a mark at the given
     * location.
     *
     * If mark == "", just format the grid as is.
     */
    public String annotateGrid(Coord c, String mark) {
        int maxWidth = 0;
        for (int i = 0; i < this.getDepth(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                Coord p = new Coord(i, j);
                int entryWidth = String.format("%d", this.getValue(p)).length();
                if (entryWidth > maxWidth) {
                    maxWidth = entryWidth;
                }
            }
        }
        maxWidth = Math.max(maxWidth, mark.length());
        assert maxWidth > 0;

        String res = "";
        for (int i = 0; i < this.getDepth(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                if (mark.length() > 0 && c.getX() == i && c.getY() == j) {
                    res += String.format(" %" + maxWidth + "s", mark);
                } else {
                    Coord p = new Coord(i, j);
                    res += String.format(" %" + maxWidth + "d", this.getValue(p));
                }
            }
            res += "\n";
        }

        return res;
    }

    public int getValue(Coord p) throws NoSuchElementException {
        if (!this.onGrid(p)) {
            throw new NoSuchElementException("invalid grid position");
        }

        return grid[p.getX()][p.getY()];
    }

    public void setValue(Coord p, int v) {
        if (!this.onGrid(p)) {
            throw new NoSuchElementException("invalid grid position");
        }

        this.grid[p.getX()][p.getY()] = v;
    }

    public Coord3 getCoord3(Coord p) throws NoSuchElementException {
        return new Coord3(p.getX(), p.getY(), this.getValue(p));
    }

    /**
     * Compute the minimum Manhatten distance required to move from `from` to
     * `to`, while avoiding positions that fail the given predicate.
     *
     * The implementation is Djikstra's algorithm.
     *
     * @param from starting grid position
     * @param to ending grid position
     * @param passable predicate indicating whether an arbitrary
     *     position/value (encoded as a Coord3) is passable
     *
     * @return Return the minimum travel distance, or Optional.empty() if
     *     there is no path
     */
    public Optional<Integer> minDistance(Coord from, Coord to,
                                         Predicate<Coord3> passable) {
        // initialize frontier: the set of nodes to explore next
        Set<Coord> frontier = new HashSet<>();
        frontier.add(from);
        // initialize the visited set: to visit a position means to assign
        // distances to all its neighbors and add it to the visited set
        Set<Coord> visited = new HashSet<>();

        // Initialize minDist map, the keys represent positions whose distance
        // has been measured. The values are best known distances to the
        // position. Note that when a node has been visited, it's neighbors
        // have their estimated min distances recorded.
        Map<Coord, Integer> minDist = new HashMap<>();
        minDist.put(from, 0);

        // While the frontier is non-empty, explore the passable neighbors.
        // Loop invariant: if x in frontier, then x is not in visited set and
        // minDist contains an entry for x.
        while (!frontier.isEmpty()) {
            Set<Coord> nextFrontier = new HashSet<>();
            for (Coord c : frontier) {
                assert minDist.containsKey(c);
                assert !visited.contains(c);
                int coordDist = minDist.get(c);
                for (Coord n : this.neighbors(c, passable)) {
                    boolean nbVisited = visited.contains(n);
                    if (!nbVisited || minDist.get(n) > coordDist + 1) {
                        minDist.put(n, coordDist + 1);
                    }
                    if (!nbVisited) {
                        nextFrontier.add(n);
                    }
                }
                visited.add(c);
            }
            frontier = nextFrontier;
        }

        if (minDist.containsKey(to)) {
            return Optional.of(minDist.get(to));
        } else {
            // If we didn't reach `to` along the way, the return Optional.empty()
            return Optional.empty();
        }
    }

    /**
     * Return the set of neighboring grid positions that are passable
     * according to the given predicate.
     *
     * @param p given position
     * @param passable predicate indicating whether an arbitrary
     *     position/value (encoded as a Coord3) is passable
     */
    public Set<Coord> neighbors(Coord p, Predicate<Coord3> passable) {
        Predicate<Coord> passableClosure = c -> {
            Coord3 coordWithValue = new Coord3(c.getX(), c.getY(), this.getValue(c));
            return passable.test(coordWithValue);
        };
        return p.cardinalNeighbors()
            .stream()
            .filter(this.onGridPredicate().and(passableClosure))
            .collect(Collectors.toSet());
    }

    @Override
    public Iterator<Coord3> iterator() {
        return new GridIterator(this);
    }

    /**
     * Iterate over a Grid by returning entries across columns first, then
     * down rows.
     */
    class GridIterator implements Iterator<Coord3> {
        // position to return next
        private int x;
        private int y;
        private Grid grid;

        public GridIterator(Grid grid) {
            this.x = 0;
            this.y = 0;
            this.grid = grid;
        }

        @Override
        public boolean hasNext() {
            // we assume that (x,y) is a valid grid position, unless there are no
            // more elements to traverse in which case we're at x == #rows && y == 0
            if (x == grid.getDepth() && y == 0) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public Coord3 next() {
            Coord3 res = new Coord3(x, y, grid.getValue(new Coord(x, y)));

            if (this.y == grid.getWidth() - 1) {
                this.y = 0;
                this.x++;
            } else {
                this.y++;
            }
            return res;
        }
    }
}

package lumberjack;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Models the state of the lumberjack's forest.
 *
 * The forest is represented by a 2d grid of integers. Flat spots are 0,
 * impassable trenches are -1, and trees of height {@code n > 0} are {@code n}.
 * The current position of the lumberjack is (x,y) where x, y are valid
 * coordinates on the grid and represent rows, columns, respectively.
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
     * measures width. Thus coordinate (x, y) on the grid has height
     * `grid[x][y]`.
     *
     * A copy of the grid and of p are made.
     *
     * @param grid an initial grid
     * @param p position of the lumberjack (checked to be on the grid)
     */
    public State(int[][] grid, Coord p) {

        // validate that grid entries have height >= -1
        this.grid = new Grid(grid, h -> {
            return h >= -1;
        });

        if (!this.grid.onGrid(p)) {
            throw new IndexOutOfBoundsException("lumberjack position is not on grid");
        }
        this.pos = p;
    }

    /**
     * A semi-(copy constructor).
     *
     * Both arguments are copied.
     */
    public State(Grid grid, Coord p) {
        this.grid = new Grid(grid);
        this.pos = new Coord(p);
    }

    /**
     * Copy constructor.
     */
    public State(State state) {
        this(state.grid, state.pos);
    }

    /**
     * Return the position.
     */
    public Coord getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return this.grid.annotateGrid(this.pos, "X");
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof State)) {
            return false;
        }
        State otherState = (State) other;
        return this.grid.equals(otherState.grid) && this.pos.equals(otherState.pos);
    }

    @Override
    public int hashCode() {
        return 31 * this.grid.hashCode() + this.pos.hashCode();
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
     * Return true if there are trees in the forest.
     */
    public boolean hasTrees() {
        for (Coord3 c3 : this) {
            if (c3.getZ() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find the shortest path from the lumberjack's position to the given
     * coordinate and return the length of the path.
     *
     * A valid path is a sequence of moves one unit in a cardinal direction on
     * the grid. Moves must pass over flat ground (height 0) only, no trenches
     * or trees with the exception of the final position where a tree (to be
     * cut down) is allowed to exist.
     *
     * @param to the destination coordinate
     * @return minimum distance to the destination, or nothing if there is no
     *     path.
     */
    public Optional<Integer> findPath(Coord to) {
        Predicate<Coord3> passable = c3 -> {
            return c3.getZ() == 0 || c3.projectXY().equals(to);
        };

        return this.grid.minDistance(this.pos, to, passable);
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
     * Modify the current state so that the tree at coordinate `p` has been
     * chopped down and the lumberjack's position is `p`.
     *
     * Fluent style.
     * Assume that there is in fact a tree at `p`.
     */
    public State chop(Coord p) throws IndexOutOfBoundsException {
        this.grid.setValue(p, 0);
        this.pos = new Coord(p);
        return this;
    }

    /**
     * Return a set of states which are reachable from the current state via
     * movement and a single tree chop.
     *
     * The returned state do not share memory with the current state.
     */
    public Set<StateJump> nextStates() {
        Set<Coord> nextTrees = this.nextTrees();
        Set<StateJump> res = new HashSet<>();
        for (Coord t : nextTrees) {
            Optional<Integer> d = this.findPath(t);
            if (d.isPresent()) {
                State newState = new State(this).chop(t);
                res.add(new StateJump(newState, d.get()));
            }
        }
        return res;
    }

}

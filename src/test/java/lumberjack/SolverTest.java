package lumberjack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class SolverTest {

    private static int[][] grid1 = {
        {0,1,0},
        {0,0,-1},
        {3,0,0}};

    // tree surrounded by moat
    private static int[][] moat = {
        {0,0,0,0,0},
        {0,-1,-1,-1,0},
        {0,-1,1,-1,0},
        {0,-1,-1,-1,0},
        {0,0,0,0,0}};

    // line of trees, some branching
    private static int[][] lineOfTrees = {
        {1,1,1,1},
        {0,0,0,0}};

    // 28 moves, linear
    private static int[][] grid6 = {
        {0, 0, 0, 0, 3},
        {0, 2,-1, 0, 0},
        {0,-1,-1, 0, 0},
        {0, 0, 0, 0, 0},
        {4, 0, 0, 0, 1}};

    // 28 moves, lots of branching
    private static int[][] dense = {
        {1, 1, 1, 2, 3},
        {1, 1, 1, 2, 1},
        {1, 1, 2, 2, 1},
        {1, 2, 2, 2, 1},
        {1, 1, 1, 1, 0}};

    /**
     * Test that there is a minimal solution with distance 4: chop right,
     * down, down, chop left.
     */
    @Test
    public void testSolveGrid1() {
        State initial = new State(grid1, new Coord(0,0));
        Solver solver = new Solver(initial);
        assertEquals(4, solver.solve());

        // test starting in the opposite corner
        initial = new State(grid1, new Coord(2,2));
        solver = new Solver(initial);
        assertEquals(6, solver.solve());
    }

    /**
     * Test that there is no solution when a single tree is surrounded by a
     * moat.
     */
    @Test
    public void testSolveMoat() {
        State initial = new State(moat, new Coord(0,0));
        Solver solver = new Solver(initial);
        assertEquals(-1, solver.solve());
    }

    /**
     * Test that a minimal solution to cutting down the line of trees starts
     * at an immediately adjacent tree and proceeds rightward.
     *
     * Initially there are 4 paths of 2 steps corresponding to each of the 4
     * trees. For each of those 4, there are 3 extensions to cut down the
     * second tree, giving 12 paths. Without pruning there would then be 24
     * paths and then finally 24 paths to compare.
     */
    @Test
    public void testSolveLineOfTrees() {
        State initial = new State(lineOfTrees, new Coord(1,0));
        Solver solver = new Solver(initial);
        assertEquals(4, solver.solve());
    }

    /**
     * Test a larger initial state.
     *
     * The test input corresponds to grid6 in StateTest.java.
     */
    @Test
    public void testSolveGrid6() {
        State initial = new State(grid6, new Coord(0,0));
        Solver solver = new Solver(initial);
        assertEquals(28, solver.solve());
    }

    /**
     * Test a larger initial state with a lot of branching paths.
     */
    @Test
    public void testSolveDense() {
        State initial = new State(dense, new Coord(4,4));
        Solver solver = new Solver(initial);
        assertEquals(28, solver.solve());
    }
}

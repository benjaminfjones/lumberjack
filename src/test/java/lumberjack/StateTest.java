package lumberjack;

import static org.junit.Assert.*;
// import static org.junit.Assert.assertEqual;
// import static org.junit.Assert.assertThrows;
// import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

import lumberjack.Coord;
import lumberjack.State;


public class StateTest {

    private static int[][] badGrid1 = {
        {-3,1,0},
        {0,0,-1},
        {3,0,0}};

    private static int[][] grid1 = {
        {0,1,0},
        {0,0,-1},
        {3,0,0}};
    private static int[][] grid2 = {
        {1,2},
        {3,0},
        {0,-1}};
    private static int[][] grid3 = {
        {0,-1,0},
        {0,0,-1},
        {0,0,0}};
    private static int[][] grid4 = {
        {1,0,0},
        {0,1,0},
        {0,0,1}};
    private static int[][] grid5 = {
        {0,1,2},
        {7,10,3},
        {6,5,4}};
    private static int[][] grid6 = {
        {0, 0, 0, 0, 3},
        {0, 2,-1, 0, 0},
        {0,-1,-1, 0, 0},
        {0, 0, 0, 0, 0},
        {4, 0, 0, 0, 1}};

    @Test
    public void testNewState() {
        State s = new State(grid1, new Coord(0,0));
        System.out.println(s.toString());

        System.out.println();

        s = new State(grid2, new Coord(1,1));
        System.out.println(s.toString());
    }

    @Test
    public void testBadNewState() {
        assertThrows(RuntimeException.class, () -> {
            State s = new State(badGrid1, new Coord(0,0));
        });
    }

    @Test
    public void testBadCoord() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            State s = new State(grid1, new Coord(-2,-2));
        });
    }

    @Test
    public void testGetHeight() {
        State s = new State(grid1, new Coord(0, 0));
        for (int x = 0; x < s.getDepth(); x++) {
            for (int y = 0; y < s.getWidth(); y++) {
                assertEquals(s.getHeight(new Coord(x, y)), grid1[x][y]);
            }
        }

        // test non-square grid
        s = new State(grid2, new Coord(0, 0));
        for (int x = 0; x < s.getDepth(); x++) {
            for (int y = 0; y < s.getWidth(); y++) {
                assertEquals(s.getHeight(new Coord(x, y)), grid2[x][y]);
            }
        }
    }

    @Test
    public void testNextTrees() {
        State s = new State(grid1, new Coord(0, 0));
        Set<Coord> ts = s.nextTrees();
        assertEquals(ts.size(), 1);
        Set<Coord> oracle = new HashSet<Coord>();
        oracle.add(new Coord(0, 1));
        assertEquals(ts, oracle);

        s = new State(grid3, new Coord(0, 0));  // no trees
        ts = s.nextTrees();
        assertTrue(ts.isEmpty());
    }

    @Test
    public void testGetContour() {
        // grid 1, trees of height 1
        State s = new State(grid1, new Coord(0, 0));
        Set<Coord> ps = s.getContour(1);
        Set<Coord> oracle = new HashSet<>();
        oracle.add(new Coord(0, 1));
        assertEquals(ps, oracle);

        // grid 4, trees of height 1
        s = new State(grid4, new Coord(0, 0));
        ps = s.getContour(1);
        oracle = new HashSet<>();
        oracle.add(new Coord(0, 0));
        oracle.add(new Coord(1, 1));
        oracle.add(new Coord(2, 2));
        assertEquals(ps, oracle);

        // grid 4, flat spots
        ps = s.getContour(0);
        oracle = new HashSet<>();
        oracle.add(new Coord(0, 1));
        oracle.add(new Coord(0, 2));
        oracle.add(new Coord(1, 0));
        oracle.add(new Coord(1, 2));
        oracle.add(new Coord(2, 0));
        oracle.add(new Coord(2, 1));
        assertEquals(ps, oracle);
    }

    @Test
    public void testFindPath() {
        // can cross flat ground
        State s = new State(grid1, new Coord(0, 0));
        assertEquals(1, (int)s.findPath(new Coord(0, 1)).get());
        assertEquals(2, (int)s.findPath(new Coord(2, 0)).get());
        assertEquals(4, (int)s.findPath(new Coord(2, 2)).get());

        // can't cross trenches
        s = new State(grid3, new Coord(0, 0));
        assertTrue(!s.findPath(new Coord(0, 2)).isPresent());

        s = new State(grid4, new Coord(2, 0));
        // can cross flat ground and end on a tree
        assertTrue(s.findPath(new Coord(1, 1)).isPresent());
        // can't cross through trees
        assertTrue(!s.findPath(new Coord(0, 2)).isPresent());
    }

    @Test
    public void testChop() {
        // level grid1
        State s = new State(grid1, new Coord(0, 0))
                .chop(new Coord(0, 1))
                .chop(new Coord(2, 0));
        for (Coord3 c : s) {
            assertTrue(c.getZ() <= 0);
        }
    }

    @Test
    public void testNextState() {
        State s = new State(grid1, new Coord(0, 0));

        // the unique next state is to chop down tree at (0, 1)
        Set<StateJump> nextStates = s.nextStates();
        assertEquals(1, nextStates.size());
        s = nextStates.iterator().next().state;
        assertEquals(0, s.getHeight(new Coord(0, 1)));

        // then, the unique next state is to chop down tree at (2, 0)
        nextStates = s.nextStates();
        assertEquals(1, nextStates.size());
        s = nextStates.iterator().next().state;
        assertEquals(0, s.getHeight(new Coord(2, 0)));

        // there are no moves starting on grid2
        s = new State(grid2, new Coord(1, 1));
        nextStates = s.nextStates();
        assertEquals(0, nextStates.size());

        // there are no moves starting on grid3
        s = new State(grid3, new Coord(2, 0));
        nextStates = s.nextStates();
        assertEquals(0, nextStates.size());

        // {0,1,2},
        // {7,10,3},
        // {6,5,4}
        // explore the unique next steps as we spiral around the grid
        s = new State(grid5, new Coord(0, 0));
        nextStates = s.nextStates();
        int d = 0;
        while (nextStates.size() > 0) {
            assertEquals(1, nextStates.size());
            s = nextStates.iterator().next().state;
            d += nextStates.iterator().next().dist;
            nextStates = s.nextStates();
        }
        // verify that field is chopped down
        for (Coord3 c : s) {
            assertTrue(c.getZ() <= 0);
        }
        // verify the distance traveled
        assertEquals(8, d);

        // 28 steps to level grid6
        s = new State(grid6, new Coord(0, 0));
        System.out.println(s);
        nextStates = s.nextStates();
        d = 0;
        while (nextStates.size() > 0) {
            assertEquals(1, nextStates.size());
            s = nextStates.iterator().next().state;
            System.out.println(s);
            d += nextStates.iterator().next().dist;
            nextStates = s.nextStates();
        }
        for (Coord3 c : s) {
            assertTrue(c.getZ() <= 0);
        }
        // verify the distance traveled
        assertEquals(28, d);
    }
}

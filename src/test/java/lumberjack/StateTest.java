package lumberjack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

import lumberjack.Coord;
import lumberjack.State;


public class StateTest {

    private static int[][] badGrid1 = {{-3,1,0}, {0,0,-1}, {3,0,0}};

    private static int[][] grid1 = {{0,1,0}, {0,0,-1}, {3,0,0}};
    private static int[][] grid2 = {{1,2}, {3,0}, {0,-1}};
    private static int[][] grid3 = {{0,-1,0}, {0,0,-1}, {0,0,0}};
    private static int[][] grid4 = {{1,0,0}, {0,1,0}, {0,0,1}};

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
        assertEquals("ts = " + ts, ts, oracle);

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
}

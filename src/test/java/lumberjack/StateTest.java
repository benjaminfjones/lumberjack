package lumberjack;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

import lumberjack.Coord;
import lumberjack.State;


public class StateTest {

    private static int[][] badGrid1 = {{-3,1,0}, {0,0,-1}, {3,0,0}};

    private static int[][] grid1 = {{0,1,0}, {0,0,-1}, {3,0,0}};
    private static int[][] grid2 = {{1,2}, {3,0}, {0,-1}};
    private static int[][] grid3 = {{0,-1,0}, {0,0,-1}, {0,0,0}};

    @Test
    public void testNewState() {
        State s = new State(grid1, new Coord(0,0));
        System.out.print(s.toString());

        System.out.println();

        s = new State(grid2, new Coord(1,1));
        System.out.print(s.toString());
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
        for (int y = 0; y < s.getDepth(); y++) {
            for (int x = 0; x < s.getWidth(); x++) {
                assertEquals(s.getHeight(new Coord(x, y)), grid1[y][x]);
            }
        }
    }

    @Test
    public void testNextTrees() {
        State s = new State(grid1, new Coord(0, 0));
        List<Coord> ts = s.nextTrees();
        assertEquals(ts.size(), 1);
        List<Coord> oracle = new ArrayList<Coord>();
        oracle.add(new Coord(1, 0));
        assertEquals("ts = " + ts, ts, oracle);

        s = new State(grid3, new Coord(0, 0));  // no trees
        ts = s.nextTrees();
        assertTrue(ts.isEmpty());
    }


}

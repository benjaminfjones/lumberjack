package lumberjack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
//
import lumberjack.Coord;
//
import org.junit.Before;
import org.junit.Test;


public class GridTest {

    private static int[][] grid1 = {{0,1,0}, {0,0,-1}, {3,0,0}};
    private static int[][] grid2 = {{1,2}, {3,0}, {0,-1}};
    private static int[][] grid3 = {{0, 0, 0, 0, 0}, {0, 2, -1, 1, 0}};
    private static List<Grid> allGrids;

    /**
     * Setup unit tests by creating Grids from all raw grids.
     */
    @Before
    public void setup() {
        List<int[][]> allRawGrids = Arrays.asList(
            grid1,
            grid2,
            grid3
        );
        allGrids = new ArrayList<Grid>();
        for (int[][] rg : allRawGrids) {
            allGrids.add(new Grid(rg));
        }
    }

    @Test
    public void testNewGrid() {
        Grid g = new Grid(grid1, h -> {
            return h >= -1;
        });
        assertEquals(g.getDepth(), 3);
        assertEquals(g.getDepth(), 3);
        assertEquals(g.getValue(new Coord(0, 1)), 1);
        assertEquals(g.getValue(new Coord(1, 2)), -1);
        assertEquals(g.getValue(new Coord(2, 0)), 3);

        // check that entry value validation works
        assertThrows(RuntimeException.class, () -> {
            Grid bad = new Grid(grid1, h -> {
                return h > 0;
            });
        });
    }

    @Test
    public void testIteration() {
        // Test that we iterate over depth x width entries
        for (Grid g : allGrids) {
            Set<Coord3> cs = new HashSet<>();
            for (Coord3 c : g) {
                cs.add(c);
            }
            assertEquals(cs.size(), g.getDepth() * g.getWidth());
        }
    }

    @Test
    public void testNeighbors() {
        Grid g = new Grid(grid1);
        Coord p0 = new Coord(0, 0);

        // with no passable constraint, we should get 2 neighbors, one a row
        // down and the other a column over
        Set<Coord> nbs = g.neighbors(p0, c3 -> {
            return true;
        });
        assertEquals(nbs, new HashSet<Coord>(Arrays.asList(
            new Coord(0,1),
            new Coord(1,0)
        )));

        // rule out flat spots as neighbors, result should be a single coord
        nbs = g.neighbors(p0, c3 -> {
            return c3.getZ() != 0;
        });
        assertEquals(nbs, new HashSet<Coord>(Arrays.asList(new Coord(0,1))));

        // rule out all positions with value < 2, result should empty
        nbs = g.neighbors(p0, c3 -> {
            return c3.getZ() >= 2;
        });
        assertTrue(nbs.isEmpty());

        // check all four cardinal neighbors of the center of the 3x3 grid
        Coord p1 = new Coord(1, 1);
        nbs = g.neighbors(p1, c3 -> {
            return true;
        });
        assertEquals("nbs = " + nbs, nbs.size(), 4);

        // exclude single neighbor of value -1
        nbs = g.neighbors(p1, c3 -> {
            return c3.getZ() >= 0;
        });
        assertEquals("nbs = " + nbs, nbs.size(), 3);
    }

    @Test
    public void testMinDistance() {
        Predicate<Coord3> flat = c3 -> {
            return c3.getZ() == 0;
        };

        // Find a path to a flat spot
        int[][] grid = {{0,0,0}, {0,0,0}, {0,0,0}};
        Grid g = new Grid(grid);
        Optional<Integer> d = g.minDistance(new Coord(0,0), new Coord(2,2), flat);
        assertTrue(d.isPresent());
        assertEquals(d.get(), new Integer(4));

        // XXX awkward to have to build destination into the predicate
        Predicate<Coord3> flatUntilEnd = c3 -> {
            return c3.getZ() == 0 || (c3.getX() == 2 && c3.getY() == 2);
        };

        // Find a path to a tree
        int[][] gridWithTree = {{0,0,0}, {0,0,0}, {0,0,1}};
        g = new Grid(gridWithTree);
        d = g.minDistance(new Coord(0,0), new Coord(2,2), flatUntilEnd);
        assertTrue(d.isPresent());
        assertEquals(d.get(), new Integer(4));

        // Try to find a path to a blocked location
        int[][] gridNoPath = {
            {0,0,0},
            {0,1,1},
            {0,1,0}
        };
        g = new Grid(gridNoPath);
        d = g.minDistance(new Coord(0,0), new Coord(2,2), flatUntilEnd);
        assertTrue(!d.isPresent());

        // Find a spiral path
        int[][] gridSpiral = {
            {0,0,0,0,0},
            {0,1,1,1,0},
            {0,1,0,0,0}
        };
        g = new Grid(gridSpiral);
        d = g.minDistance(new Coord(2,0), new Coord(2,2), flat);
        assertTrue(d.isPresent());
        assertEquals(d.get(), new Integer(10));

        // Find the shortest of two paths (dist 2 and 8)
        int[][] gridTwoPaths = {
            {0,0,0,0},
            {0,1,1,0},
            {0,0,0,0}
        };
        g = new Grid(gridTwoPaths);
        d = g.minDistance(new Coord(0,0), new Coord(2,0), flat);
        assertTrue(d.isPresent());
        assertEquals(d.get(), new Integer(2));
    }
}

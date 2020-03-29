package lumberjack;

import org.junit.Test;
import static org.junit.Assert.*;

import lumberjack.Coord;
import lumberjack.State;


public class StateTest {

    @Test
    public void testNewState() {
        int[][] grid = {{0,1,0}, {0,0,-2}, {3,0,0}};
        State s = new State(grid, new Coord(0,0));
        System.out.print(s.toString());
    }

}

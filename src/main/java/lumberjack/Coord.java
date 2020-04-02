package lumberjack;

import java.util.HashSet;
import java.util.Set;


/**
 * Represents 2d grid location.
 */
class Coord {
    private int x;
    private int y;

    public Coord(Coord c) {
        this(c.x, c.y);
    }

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Coord) {
            Coord c = (Coord) other;
            return this.x == c.getX() && this.y == c.getY();
        }
        return false;
    }

    /**
     * Szudzik's function for hashing two integers < 2^15
     */
    @Override
    public int hashCode() {
        if (this.x >= this.y) {
            return x*x + x + y;
        } else {
            return x + y*y;
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    /**
     * Return the 4 coordinates within 1 Manhatten distance from the
     * corrdinate.
     */
    public Set<Coord> cardinalNeighbors() {
        Set<Coord> res = new HashSet<>();
        res.add(new Coord(this.x+1, this.y));
        res.add(new Coord(this.x-1, this.y));
        res.add(new Coord(this.x, this.y+1));
        res.add(new Coord(this.x, this.y-1));
        return res;
    }
}

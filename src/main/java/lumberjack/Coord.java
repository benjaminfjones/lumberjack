package lumberjack;

/**
 * Represents 2d grid location.
 */
class Coord {
    private int x;
    private int y;

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
}

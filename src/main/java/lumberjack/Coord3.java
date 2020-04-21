package lumberjack;

/**
 * Represents 3d grid location.
 *
 * This class is designed to be immutable.
 */
class Coord3 {
    private int x;
    private int y;
    private int z;

    public Coord3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Coord) {
            Coord3 c = (Coord3) other;
            return this.x == c.getX() && this.y == c.getY() && this.z == c.getZ();
        }
        return false;
    }

    /**
     * Szudzik's function for hashing two integers less than 2^15, where the two
     * integers are the height and the hashCode of the X-Y projection.
     */
    @Override
    public int hashCode() {
        int h = this.projectXY().hashCode();
        if (h >= this.z) {
            return h * h + h + z;
        } else {
            return h + z * z;
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d)", x, y, z);
    }

    public Coord projectXY() {
        return new Coord(x, y);
    }
}

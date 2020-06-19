package lumberjack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

class Path implements Iterable<StateJump> {

    /**
     * List of State / dist pairs.
     *
     * By convention the first step should always be the initial state and the
     * distance should be zero.
     */
    private List<StateJump> steps;

    /**
     * Maintain the total distance covered by the path.
     */
    private int distance;

    /**
     * Construct a new path from a single initial state.
     */
    public Path(StateJump step) {
        this(Arrays.asList(step));
    }

    /**
     * Construct a new path by copying all steps from the input list.
     */
    public Path(List<StateJump> steps) {
        this.steps = new ArrayList<StateJump>();
        this.distance = 0;
        for (StateJump sj : steps) {
            this.steps.add(new StateJump(sj));
            this.distance += sj.dist;
        }
    }

    public Path(Path copy) {
        this(copy.steps);
    }

    /**
     * Add a next step to the path.
     *
     * The input is copied.
     */
    public void add(StateJump nextStep) {
        this.steps.add(new StateJump(nextStep));
        this.distance += nextStep.dist;
    }

    public int numSteps() {
        return this.steps.size();
    }

    public int getDistance() {
        return distance;
    }

    public Iterator<StateJump> iterator() {
        return this.steps.iterator();
    }

    /**
     * Return the last element in the list or Optional.empty().
     */
    public Optional<StateJump> head() {
        if (this.steps.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(this.steps.get(this.steps.size() - 1));
        }
    }

    @Override
    public String toString() {
        String res = "";
        boolean first = true;
        for (StateJump sj : this.steps) {
            Coord pos = sj.state.getPos();
            if (first) {
                res += pos;
                first = false;
            } else {
                res += " -> " + pos;
            }
        }
        return res;
    }

}

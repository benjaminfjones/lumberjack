package lumberjack;

/**
 * Holds a pair of a state and a distance required to get there from some
 * unspecified starting point.
 */
class StateJump {
    public State state;
    public int dist;

    public StateJump(State state, int dist) {
        this.state = state;
        this.dist = dist;
    }

    public StateJump(StateJump copy) {
        this.state = new State(copy.state);
        this.dist = copy.dist;
    }
}

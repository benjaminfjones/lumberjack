package lumberjack;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class Solver {

    /**
     * Initial state of the solver.
     */
    private final State initial;

    public Solver(State initial) {
        this.initial = initial;
    }

    /**
     * Return the minimal distance required to travel to cut down the whole
     * initial forest.
     *
     * Returns -1 if there is no solution.
     */
    public int solve() {
        Optional<Path> detailedSolution = detailedSolve();
        if (detailedSolution.isPresent()) {
            return detailedSolution.get().getDistance();
        } else {
            return -1;
        }
    }

    /**
     * Return a minimal path through the forest that cuts down all trees in
     * order from shortest to tallest.
     */
    public Optional<Path> detailedSolve() {
        // cache states we've visited so far along with minimum distances to
        // them
        Map<State, Integer> knownStates = new HashMap<>();
        knownStates.put(this.initial, 0);

        // paths contains the current path frontier
        Set<Path> paths = new HashSet<>();
        Path initialPath = new Path(new StateJump(this.initial, 0));
        paths.add(initialPath);

        // temporary set of new paths in the BFS loop
        Set<Path> newPaths;
        // paths with no next states
        Set<Path> finalPaths = new HashSet<>();

        // Main pruned BFS loop
        int bfsSteps = 0;
        do {
            newPaths = new HashSet<>();
            for (Path p : paths) {
                StateJump head = p.head().get();
                Set<StateJump> nextStates = head.state.nextStates();

                // If there are no more next states, either we've cut down the
                // forest, or we're stuck. We filter out the stuck cases post
                // BFS.
                if (nextStates.isEmpty()) {
                    finalPaths.add(p);
                }
                for (StateJump sj : nextStates) {
                    // If the new destination state is not known yet, or it is
                    // known but the distance to get there was higher than
                    // on this path, then add a new path.
                    int newDist = p.getDistance() + sj.dist;
                    if (!knownStates.containsKey(sj.state)
                        || knownStates.get(sj.state) > newDist) {
                        Path np = new Path(p);
                        np.add(sj);
                        newPaths.add(np);
                        knownStates.put(sj.state, newDist);
                    }
                }
            }
            paths = newPaths;
            System.out.println(
                    String.format("Solver: path set size %d", paths.size()));
            bfsSteps++;
        } while (newPaths.size() > 0);
        System.out.println(String.format("Solver: %d BFS steps", bfsSteps));

        // Filter out final paths that still have trees present
        Set<Path> finalValidPaths = finalPaths.stream()
            .filter(p -> {
                return !p.head().get().state.hasTrees();
            })
            .collect(Collectors.toSet());
        int filtered = finalPaths.size() - finalValidPaths.size();
        System.out.println(
                String.format("Solver: [WARN] filtered %s final paths with trees left",
                              filtered));

        // find a minimum distance final path
        if (finalValidPaths.size() == 0) {
            return Optional.empty();
        }
        Path minPath = Collections.min(finalValidPaths,
                Comparator.comparingInt(Path::getDistance));
        return Optional.of(minPath);
    }
}

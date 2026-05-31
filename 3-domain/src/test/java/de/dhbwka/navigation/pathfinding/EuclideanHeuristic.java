package de.dhbwka.navigation.pathfinding;

import de.dhbwka.navigation.graph.node.VectorNode;
import de.dhbwka.navigation.math.Euclid;

public class EuclideanHeuristic<T extends VectorNode> implements Heuristic<T> {

    @Override
    public double estimate(T from, T to) {
        return Euclid.distance(from.getComponents(), to.getComponents());
    }
}

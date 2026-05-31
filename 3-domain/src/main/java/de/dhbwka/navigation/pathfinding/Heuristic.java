package de.dhbwka.navigation.pathfinding;

import de.dhbwka.navigation.graph.node.Node;

@FunctionalInterface
public interface Heuristic<T extends Node> {
    double estimate(T from, T to);
}

package de.dhbwka.navigation.pathfinding;

import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.node.Node;

public interface EdgeScorer<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > {
    double calculateScore(EdgeType edge);
}

package de.dhbwka.navigation.pathfinding;

import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.node.Node;

import java.util.List;

public interface PathFinder<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > {
    List<EdgeType> findPath(String startId, String destinationId);
}

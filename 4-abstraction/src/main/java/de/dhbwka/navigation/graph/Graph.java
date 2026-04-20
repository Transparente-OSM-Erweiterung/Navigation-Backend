package de.dhbwka.navigation.graph;

import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.node.Node;

import java.util.Collection;
import java.util.Optional;

public interface Graph<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > {
    Optional<NodeType> getNode(String id);

    Collection<EdgeType> getEdgesFrom(String nodeId);
}

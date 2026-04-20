package de.dhbwka.navigation.graph;

import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.node.Node;

import java.util.Optional;

public interface GraphBuilder<NodeType extends Node, EdgeType extends Edge<? extends NodeType>> {
    void addNode(NodeType node);

    void addEdge(EdgeType edge);

    Optional<NodeType> getNode(String id);
}

package de.dhbwka.navigation.graph.edge;

import de.dhbwka.navigation.graph.node.Node;

public interface WidthedEdge<NodeType extends Node> extends Edge<NodeType> {
    double getWidth();
}

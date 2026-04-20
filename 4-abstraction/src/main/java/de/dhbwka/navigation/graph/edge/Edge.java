package de.dhbwka.navigation.graph.edge;

import de.dhbwka.navigation.graph.node.Node;

public interface Edge <NodeType extends Node> {
    NodeType getOrigin();
    NodeType getDestination();
}

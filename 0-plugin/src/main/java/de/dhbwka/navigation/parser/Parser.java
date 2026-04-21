package de.dhbwka.navigation.parser;

import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.GraphBuilder;
import de.dhbwka.navigation.graph.node.Node;

public interface Parser<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > {
    void parse(GraphBuilder<? super NodeType, ? super EdgeType> builder);
}

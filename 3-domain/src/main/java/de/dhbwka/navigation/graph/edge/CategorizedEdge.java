package de.dhbwka.navigation.graph.edge;

import de.dhbwka.navigation.graph.node.Node;

public interface CategorizedEdge<
        NodeType extends Node,
        CategoryType extends EdgeCategory
    > extends Edge<NodeType> {
    CategoryType getCategory();
}

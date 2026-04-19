package de.dhbwka.navigation;

public interface CategorizedEdge<
        NodeType extends Node,
        CategoryType extends EdgeCategory
    > extends Edge<NodeType> {
    CategoryType getCategory();
}

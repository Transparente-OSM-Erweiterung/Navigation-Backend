package de.dhbwka.navigation;

public interface Parser<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > {
    void parse(GraphBuilder<? super NodeType, ? super EdgeType> builder);
}

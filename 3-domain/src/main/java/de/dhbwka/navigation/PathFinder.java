package de.dhbwka.navigation;

import java.util.List;

public interface PathFinder<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > {
    List<EdgeType> findPath(NodeType start, NodeType destination);
}

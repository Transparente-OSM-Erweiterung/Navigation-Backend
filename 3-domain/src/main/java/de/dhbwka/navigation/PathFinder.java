package de.dhbwka.navigation;

import java.util.List;

public interface PathFinder<NodeType extends Node, EdgeType extends Edge<NodeType>> {
    List<EdgeType> findPath(NodeType start, NodeType destination);
}

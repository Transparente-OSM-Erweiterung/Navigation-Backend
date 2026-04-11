package de.dhbwka.navigation;

public interface GraphWithWidth<NodeType extends Node, EdgeType extends Edge<NodeType>> extends Graph<NodeType, EdgeType> {
    double getWidth(String fromId, String toId);
}

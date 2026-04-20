package de.dhbwka.navigation;

public interface WidthedEdge<NodeType extends Node> extends Edge<NodeType> {
    double getWidth();
}

package de.dhbwka.navigation;

public interface Edge <NodeType extends Node> {
    NodeType getOrigin();
    NodeType getDestination();
}

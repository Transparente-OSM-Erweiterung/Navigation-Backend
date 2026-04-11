package de.dhbwka.navigation;

public interface EdgeScorer<NodeType extends Node, EdgeType extends Edge<NodeType>> {
    double calculateScore(EdgeType edge);
}

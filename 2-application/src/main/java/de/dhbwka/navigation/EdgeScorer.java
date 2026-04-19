package de.dhbwka.navigation;

public interface EdgeScorer<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > {
    double calculateScore(EdgeType edge);
}

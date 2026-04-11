package de.dhbwka.navigation;

import java.util.Optional;

public interface GraphBuilder<NodeType extends Node, EdgeType extends Edge<NodeType>> {
    void addNode(NodeType node);

    void addEdge(EdgeType edge);

    Optional<NodeType> getNode(String id);


    @Deprecated
    void addEdge(String fromId, String toId, boolean bidirectional, double streetWidth);

    @Deprecated
    default void addEdge(String fromId, String toId, double streetWidth) {
        addEdge(fromId, toId, true, streetWidth);
    }

}

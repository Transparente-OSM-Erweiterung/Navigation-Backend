package de.dhbwka.navigation.adapter;

import de.dhbwka.navigation.abstraction.Node;

public interface GraphBuilder<T extends Node> {
    void addNode(T node);

    void addEdge(String fromId, String toId, boolean bidirectional);
    default void addEdge(String fromId, String toId) {
        addEdge(fromId, toId, true);
    }

}

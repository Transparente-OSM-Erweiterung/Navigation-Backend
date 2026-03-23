package de.dhbwka.navigation;

public interface GraphBuilder<T extends Node> {
    void addNode(T node);

    void addEdge(String fromId, String toId, boolean bidirectional, double streetwidth);

    default void addEdge(String fromId, String toId, double streetwidth) {
        addEdge(fromId, toId, true, streetwidth);
    }

}

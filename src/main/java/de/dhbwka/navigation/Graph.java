package de.dhbwka.navigation;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record Graph<T extends GraphNode>(Set<T> nodes, Map<String, Set<String>> connections) {
    public T getNode(String id) {
        return nodes.stream()
                .filter(node -> node.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No node found with ID"));
    }

    public Set<T> getConnections(T node) {
        return connections.get(node.id()).stream()
                .map(this::getNode)
                .collect(Collectors.toSet());
    }
}

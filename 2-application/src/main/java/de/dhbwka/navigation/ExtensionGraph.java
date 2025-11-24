package de.dhbwka.navigation;

import java.util.*;

public class ExtensionGraph<T extends GeoNode> implements Graph<T> {
    private final Graph<T> graph;
    private final ExtensionNodeFactory<? extends T> factory;

    public ExtensionGraph(Graph<T> graph, ExtensionNodeFactory<T> factory) {
        this.graph = graph;
        this.factory = factory;
    }

    @Override
    public Optional<T> getNode(String id) {
        if (!isExtensionId(id)) {
            return graph.getNode(id);
        }
        return Optional.empty();
    }

    @Override
    public Collection<? extends T> getNeighbors(T node) {
        if (!isExtensionId(node.getId())) {
            Collection<? extends T> delegated = graph.getNeighbors(node);

            List<T> merged = new ArrayList<>();
            for (T neighbor : delegated) {
                merged.add(neighbor);
                String id = node.getId();
                merged.add(factory.createNode(id + "a"));
            }
            return merged;
        }
        return graph.getNeighbors(node);
    }

    private static boolean isExtensionId(String id) {
        return id.matches(".*[a-z].*");
    }

}

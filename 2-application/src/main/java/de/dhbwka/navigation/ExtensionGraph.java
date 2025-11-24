package de.dhbwka.navigation;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

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
            Collection<? extends T> delegated = sortDegree(graph.getNeighbors(node),node);

            List<T> merged = new ArrayList<>();
            int i = 0;
            for (T neighbor : delegated) {
                merged.add(neighbor);
                String id = node.getId();
                char append = (char) (97 + i);
                merged.add(factory.createNode(id + append));
                i++;
            }
            return merged;
        }
        return graph.getNeighbors(node);
    }

    private static boolean isExtensionId(String id) {
        return id.matches(".*[a-z].*");
    }

    public static <E extends GeoNode> List<E> sortDegree(Collection<? extends E> delegates, E origin
    ) {

        double originX = origin.getLatitude();
        double originY = origin.getLongitude();
        double posNeighbourX = 0.;
        double posNeighbourY = 0.;
        double vecX;
        double vecY;
        Map<E,Double> sortedList = new HashMap<>();

        for (E neighbor : delegates) {
            posNeighbourX = neighbor.getLatitude();
            posNeighbourY = neighbor.getLongitude();
            vecX = posNeighbourX - originX;
            vecY = posNeighbourY - originY;
            double degree = Math.abs((Math.toDegrees(Math.atan2(-vecY,-vecX)) - 270) % 360);
            sortedList.put(neighbor,degree);
        }

        sortedList = sortedList.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new
                ));

        return sortedList.keySet().stream().toList();
    }

    public static double calcDeg(GeoNode origin, GeoNode dest) {
        double originX = origin.getLatitude();
        double originY = origin.getLongitude();
        double posNeighbourX = 0.;
        double posNeighbourY = 0.;
        double vecX;
        double vecY;

        posNeighbourX = dest.getLatitude();
        posNeighbourY = dest.getLongitude();
        vecX = posNeighbourX - originX;
        vecY = posNeighbourY - originY;
        return Math.abs((Math.toDegrees(Math.atan2(-vecY,-vecX)) - 270) % 360);
    }

}

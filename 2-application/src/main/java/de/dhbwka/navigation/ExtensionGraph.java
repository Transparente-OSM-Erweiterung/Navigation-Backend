package de.dhbwka.navigation;

import java.util.*;
import java.util.stream.Collectors;

public class ExtensionGraph<T extends GeoNode> implements Graph<T> {
    private final Graph<T> graph;

    private final ExtensionNodeFactory<T> factory;

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
    public Collection<T> getNeighbors(T node) {
        if (!isExtensionId(node.getId())) {
            Collection<T> delegated = sortDegree(graph.getNeighbors(node), node);

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

    private Collection<T> getNeighborsOfExtensionNode(T node) {
        List<T> result = new ArrayList<>();
        T mainNode = graph.getNode(node.getId().replaceAll("[^0-9]", "")).orElseThrow();
        result.add(mainNode);
        List<T> neighboursMainNode = List.copyOf(getNeighbors(mainNode));

        for (int i = 0; i < neighboursMainNode.size(); i++) {
            if (neighboursMainNode.get(i).getId().equals(node.getId())) {
                T extNeighbourLeft = neighboursMainNode.get((i - 2 + neighboursMainNode.size()) % neighboursMainNode.size());
                T extNeighbourRight = neighboursMainNode.get((i + 2) % neighboursMainNode.size());
                result.add(extNeighbourLeft);
                if (!extNeighbourLeft.getId().equals(extNeighbourRight.getId())) {
                    result.add(extNeighbourRight);
                }
                break;
            }
        }
        double angleOfExtension = calcDeg(mainNode, node);
        for (int i = 0; i < neighboursMainNode.size(); i++) {
            double angleOfNeighbour = calcDeg(mainNode, neighboursMainNode.get(i));
            if (angleOfNeighbour >= angleOfExtension) {
                T prev = neighboursMainNode.get((i+neighboursMainNode.size()-1)%neighboursMainNode.size());
                T next = neighboursMainNode.get((i+1)%neighboursMainNode.size());
                List<T> prevNeighbours = new ArrayList<>(getNeighbors(prev));
                List<T> nextNeighbours = new ArrayList<>(getNeighbors(next));


                
                for (int iprev = 0; iprev < prevNeighbours.size(); iprev++) {
                    if (prevNeighbours.get(iprev).getId().equals(mainNode.getId())) {
                        T extNeighbourLeft = prevNeighbours.get((iprev - 1 + prevNeighbours.size()) % neighboursMainNode.size());
                        result.add(extNeighbourLeft);

                        break;
                    }
                }

                for (int inext = 0; inext < nextNeighbours.size(); inext++) {
                    if (nextNeighbours.get(inext).getId().equals(mainNode.getId())) {
                        T extNeighbourRight = nextNeighbours.get((inext + 1) % nextNeighbours.size());
                        result.add(extNeighbourRight);

                        break;
                    }
                }

                break;
            }
        }
        return result;
    }

    private static boolean isExtensionId(String id) {
        return id.matches(".*[a-z].*");
    }

    public static <E extends GeoNode> List<E> sortDegree(Collection<E> delegates, E origin
    ) {

        double originX = origin.getLatitude();
        double originY = origin.getLongitude();
        double posNeighbourX = 0.;
        double posNeighbourY = 0.;
        double vecX;
        double vecY;
        Map<E, Double> sortedList = new HashMap<>();

        for (E neighbor : delegates) {
            posNeighbourX = neighbor.getLatitude();
            posNeighbourY = neighbor.getLongitude();
            vecX = posNeighbourX - originX;
            vecY = posNeighbourY - originY;
            double degree = Math.abs((Math.toDegrees(Math.atan2(-vecY, -vecX)) - 270) % 360);
            sortedList.put(neighbor, degree);
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
        return Math.abs((Math.toDegrees(Math.atan2(-vecY, -vecX)) - 270) % 360);
    }

}

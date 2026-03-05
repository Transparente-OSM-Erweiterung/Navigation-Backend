package de.dhbwka.navigation;

import java.util.*;
import java.util.stream.Collectors;

public class ExtensionGraph implements Graph<GeoNode> {
    private final Graph<GeoNode> graph;

    private final double STREET_WIDTH = 2;


    public ExtensionGraph(Graph<GeoNode> graph) {
        this.graph = graph;
    }

    @Override
    public Optional<GeoNode> getNode(String id) {
        if (!isExtensionId(id)) {
            return graph.getNode(id);
        }
        //TODO
        return Optional.empty();
    }

    @Override
    public Collection<GeoNode> getNeighbors(GeoNode node) {
        if (!isExtensionId(node.getId())) {
            List<GeoNode> delegated = sortDegree(graph.getNeighbors(node), node);
            Vec2 origin = new Vec2(node.getLatitude(), node.getLongitude());
            List<GeoNode> merged = new ArrayList<>();

            for (int i = 0; i < delegated.size(); i++) {
                merged.add(delegated.get(i));
                String id = node.getId();
                char append = (char) (97 + i);

                Vec2 u1 = new Vec2(
                    delegated.get(i).getLatitude() - node.getLatitude(),
                    delegated.get(i).getLongitude() - node.getLongitude()
                ).normalized();
                Vec2 u2 = new Vec2(
                        delegated.get((i + 1) % delegated.size()).getLatitude() - node.getLatitude(),
                        delegated.get((i + 1) % delegated.size()).getLongitude() - node.getLongitude()
                ).normalized();

                Vec2 normal1 = u1.rot90right();
                Vec2 normal2 = u2.rot90left();

                Vec2 p1 = origin.add(normal1.scale(STREET_WIDTH / 2));
                Vec2 p2 = origin.add(normal2.scale(STREET_WIDTH / 2));

                // g: point1 + u1 * s
                // f: point2 + u2 * t

                //(u1.x, -u2.x) * (s) = (p2.x - p1.x)
                //(u1.y, -u2.y)   (t)   (p2.y - p1.y)

                Mat2 mat = new Mat2(
                        u1.x, -u2.x,
                        u1.y, -u2.y
                );
                Vec2 rhs = p2.add(p1.negated());

                Vec2 intersection;
                try {
                    double s = Cramer2Solve.solveX(mat, rhs);
                    intersection = p1.add(u1.scale(s));
                } catch (IllegalArgumentException e) {
                    intersection = p1.add(p2).scale(0.5);
                }

                merged.add(new ExtensionNode(id+append, intersection.x, intersection.y));

            }
            return merged;
        }
        return getNeighborsOfExtensionNode(node);
    }
    private Collection<GeoNode> getNeighborsOfExtensionNode(GeoNode node) {
        List<GeoNode> result = new ArrayList<>();
        GeoNode mainNode = graph.getNode(node.getId().replaceAll("[^0-9]", "")).orElseThrow();
        result.add(mainNode);
        List<GeoNode> neighboursMainNode = List.copyOf(getNeighbors(mainNode));

        for (int i = 0; i < neighboursMainNode.size(); i++) {
            if (neighboursMainNode.get(i).getId().equals(node.getId())) {
                GeoNode extNeighbourLeft = neighboursMainNode.get((i - 2 + neighboursMainNode.size()) % neighboursMainNode.size());
                GeoNode extNeighbourRight = neighboursMainNode.get((i + 2) % neighboursMainNode.size());
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
                GeoNode prev = neighboursMainNode.get((i+neighboursMainNode.size()-1)%neighboursMainNode.size());
                GeoNode next = neighboursMainNode.get((i+1)%neighboursMainNode.size());
                List<GeoNode> prevNeighbours = new ArrayList<>(getNeighbors(prev));
                List<GeoNode> nextNeighbours = new ArrayList<>(getNeighbors(next));


                
                for (int iprev = 0; iprev < prevNeighbours.size(); iprev++) {
                    if (prevNeighbours.get(iprev).getId().equals(mainNode.getId())) {
                        GeoNode extNeighbourLeft = prevNeighbours.get((iprev - 1 + prevNeighbours.size()) % prevNeighbours.size());
                        result.add(extNeighbourLeft);

                        break;
                    }
                }

                for (int inext = 0; inext < nextNeighbours.size(); inext++) {
                    if (nextNeighbours.get(inext).getId().equals(mainNode.getId())) {
                        GeoNode extNeighbourRight = nextNeighbours.get((inext + 1) % nextNeighbours.size());
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

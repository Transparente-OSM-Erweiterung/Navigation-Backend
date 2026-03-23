package de.dhbwka.navigation;

import java.util.*;

public class ExtensionGraph implements Graph<GeoNode>{
    private final GraphWithWidth<GeoNode> graph;

    private final double STREET_WIDTH = 0.00002;


    public ExtensionGraph(GraphWithWidth<GeoNode> graph) {
        this.graph = graph;
    }


    @Override
    public Optional<GeoNode> getNode(String id) {
        return graph.getNode(id);
    }

    @Override
    public Collection<GeoNode> getNeighbors(GeoNode node) {
        Compound<GeoNode> nb =
                isExtensionId(node.getId())
                ?getNeighborsOfExtensionNode(node)
                :getNeighborsOfBaseNode(node);
        List<GeoNode> ret = new ArrayList<>(nb.baseNodes);
        ret.addAll(nb.extenesionNodes);
        return ret;
    }

    private Compound<GeoNode> getNeighborsOfBaseNode(GeoNode node) {
        if(isExtensionId(node.getId())) throw new IllegalArgumentException();
        Compound<GeoNode> compound = new Compound<>(new ArrayList<>(), new ArrayList<>());
        compound.baseNodes.addAll(graph.getNeighbors(node));
        compound.baseNodes.sort(new DegreeComparator<>(node, 0));

        Vec2 origin = new Vec2(node.getLatitude(), node.getLongitude());

        for (int i = 0; i < compound.baseNodes.size(); i++) {
            Vec2 u1 = new Vec2(
                    compound.baseNodes.get(i).getLatitude(),
                    compound.baseNodes.get(i).getLongitude())
                    .add(origin.scale(-1))
                    .normalized();
            Vec2 u2 = new Vec2(
                    compound.baseNodes.get((i + 1) % compound.baseNodes.size()).getLatitude(),
                    compound.baseNodes.get((i + 1) % compound.baseNodes.size()).getLongitude())
                    .add(origin.scale(-1))
                    .normalized();

            Vec2 normal1 = u1.rot90right();
            Vec2 normal2 = u2.rot90left();

            Vec2 p1 = origin.add(
                    graph.getWidth(node.getId(),
                            compound.baseNodes.get(i).getId())
            );

            Vec2 p2 = origin.add(
                    graph.getWidth(node.getId(),
                            compound.baseNodes.get((i + 1) % compound.baseNodes.size()).getId())
            );

            //Vec2 p1 = origin.add(normal1.scale(STREET_WIDTH / 2));
            //Vec2 p2 = origin.add(normal2.scale(STREET_WIDTH / 2));

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
            compound.extenesionNodes.add(new ExtensionNode(node.getId() + (char)('a' + i), intersection.x, intersection.y));
        }
        if(!compound.baseNodes.isEmpty()) {
            compound.extenesionNodes.sort(new DegreeComparator<>(node, calcDeg(node, compound.baseNodes.getFirst())));
        }
        return compound;
    }

    private Compound<GeoNode> getNeighborsOfExtensionNode(GeoNode node) {
        if(!isExtensionId(node.getId())) throw new IllegalArgumentException();
        Compound<GeoNode> compound = new Compound<>(new ArrayList<>(), new ArrayList<>());
        GeoNode parent = graph.getNode(node.getId().replaceAll("[^0-9]", "")).orElseThrow();
        int offset = node.getId().charAt(node.getId().length() - 1) - 'a';
        compound.baseNodes.add(parent);

        Compound<GeoNode> parentNeighbors = getNeighborsOfBaseNode(parent);

        if(parentNeighbors.extenesionNodes.size() > 1) {
            for (int i = 0; i < parentNeighbors.extenesionNodes.size(); i++) {
                if(parentNeighbors.extenesionNodes.get(i).getId().equals(node.getId())){
                    GeoNode left = parentNeighbors.extenesionNodes.get((i - 1 + parentNeighbors.extenesionNodes.size()) % parentNeighbors.extenesionNodes.size());
                    GeoNode right = parentNeighbors.extenesionNodes.get((i + 1) % parentNeighbors.extenesionNodes.size());
                    compound.extenesionNodes.add(left);
                    if (left != right) {
                        compound.extenesionNodes.add(parentNeighbors.extenesionNodes.get((i + 1) % parentNeighbors.extenesionNodes.size()));
                    }
                    break;
                }
            }
        }

        List<GeoNode>  matchingBaseNeighboursOfParent = List.of(
                parentNeighbors.baseNodes.get(offset),
                parentNeighbors.baseNodes.get((offset + 1) % parentNeighbors.baseNodes.size())
        ); // Double entries are intended as single base edged ExtensionNeighbours also have 2 ExtensionNeighbors, in this case twice from the same base node.

        // Special Case of 2 Nodes Connected to just each other but nowhere else where Correct would be only 1 extension neighbor but calculated are being 2 times the same neighbor gets ignored here.

        for (int i = 0; i < matchingBaseNeighboursOfParent.size(); i++) {
            GeoNode matchingBaseNeighbourOfParent = matchingBaseNeighboursOfParent.get(i);
            Compound<GeoNode> neighboursAroundMatchingBaseNeighbourOfParent = getNeighborsOfBaseNode(matchingBaseNeighbourOfParent);
            for (int j = 0; j < neighboursAroundMatchingBaseNeighbourOfParent.baseNodes.size(); j++) {
                if (neighboursAroundMatchingBaseNeighbourOfParent.baseNodes.get(j).getId().equals(parent.getId())) {
                    compound.extenesionNodes.add(
                            neighboursAroundMatchingBaseNeighbourOfParent.extenesionNodes.get(
                                    (j + i - 1 + neighboursAroundMatchingBaseNeighbourOfParent.extenesionNodes.size())
                                            % neighboursAroundMatchingBaseNeighbourOfParent.extenesionNodes.size()
                            ));
                }
            }
        }

        return compound;
    }

    private static boolean isExtensionId(String id) {
        return id.matches("[0-9]*[a-z]");
    }

    public static<E extends GeoNode> double calcDeg(E origin, E dest, double offset) {
        Vec2 originVec = new Vec2(origin.getLatitude(),origin.getLongitude());
        Vec2 destVec = new Vec2(dest.getLatitude(), dest.getLongitude());

        Vec2 delta = destVec.add(originVec.scale(-1));

        return Math.abs((Math.toDegrees(Math.atan2(-delta.y, -delta.x)) - 270 + offset) % 360);
    }

    public static<E extends GeoNode> double calcDeg(E origin, E dest) {
        return calcDeg(origin, dest, 0);
    }

    private record Compound<E extends GeoNode> (List<E> baseNodes, List<E> extenesionNodes){}

    private record DegreeComparator<E extends GeoNode>(E origin, double offset) implements Comparator<E> {
        @Override
        public int compare(E e1, E e2) {
            return Comparator.comparingDouble((E n) -> calcDeg(origin, n, offset))
                    .compare(e1, e2);
        }
    }
}

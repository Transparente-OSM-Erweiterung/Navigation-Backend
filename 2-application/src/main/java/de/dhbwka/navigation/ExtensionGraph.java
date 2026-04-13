package de.dhbwka.navigation;

import java.util.*;

public class ExtensionGraph implements Graph<GeoNode, GeoEdge<GeoNode>>{
    private final GraphWithWidth<GeoNode, GeoEdge<GeoNode>> graph;


    public ExtensionGraph(GraphWithWidth<GeoNode, GeoEdge<GeoNode>> graph) {
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

    @Override
    public Collection<GeoEdge<GeoNode>> getEdgesFrom(GeoNode node) {
        Compound2<GeoNode, GeoEdge<GeoNode>> neighbouringEdges =
                isExtensionId(node.getId())
                ?getEdgesOfExtensionNode(node)
                :getEdgesOfBaseNode(node);
        List<GeoEdge<GeoNode>> neighBouringEdgeList = new ArrayList<>(neighbouringEdges.baseEdges);
        neighBouringEdgeList.addAll(neighbouringEdges.extensionEdges);
        return neighBouringEdgeList;
    }

    private Compound2<GeoNode, GeoEdge<GeoNode>> getEdgesOfBaseNode(GeoNode node) {
        if(isExtensionId(node.getId())) throw new IllegalArgumentException();
        Compound2<GeoNode, GeoEdge<GeoNode>> compound = new Compound2<>(new ArrayList<>(), new ArrayList<>());

        compound.baseEdges.addAll(graph.getEdgesFrom(node));
        compound.baseEdges.sort(new DegreeComparator2<>(0));

        Vec2 projectionReference = node.toVec2();
        Vec2 localOrigin = Projection.projectToLocal(projectionReference, projectionReference);

        for (int i = 0; i < compound.baseEdges.size(); i++) {
            Vec2 localNeighbor1 = Projection.projectToLocal(
                    compound.baseEdges.get(i).getDestination().toVec2(),
                    projectionReference
            );
            Vec2 localNeighbor2 = Projection.projectToLocal(
                    compound.baseEdges.get((i + 1) % compound.baseEdges.size()).getDestination().toVec2(),
                    projectionReference
            );

            Vec2 u1 = localNeighbor1.add(localOrigin.negated()).normalized();
            Vec2 u2 = localNeighbor2.add(localOrigin.negated()).normalized();

            Vec2 normal1 = u1.rot90right();
            Vec2 normal2 = u2.rot90left();

            Vec2 p1 = localOrigin.add(normal1.scale(
                    0.5 * graph.getWidth(
                            node.getId(),
                            compound.baseEdges.get(i).getDestination().getId()
                    )
            ));
            Vec2 p2 = localOrigin.add(normal2.scale(
                    0.5 * graph.getWidth(
                            node.getId(),
                            compound.baseEdges.get((i + 1) % compound.baseEdges.size()).getDestination().getId()
                    )
            ));

            // g: point1 + u1 * s
            // f: point2 + u2 * t

            //(u1.x, -u2.x) * (s) = (p2.x - p1.x)
            //(u1.y, -u2.y)   (t)   (p2.y - p1.y)

            Mat2 mat = new Mat2(
                    u1.x, -u2.x,
                    u1.y, -u2.y
            );
            Vec2 rhs = p2.add(p1.negated());

            Vec2 localIntersection;
            try {
                double s = Cramer2Solve.solveX(mat, rhs);
                localIntersection = p1.add(u1.scale(s));
            } catch (IllegalArgumentException e) {
                localIntersection = p1.add(p2).scale(0.5);
            }

            Vec2 globalIntersection = Projection.unprojectToGlobal(localIntersection, projectionReference);

            compound.extensionEdges.add(
                    new ExtensionEdge<>(
                            node,
                            new ExtensionNode(
                                    node.getId() + indexToIdAppender(i),
                                    globalIntersection.x,
                                    globalIntersection.y
                            ),
                            ExtensionEdgeType.BASE_TO_EXTENSION
                    ));
        }

        if(!compound.baseEdges.isEmpty()) {
            compound.extensionEdges.sort(new DegreeComparator2<>(calcDeg(node, compound.extensionEdges.getFirst().getDestination())));
        }
        return compound;
    }

    private Compound2<GeoNode, GeoEdge<GeoNode>> getEdgesOfExtensionNode(GeoNode node) {
        if(!isExtensionId(node.getId())) throw new IllegalArgumentException();
        Compound2<GeoNode, GeoEdge<GeoNode>> compound = new Compound2<>(new ArrayList<>(), new ArrayList<>());
        GeoNode parent = graph.getNode(node.getId().replaceAll("[^0-9]", "")).orElseThrow();
        int offset = idAppenderToIndex(node.getId().replaceAll("[0-9]+", ""));
        compound.baseEdges.add(new ExtensionEdge<>(node, parent, ExtensionEdgeType.EXTENSION_TO_BASE));

        Compound2<GeoNode, GeoEdge<GeoNode>> parentNeighbors = getEdgesOfBaseNode(parent);

        if(parentNeighbors.extensionEdges.size() > 1) {
            for (int i = 0; i < parentNeighbors.extensionEdges.size(); i++) {
                if(parentNeighbors.extensionEdges.get(i).getDestination().getId().equals(node.getId())){
                    GeoNode left = parentNeighbors.extensionEdges.get((i - 1 + parentNeighbors.extensionEdges.size()) % parentNeighbors.extensionEdges.size()).getDestination();
                    GeoNode right = parentNeighbors.extensionEdges.get((i + 1) % parentNeighbors.extensionEdges.size()).getDestination();
                    compound.extensionEdges.add(
                            new ExtensionEdge<>(
                                    node,
                                    left,
                                    ExtensionEdgeType.EXTENSION_TO_EXTENSION_CROSSING_STREET
                            ));
                    if (left != right) {
                        compound.extensionEdges.add(
                                new ExtensionEdge<>(
                                        node,
                                        parentNeighbors.extensionEdges.get(
                                                (i + 1) % parentNeighbors.extensionEdges.size()
                                        ).getDestination(),
                                        ExtensionEdgeType.EXTENSION_TO_EXTENSION_CROSSING_STREET
                                ));
                    }
                    break;
                }
            }
        }

        List<GeoEdge<GeoNode>>  matchingBaseNeighboursOfParent = List.of(
                parentNeighbors.baseEdges.get(offset),
                parentNeighbors.baseEdges.get((offset + 1) % parentNeighbors.baseEdges.size())
        ); // Double entries are intended as single base edged ExtensionNeighbours also have 2 ExtensionNeighbors, in this case twice from the same base node.

        // Special Case of 2 Nodes Connected to just each other but nowhere else where Correct would be only 1 extension neighbor but calculated are being 2 times the same neighbor gets ignored here.

        for (int i = 0; i < matchingBaseNeighboursOfParent.size(); i++) {
            GeoEdge<GeoNode> matchingBaseNeighbourOfParent = matchingBaseNeighboursOfParent.get(i);
            Compound2<GeoNode, GeoEdge<GeoNode>> neighboursAroundMatchingBaseNeighbourOfParent = getEdgesOfBaseNode(matchingBaseNeighbourOfParent.getDestination());
            for (int j = 0; j < neighboursAroundMatchingBaseNeighbourOfParent.baseEdges.size(); j++) {
                if (neighboursAroundMatchingBaseNeighbourOfParent.baseEdges.get(j).getDestination().getId().equals(parent.getId())) {
                    compound.extensionEdges.add(
                            new ExtensionEdge<>(
                                    node,
                                    neighboursAroundMatchingBaseNeighbourOfParent.extensionEdges.get(
                                            (j + i - 1 + neighboursAroundMatchingBaseNeighbourOfParent.extensionEdges.size())
                                                    % neighboursAroundMatchingBaseNeighbourOfParent.extensionEdges.size()
                                    ).getDestination(),
                                    ExtensionEdgeType.EXTENSION_TO_EXTENSION_ALONG_STREET
                            ));
                }
            }
        }

        return compound;
    }

    private Compound<GeoNode> getNeighborsOfBaseNode(GeoNode node) {
        if(isExtensionId(node.getId())) throw new IllegalArgumentException();
        Compound<GeoNode> compound = new Compound<>(new ArrayList<>(), new ArrayList<>());
        compound.baseNodes.addAll(graph.getNeighbors(node));
        compound.baseNodes.sort(new DegreeComparator<>(node, 0));

        Vec2 projectionReference = node.toVec2();
        Vec2 localOrigin = Projection.projectToLocal(projectionReference, projectionReference);

        for (int i = 0; i < compound.baseNodes.size(); i++) {
            Vec2 localNeighbor1 = Projection.projectToLocal(
                    compound.baseNodes.get(i).toVec2(),
                    projectionReference
            );
            Vec2 localNeighbor2 = Projection.projectToLocal(
                    compound.baseNodes.get((i + 1) % compound.baseNodes.size()).toVec2(),
                    projectionReference
            );

            Vec2 u1 = localNeighbor1.add(localOrigin.negated()).normalized();
            Vec2 u2 = localNeighbor2.add(localOrigin.negated()).normalized();

            Vec2 normal1 = u1.rot90right();
            Vec2 normal2 = u2.rot90left();

            Vec2 p1 = localOrigin.add(normal1.scale(
                    0.5 * graph.getWidth(
                            node.getId(),
                            compound.baseNodes.get(i).getId()
                    )
            ));
            Vec2 p2 = localOrigin.add(normal2.scale(
                    0.5 * graph.getWidth(
                            node.getId(),
                            compound.baseNodes.get((i + 1) % compound.baseNodes.size()).getId()
                    )
            ));

            // g: point1 + u1 * s
            // f: point2 + u2 * t

            //(u1.x, -u2.x) * (s) = (p2.x - p1.x)
            //(u1.y, -u2.y)   (t)   (p2.y - p1.y)

            Mat2 mat = new Mat2(
                    u1.x, -u2.x,
                    u1.y, -u2.y
            );
            Vec2 rhs = p2.add(p1.negated());

            Vec2 localIntersection;
            try {
                double s = Cramer2Solve.solveX(mat, rhs);
                localIntersection = p1.add(u1.scale(s));
            } catch (IllegalArgumentException e) {
                localIntersection = p1.add(p2).scale(0.5);
            }

            Vec2 globalIntersection = Projection.unprojectToGlobal(localIntersection, projectionReference);

            compound.extenesionNodes.add(new ExtensionNode(
                    node.getId() + indexToIdAppender(i),
                    globalIntersection.x,
                    globalIntersection.y)
            );
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
        int offset = idAppenderToIndex(node.getId().replaceAll("[0-9]+", ""));
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

    public static String indexToIdAppender(int index) {
        if (index == 0) {
            return "a";
        }

        StringBuilder sb = new StringBuilder();

        while (index > 0) {
            int remainder = index % 26;
            char c = (char) ('a' + remainder);
            sb.append(c);
            index = index / 26;
        }

        return sb.reverse().toString();
    }

    public static int idAppenderToIndex(String idAppender) {
        if (idAppender == null || idAppender.isEmpty()) {
            return 0;
        }

        int result = 0;
        for (int i = 0; i < idAppender.length(); i++) {
            char c = idAppender.charAt(i);
            int value = c - 'a';

            result = result * 26 + value;
        }
        return result;
    }

    public static<E extends GeoNode> double calcDeg(E origin, E dest, double offset) {
        Vec2 originVec = origin.toVec2();
        Vec2 destVec = dest.toVec2();

        Vec2 localOrigin = Projection.projectToLocal(originVec, originVec);
        Vec2 localDest = Projection.projectToLocal(destVec, originVec);

        Vec2 dir = localDest.add(localOrigin.negated());

        double angleDegrees = Math.toDegrees(Math.atan2(-dir.y, -dir.x));

        return Math.abs((angleDegrees - 270 + offset) % 360);
    }

    public static<E extends GeoNode> double calcDeg(E origin, E dest) {
        return calcDeg(origin, dest, 0);
    }

    private record Compound<E extends GeoNode> (List<E> baseNodes, List<E> extenesionNodes){}

    private record Compound2<NodeType extends Node, EdgeType extends Edge<NodeType>> (List<EdgeType> baseEdges, List<EdgeType> extensionEdges){}

    private record DegreeComparator<E extends GeoNode>(E origin, double offset) implements Comparator<E> {
        @Override
        public int compare(E e1, E e2) {
            return Comparator.comparingDouble((E n) -> calcDeg(origin, n, offset))
                    .compare(e1, e2);
        }
    }

    private record DegreeComparator2<NodeType extends GeoNode, EdgeType extends GeoEdge<NodeType>>(double offset) implements Comparator<EdgeType> {

        @Override
        public int compare(EdgeType thisEdge, EdgeType otherEdge) {
            return Comparator.comparingDouble(
                    (EdgeType edge) -> calcDeg(
                            edge.getOrigin(),
                            edge.getDestination(),
                            offset
                    )
            ).compare(thisEdge, otherEdge);
        }
    }
}

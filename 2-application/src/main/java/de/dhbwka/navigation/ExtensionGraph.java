package de.dhbwka.navigation;

import java.util.*;
import java.util.function.Function;

public class ExtensionGraph<
        InputNodeType extends GeoNode,
        InputEdgeType extends CategorizedGeoEdge<? extends InputNodeType, ExtensionEdgeCategory>
                & WidthedEdge<? extends InputNodeType>
    > implements Graph<GeoNode, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> {
    private final Graph<? extends InputNodeType, ? extends InputEdgeType> graph;


    public ExtensionGraph(Graph<
            ? extends InputNodeType,
            ? extends InputEdgeType
        > graph) {
        this.graph = graph;
    }


    @Override
    public Optional<GeoNode> getNode(String nodeId) {
        if(isExtensionId(nodeId)) {
            return getExtensionNode(nodeId);
        }
        return graph.getNode(nodeId).map(Function.identity());
    }

    private Optional<GeoNode> getExtensionNode(String nodeId) {
        if(!isExtensionId(nodeId)) throw new IllegalArgumentException();
        String parentId = nodeId.replaceAll("[^0-9]", "");
        int index = idAppenderToIndex(nodeId.replaceAll("[0-9]+", ""));

        return graph.getNode(parentId).flatMap(parent -> {
            List<? extends InputEdgeType> sortedEdges = graph.getEdgesFrom(parentId)
                    .stream()
                    .sorted(new DegreeComparator2<>(0))
                    .toList();
            if (index < 0 || index >= sortedEdges.size()){
                return Optional.empty();
            }

            InputEdgeType edge1 = sortedEdges.get(index);
            InputEdgeType edge2 = sortedEdges.get((index + 1) % sortedEdges.size());

            return Optional.of(createExtensionNode(nodeId, parent, edge1, edge2));
        });
    }

    private ExtensionNode createExtensionNode(String id, GeoNode center, WidthedEdge<? extends GeoNode> e1, WidthedEdge<? extends GeoNode> e2) {
        Vec2 ref = center.toVec2();
        Vec2 localOrigin = Projection.projectToLocal(ref, ref);

        Vec2 loc1 = Projection.projectToLocal(e1.getDestination().toVec2(), ref);
        Vec2 loc2 = Projection.projectToLocal(e2.getDestination().toVec2(), ref);

        Vec2 u1 = loc1.add(localOrigin.negated()).normalized();
        Vec2 u2 = loc2.add(localOrigin.negated()).normalized();

        double w1 = e1.getWidth();
        double w2 = e2.getWidth();

        Vec2 p1 = localOrigin.add(u1.rot90right().scale(0.5 * w1));
        Vec2 p2 = localOrigin.add(u2.rot90left().scale(0.5 * w2));

        // Cramer's Rule for 2x2 Matrices

        // g: point1 + u1 * s
        // f: point2 + u2 * t

        //(u1.x, -u2.x) * (s) = (p2.x - p1.x)
        //(u1.y, -u2.y)   (t)   (p2.y - p1.y)
        Mat2 mat = new Mat2(u1.x, -u2.x, u1.y, -u2.y);
        Vec2 rhs = p2.add(p1.negated());

        Vec2 intersection;
        try {
            double s = Cramer2Solve.solveX(mat, rhs);
            intersection = p1.add(u1.scale(s));
        } catch (IllegalArgumentException e) {
            // Fallback for parallel lines
            intersection = p1.add(p2).scale(0.5);
        }

        Vec2 global = Projection.unprojectToGlobal(intersection, ref);
        return new ExtensionNode(id, global.x, global.y);
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
    public Collection<CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> getEdgesFrom(String nodeId) {
        Compound2<GeoNode, InputEdgeType, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> neighbouringEdges =
                isExtensionId(nodeId)
                ?getEdgesOfExtensionNode(nodeId)
                :getEdgesOfBaseNode(nodeId);
        List<CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> neighBouringEdgeList = new ArrayList<>(neighbouringEdges.baseEdges);
        neighBouringEdgeList.addAll(neighbouringEdges.extensionEdges);
        return neighBouringEdgeList;
    }

    private Compound2<GeoNode, InputEdgeType, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> getEdgesOfBaseNode(String nodeId) {
        if(isExtensionId(nodeId)) throw new IllegalArgumentException();
        GeoNode node = graph.getNode(nodeId).orElseThrow();
        Compound2<GeoNode, InputEdgeType, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> compound = new Compound2<>(new ArrayList<>(), new ArrayList<>());

        compound.baseEdges.addAll(graph.getEdgesFrom(nodeId));
        compound.baseEdges.sort(new DegreeComparator2<>(0));

        for (int i = 0; i < compound.baseEdges.size(); i++) {
            GeoNode extensionNode = createExtensionNode(
                    nodeId + indexToIdAppender(i),
                    node,
                    compound.baseEdges.get(i),
                    compound.baseEdges.get(
                            (i + 1) % compound.baseEdges.size()
                    )
            );

            compound.extensionEdges.add(
                    new ExtensionEdge<>(
                            node,
                            extensionNode,
                            ExtensionEdgeCategory.BASE_TO_EXTENSION
                    ));
        }

        return compound;
    }

    private Compound2<GeoNode, InputEdgeType, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> getEdgesOfExtensionNode(String nodeId) {
        if(!isExtensionId(nodeId)) throw new IllegalArgumentException();
        GeoNode node = getExtensionNode(nodeId).orElseThrow();
        Compound2<GeoNode, InputEdgeType, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> compound = new Compound2<>(new ArrayList<>(), new ArrayList<>());
        GeoNode parent = graph.getNode(nodeId.replaceAll("[^0-9]", "")).orElseThrow();
        int offset = idAppenderToIndex(nodeId.replaceAll("[0-9]+", ""));
        compound.extensionEdges.add(new ExtensionEdge<>(node, parent, ExtensionEdgeCategory.EXTENSION_TO_BASE));

        Compound2<GeoNode, InputEdgeType, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> parentNeighbors = getEdgesOfBaseNode(parent.getId());

        if(parentNeighbors.extensionEdges.size() > 1) {
            for (int i = 0; i < parentNeighbors.extensionEdges.size(); i++) {
                if(parentNeighbors.extensionEdges.get(i).getDestination().getId().equals(nodeId)){
                    GeoNode left = parentNeighbors.extensionEdges.get((i - 1 + parentNeighbors.extensionEdges.size()) % parentNeighbors.extensionEdges.size()).getDestination();
                    GeoNode right = parentNeighbors.extensionEdges.get((i + 1) % parentNeighbors.extensionEdges.size()).getDestination();
                    compound.extensionEdges.add(
                            new ExtensionEdge<>(
                                    node,
                                    left,
                                    ExtensionEdgeCategory.EXTENSION_TO_EXTENSION_CROSSING_STREET
                            ));
                    if (left != right) {
                        compound.extensionEdges.add(
                                new ExtensionEdge<>(
                                        node,
                                        parentNeighbors.extensionEdges.get(
                                                (i + 1) % parentNeighbors.extensionEdges.size()
                                        ).getDestination(),
                                        ExtensionEdgeCategory.EXTENSION_TO_EXTENSION_CROSSING_STREET
                                ));
                    }
                    break;
                }
            }
        }

        List<GeoEdge<? extends GeoNode>>  matchingBaseNeighboursOfParent = List.of(
                parentNeighbors.baseEdges.get(offset),
                parentNeighbors.baseEdges.get((offset + 1) % parentNeighbors.baseEdges.size())
        ); // Double entries are intended as single base edged ExtensionNeighbours also have 2 ExtensionNeighbors, in this case twice from the same base node.

        // Special Case of 2 Nodes Connected to just each other but nowhere else where Correct would be only 1 extension neighbor but calculated are being 2 times the same neighbor gets ignored here.

        for (int i = 0; i < matchingBaseNeighboursOfParent.size(); i++) {
            GeoEdge<? extends GeoNode> matchingBaseNeighbourOfParent = matchingBaseNeighboursOfParent.get(i);
            Compound2<GeoNode, InputEdgeType, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> neighboursAroundMatchingBaseNeighbourOfParent = getEdgesOfBaseNode(matchingBaseNeighbourOfParent.getDestination().getId());
            for (int j = 0; j < neighboursAroundMatchingBaseNeighbourOfParent.baseEdges.size(); j++) {
                if (neighboursAroundMatchingBaseNeighbourOfParent.baseEdges.get(j).getDestination().getId().equals(parent.getId())) {
                    compound.extensionEdges.add(
                            new ExtensionEdge<>(
                                    node,
                                    neighboursAroundMatchingBaseNeighbourOfParent.extensionEdges.get(
                                            (j + i - 1 + neighboursAroundMatchingBaseNeighbourOfParent.extensionEdges.size())
                                                    % neighboursAroundMatchingBaseNeighbourOfParent.extensionEdges.size()
                                    ).getDestination(),
                                    ExtensionEdgeCategory.EXTENSION_TO_EXTENSION_ALONG_STREET
                            ));
                }
            }
        }

        return compound;
    }

    @Deprecated
    private Compound<GeoNode> getNeighborsOfBaseNode(GeoNode node) {
        if(isExtensionId(node.getId())) throw new IllegalArgumentException();
        Compound<GeoNode> compound = new Compound<>(new ArrayList<>(), new ArrayList<>());
        compound.baseNodes.addAll(graph.getEdgesFrom(node.getId()).stream().map(GeoEdge::getDestination).toList());
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

            int finalI = i;
            Vec2 p1 = localOrigin.add(
                    normal1.scale(
                            0.5 * graph.getEdgesFrom(node.getId())
                                    .stream()
                                    .filter(
                                            (InputEdgeType e) -> compound.baseNodes.get(finalI).getId().equals(e.getDestination().getId())
                                    )
                                    .findAny()
                                    .orElseThrow()
                                    .getWidth()
                    )
            );

             Vec2 p2 = localOrigin.add(
                    normal2.scale(
                            0.5 * graph.getEdgesFrom(node.getId())
                                    .stream()
                                    .filter(
                                            (InputEdgeType e) -> compound.baseNodes.get((finalI + 1) % compound.baseNodes.size()).getId().equals(e.getDestination().getId())
                                    )
                                    .findAny()
                                    .orElseThrow()
                                    .getWidth()
                    )
            );

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

    @Deprecated
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
        return id.matches("[0-9]+[a-z]+");
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

    @Deprecated
    private record Compound<E extends GeoNode> (List<E> baseNodes, List<E> extenesionNodes){}

    private record Compound2<
            NodeType extends Node,
            BaseEdgeType extends Edge<? extends NodeType>,
            ExtensionEdgeType extends Edge<? extends NodeType>
        > (
                List<BaseEdgeType> baseEdges,
                List<ExtensionEdgeType> extensionEdges
    ){

    }

    @Deprecated
    private record DegreeComparator<NodeType extends GeoNode>(NodeType origin, double offset) implements Comparator<NodeType> {
        @Override
        public int compare(NodeType node1, NodeType node2) {
            return Comparator.comparingDouble((NodeType n) -> calcDeg(origin, n, offset))
                    .compare(node1, node2);
        }
    }

    private record DegreeComparator2<NodeType extends GeoNode, EdgeType extends GeoEdge<? extends NodeType>>(double offset) implements Comparator<EdgeType> {

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

package de.dhbwka.navigation.pathfinding;

import de.dhbwka.navigation.graph.edge.GeoEdge;
import de.dhbwka.navigation.graph.node.GeoNode;
import de.dhbwka.navigation.math.Haversine;

public class HaversineEdgeScorer<
        NodeType extends GeoNode,
        EdgeType extends GeoEdge<? extends NodeType>
    > implements EdgeScorer<NodeType, EdgeType> {
    @Override
    public double calculateScore(EdgeType edge) {
        return Haversine.distance(
                edge.getOrigin().toVec2(),
                edge.getDestination().toVec2()
        );
    }
}

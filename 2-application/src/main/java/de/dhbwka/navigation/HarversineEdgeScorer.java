package de.dhbwka.navigation;

public class HarversineEdgeScorer<
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

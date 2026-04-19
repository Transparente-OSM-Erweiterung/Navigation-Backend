package de.dhbwka.navigation;

public class HaversineHeuristic<
        NodeType extends GeoNode
    > implements Heuristic<NodeType> {

    @Override
    public double estimate(NodeType from, NodeType to) {
        return Haversine.distance(from.toVec2(), to.toVec2());
    }
}

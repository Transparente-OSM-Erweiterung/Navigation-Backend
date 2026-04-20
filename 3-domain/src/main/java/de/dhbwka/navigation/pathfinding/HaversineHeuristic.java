package de.dhbwka.navigation.pathfinding;

import de.dhbwka.navigation.graph.node.GeoNode;
import de.dhbwka.navigation.math.Haversine;

public class HaversineHeuristic<
        NodeType extends GeoNode
    > implements Heuristic<NodeType> {

    @Override
    public double estimate(NodeType from, NodeType to) {
        return Haversine.distance(from.toVec2(), to.toVec2());
    }
}

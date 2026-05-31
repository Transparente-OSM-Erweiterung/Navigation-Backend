package de.dhbwka.navigation.pathfinding;

import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.node.VectorNode;
import de.dhbwka.navigation.math.Euclid;

public class EuclideanEdgeScorer<NodeType extends VectorNode, EdgeType extends Edge<? extends NodeType>> implements EdgeScorer<NodeType, EdgeType> {
    @Override
    public double calculateScore(EdgeType edge) {
        return Euclid.distance(edge.getOrigin().getComponents(), edge.getDestination().getComponents());
    }
}

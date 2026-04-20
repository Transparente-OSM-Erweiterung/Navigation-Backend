package de.dhbwka.navigation.extension;

import de.dhbwka.navigation.graph.edge.GeoEdge;
import de.dhbwka.navigation.graph.node.GeoNode;

import java.util.Comparator;

public record DegreeComparator<NodeType extends GeoNode, EdgeType extends GeoEdge<? extends NodeType>>(double offset) implements Comparator<EdgeType> {

        @Override
        public int compare(EdgeType thisEdge, EdgeType otherEdge) {
            return Comparator.comparingDouble(
                    (EdgeType edge) -> ExtensionGraph.calcDeg(
                            edge.getOrigin(),
                            edge.getDestination(),
                            offset
                    )
            ).compare(thisEdge, otherEdge);
        }
    }
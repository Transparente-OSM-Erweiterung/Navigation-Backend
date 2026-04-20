package de.dhbwka.navigation.graph.edge;

import de.dhbwka.navigation.graph.node.GeoNode;

public interface CategorizedGeoEdge<
        NodeType extends GeoNode,
        CategoryType extends EdgeCategory
    > extends GeoEdge<NodeType>,
        CategorizedEdge<NodeType, CategoryType> {
}

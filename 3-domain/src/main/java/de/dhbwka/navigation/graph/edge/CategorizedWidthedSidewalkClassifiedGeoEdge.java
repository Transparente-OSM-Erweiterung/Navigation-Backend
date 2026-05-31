package de.dhbwka.navigation.graph.edge;

import de.dhbwka.navigation.graph.node.GeoNode;

public interface CategorizedWidthedSidewalkClassifiedGeoEdge<
        NodeType extends GeoNode,
        CategoryType extends EdgeCategory
    > extends CategorizedGeoEdge<NodeType, CategoryType>,
        WidthedEdge<NodeType>,
        SideWalkClassified
{
}

package de.dhbwka.navigation;

public interface CategorizedWidthedGeoEdge<
        NodeType extends GeoNode,
        CategoryType extends EdgeCategory
    > extends CategorizedGeoEdge<NodeType, CategoryType>,
        WidthedEdge<NodeType>
{
}

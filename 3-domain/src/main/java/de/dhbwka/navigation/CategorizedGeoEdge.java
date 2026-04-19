package de.dhbwka.navigation;

public interface CategorizedGeoEdge<
        NodeType extends GeoNode,
        CategoryType extends EdgeCategory
    > extends GeoEdge<NodeType>,
        CategorizedEdge<NodeType, CategoryType> {
}

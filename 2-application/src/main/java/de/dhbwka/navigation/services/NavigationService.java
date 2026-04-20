package de.dhbwka.navigation.services;

import de.dhbwka.navigation.*;

import java.util.List;

public class NavigationService {
    private final Graph<GeoNode, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> extensionGraph;

    public NavigationService(Parser<? extends GeoNode, ? extends CategorizedWidthedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> parser) {
        InMemoryGraph<GeoNode, CategorizedWidthedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> graph = new InMemoryGraph<>();
        parser.parse(graph);
        this.extensionGraph = new ExtensionGraph<>(graph);

    }

    public List<CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> calculateRoute(String startId, String destinationId) {
        return new AStarPathFinder<>(
                new ExtensionEdgeCategoryFilter<>(
                        extensionGraph,
                        startId,
                        destinationId
                ),
                new HaversineEdgeScorer<>(),
                new HaversineHeuristic<>()
        ).findPath(startId, destinationId);
    }
}

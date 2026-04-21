package de.dhbwka.navigation.services;

import de.dhbwka.navigation.extension.*;
import de.dhbwka.navigation.extension.filter.ExtensionEdgeCategory;
import de.dhbwka.navigation.extension.filter.ExtensionEdgeCategoryPredicate;
import de.dhbwka.navigation.extension.filter.FilteredGraph;
import de.dhbwka.navigation.graph.Graph;
import de.dhbwka.navigation.graph.edge.CategorizedGeoEdge;
import de.dhbwka.navigation.graph.edge.CategorizedWidthedGeoEdge;
import de.dhbwka.navigation.graph.node.GeoNode;
import de.dhbwka.navigation.pathfinding.AStarPathFinder;
import de.dhbwka.navigation.pathfinding.HaversineEdgeScorer;
import de.dhbwka.navigation.pathfinding.HaversineHeuristic;

import java.util.List;

public class NavigationService {
    private final Graph<GeoNode, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> extensionGraph;

    public NavigationService(Graph<GeoNode, CategorizedWidthedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> graph) {
        this.extensionGraph = new ExtensionGraph<>(graph);
    }

    public List<CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> calculateRoute(String startId, String destinationId) {
        return new AStarPathFinder<>(
                new FilteredGraph<>(
                        extensionGraph,
                        new ExtensionEdgeCategoryPredicate<>(startId, destinationId)
                ),
                new HaversineEdgeScorer<>(),
                new HaversineHeuristic<>()
        ).findPath(startId, destinationId);
    }
}

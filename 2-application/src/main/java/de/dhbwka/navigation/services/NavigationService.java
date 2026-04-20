package de.dhbwka.navigation.services;

import de.dhbwka.navigation.extension.ExtensionEdgeCategory;
import de.dhbwka.navigation.extension.ExtensionEdgeCategoryFilter;
import de.dhbwka.navigation.extension.ExtensionGraph;
import de.dhbwka.navigation.graph.Graph;
import de.dhbwka.navigation.graph.InMemoryGraph;
import de.dhbwka.navigation.graph.edge.CategorizedGeoEdge;
import de.dhbwka.navigation.graph.edge.CategorizedWidthedGeoEdge;
import de.dhbwka.navigation.graph.node.GeoNode;
import de.dhbwka.navigation.parser.Parser;
import de.dhbwka.navigation.pathfinding.AStarPathFinder;
import de.dhbwka.navigation.pathfinding.HaversineEdgeScorer;
import de.dhbwka.navigation.pathfinding.HaversineHeuristic;

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

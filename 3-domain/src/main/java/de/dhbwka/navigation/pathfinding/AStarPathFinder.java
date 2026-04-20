package de.dhbwka.navigation.pathfinding;

import de.dhbwka.navigation.graph.Graph;
import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.node.Node;
import de.dhbwka.navigation.graph.node.PathAwareNode;

import java.util.*;

public class AStarPathFinder<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > implements PathFinder<NodeType, EdgeType> {

    private final Graph<NodeType, EdgeType> graph;
    private final Heuristic<? super NodeType> heuristic;

    private final EdgeScorer<? super NodeType, ? super EdgeType> edgeScorer;

    public AStarPathFinder(
            Graph<NodeType, EdgeType> graph,
            EdgeScorer<? super NodeType, ? super EdgeType> edgeScorer,
            Heuristic<? super NodeType> heuristic
    ) {
        this.graph = Objects.requireNonNull(graph);
        this.edgeScorer = Objects.requireNonNull(edgeScorer);
        this.heuristic = Objects.requireNonNull(heuristic);
    }

    @Override
    public List<EdgeType> findPath(String startId, String destinationId) {
        NodeType start = graph.getNode(startId).orElseThrow(NullPointerException::new);
        NodeType destination = graph.getNode(destinationId).orElseThrow(NullPointerException::new);
        // Warteschlange an Nodes für über die Iteriert werden soll, geordnet nach Priorität.
        Queue<PathAwareNode<NodeType, EdgeType>> openList = new PriorityQueue<>();
        // Speicherung von PathAwareNodes die Kontextinformationen über den Pfad besitzen.
        Map<String, PathAwareNode<NodeType, EdgeType>> closedList = new HashMap<>();

        // Start Node laden.
        PathAwareNode<NodeType, EdgeType> startRecord = new PathAwareNode<>(
                start,
                null,
                0,
                heuristic.estimate(start, destination),
                null
        );
        openList.add(startRecord);
        closedList.put(start.getId(), startRecord);

        // Solange Elemente in der Warteschlange sind.
        while (!openList.isEmpty()) {
            PathAwareNode<NodeType, EdgeType> current = openList.poll();

            // Falls die Node mit der höchsten Priorität in der Warteschlange das Ziel ist.
            if (current.getId().equals(destination.getId())) {
                return reconstructPath(current);
            }

            // Für alle Nodes die über Edges mit der aktuellen Node verbunden sind.
            for (EdgeType edge : graph.getEdgesFrom(current.getCurrent().getId())) {
                NodeType neighbor = edge.getDestination();
                // Node mit Kontextinformationen über den Pfad laden oder neue Node erstellen
                PathAwareNode<NodeType, EdgeType> neighbourRecord = closedList.computeIfAbsent(
                        neighbor.getId(),
                        id -> new PathAwareNode<>(neighbor)
                );
                // Pfadlänge von Start zur Nachbar Node = Pfadlänge von Start zur jetzigen Node + Pfad länge von der jetzigen Node zur Nachbar Node.
                double tentativeScore = current.getRouteScore() + edgeScorer.calculateScore(edge);
                // Falls die berechnete Pfadlänge zum Nachbar kleiner als die bereits gespeicherte.
                if (tentativeScore < neighbourRecord.getRouteScore()) {
                    // Update der Kontextinformationen der Nachbar Node.
                    neighbourRecord.setPredecessor(current);
                    neighbourRecord.setEdge(edge);
                    neighbourRecord.setRouteScore(tentativeScore);
                    // Heuristik: Update der Entfernung von Nachbar Node bis Ziel.
                    // Gilt als Priorität für die Warteschlange
                    neighbourRecord.setEstimatedScore(
                            tentativeScore + heuristic.estimate(neighbor, destination)
                    );
                    // Speicherung der Node mit Kontextinformationen.
                    openList.add(neighbourRecord);
                }
            }
        }

        throw new IllegalStateException("No route found");
    }

    private List<EdgeType> reconstructPath(PathAwareNode<NodeType, EdgeType> destinationRecord) {
        List<EdgeType> path = new ArrayList<>();
        PathAwareNode<NodeType, EdgeType> currentRecord = destinationRecord;
        do {
            EdgeType edge = currentRecord.getEdge();
            if (edge != null){
                path.add(currentRecord.getEdge());
            }
            currentRecord = currentRecord.getPredecessor();
        } while (currentRecord != null);
        Collections.reverse(path);
        return path;
    }
}

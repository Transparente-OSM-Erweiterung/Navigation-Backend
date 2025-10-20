package de.dhbwka.navigation.application;

import de.dhbwka.navigation.abstraction.Graph;
import de.dhbwka.navigation.abstraction.Node;
import de.dhbwka.navigation.domain.PathFinder;

import java.util.*;

public class AStarPathFinder<T extends Node> implements PathFinder<T> {

    private final Graph<T> graph;
    private final Scorer<T> nextNodeScorer, heuristicScorer;

    public AStarPathFinder(Graph<T> graph, Scorer<T> nextNodeScorer, Scorer<T> heuristicScorer) {
        this.graph = graph;
        this.nextNodeScorer = nextNodeScorer;
        this.heuristicScorer = heuristicScorer;
    }

    @Override
    public List<T> findPath(T start, T destination) {
        // Warteschlange an Nodes für über die Iteriert werden soll, geordnet nach Priorität.
        Queue<PathAwareNode<T>> openList = new PriorityQueue<>();
        // Speicherung von PathAwareNodes die Kontextinformationen über den Pfad besitzen.
        Map<String, PathAwareNode<T>> closedList = new HashMap<>();

        // Start Node laden.
        PathAwareNode<T> startRecord = new PathAwareNode<>(start, null, 0, heuristicScorer.computeCost(start, destination));
        openList.add(startRecord);
        closedList.put(start.getId(), startRecord);

        // Solange Elemente in der Warteschlange sind.
        while (!openList.isEmpty()) {
            PathAwareNode<T> current = openList.poll();

            // Falls die Node mit der höchsten Priorität in der Warteschlange das Ziel ist.
            if (current.getId().equals(destination.getId())) {
                return reconstructPath(current);
            }

            // Für alle Nodes die über Edges mit der aktuellen Node verbunden sind.
            for (T neighbor : graph.getNeighbors(current.getCurrent())) {
                // Node mit Kontextinformationen über den Pfad laden oder neue Node erstellen
                PathAwareNode<T> neighbourRecord = closedList.computeIfAbsent(neighbor.getId(), id -> new PathAwareNode<>(neighbor));
                // Pfadlänge von Start zur Nachbar Node = Pfadlänge von Start zur jetzigen Node + Pfad länge von der jetzigen Node zur Nachbar Node.
                double tentativeScore = current.getRouteScore() + nextNodeScorer.computeCost(current.getCurrent(), neighbor);
                // Falls die berechnete Pfadlänge zum Nachbar kleiner als die bereits gespeicherte.
                if (tentativeScore < neighbourRecord.getRouteScore()) {
                    // Update der Kontextinformationen der Nachbar Node.
                    neighbourRecord.setPredecessor(current);
                    neighbourRecord.setRouteScore(tentativeScore);
                    // Heuristik: Update der Entfernung von Nachbar Node bis Ziel.
                    // Gilt als Priorität für die Warteschlange
                    neighbourRecord.setEstimatedScore(tentativeScore + heuristicScorer.computeCost(neighbor, destination));
                    // Speicherung der Node mit Kontextinformationen.
                    openList.add(neighbourRecord);
                }
            }
        }

        throw new IllegalStateException("No route found");
    }

    private List<T> reconstructPath(PathAwareNode<T> destinationRecord) {
        List<T> path = new ArrayList<>();
        PathAwareNode<T> currentRecord = destinationRecord;
        do {
            path.add(currentRecord.getCurrent());
            currentRecord = currentRecord.getPredecessor();
        } while (currentRecord != null);
        Collections.reverse(path);
        return path;
    }
}

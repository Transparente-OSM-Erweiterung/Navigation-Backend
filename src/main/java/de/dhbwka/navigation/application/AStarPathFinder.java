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
        Queue<PathAwareNode<T>> openSet = new PriorityQueue<>();
        Map<String, PathAwareNode<T>> allNodes = new HashMap<>();

        PathAwareNode<T> startRecord = new PathAwareNode<>(start, null, 0, heuristicScorer.computeCost(start, destination));
        openSet.add(startRecord);
        allNodes.put(start.getId(), startRecord);

        while (!openSet.isEmpty()) {
            PathAwareNode<T> current = openSet.poll();

            if (current.getId().equals(destination.getId())) {
                return reconstructPath(current);
            }

            for (T neighbor : graph.getNeighbors(current.getCurrent())) {
                PathAwareNode<T> neighbourRecord = allNodes.computeIfAbsent(neighbor.getId(), id -> new PathAwareNode<>(neighbor));
                double tentativeScore = current.getRouteScore() + nextNodeScorer.computeCost(current.getCurrent(), neighbor);
                if (tentativeScore < neighbourRecord.getRouteScore()) {
                    neighbourRecord.setPredecessor(current);
                    neighbourRecord.setRouteScore(tentativeScore);
                    neighbourRecord.setEstimatedScore(tentativeScore + heuristicScorer.computeCost(neighbor, destination));
                    openSet.add(neighbourRecord);
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

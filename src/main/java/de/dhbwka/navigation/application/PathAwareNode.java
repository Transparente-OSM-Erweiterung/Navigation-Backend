package de.dhbwka.navigation.application;

import de.dhbwka.navigation.abstraction.Node;

public class PathAwareNode<T extends Node> implements Node, Comparable<PathAwareNode<T>> {
    private final T current;
    private PathAwareNode<T> predecessor;
    private double routeScore, estimatedScore;

    public PathAwareNode(T current, PathAwareNode<T> predecessor, double routeScore, double estimatedScore) {
        this.current = current;
        this.predecessor = predecessor;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
    }

    public PathAwareNode(T current) {
        this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public String getId() {
        return current.getId();
    }

    @Override
    public int compareTo(PathAwareNode<T> other) {
        return Double.compare(this.estimatedScore, other.estimatedScore);
    }

    public T getCurrent() {
        return current;
    }

    public PathAwareNode<T> getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(PathAwareNode<T> predecessor) {
        this.predecessor = predecessor;
    }

    public double getRouteScore() {
        return routeScore;
    }

    public void setRouteScore(double routeScore) {
        this.routeScore = routeScore;
    }

    public double getEstimatedScore() {
        return estimatedScore;
    }

    public void setEstimatedScore(double estimatedScore) {
        this.estimatedScore = estimatedScore;
    }
}

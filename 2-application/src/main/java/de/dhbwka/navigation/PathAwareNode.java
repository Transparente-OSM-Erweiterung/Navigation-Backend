package de.dhbwka.navigation;

public class PathAwareNode<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > implements Node,
        Comparable<PathAwareNode<NodeType, EdgeType>> {
    private final NodeType current;
    private PathAwareNode<NodeType, EdgeType> predecessor;
    private double routeScore, estimatedScore;

    private EdgeType edge;

    public PathAwareNode(
            NodeType current,
            PathAwareNode<NodeType, EdgeType> predecessor,
            double routeScore,
            double estimatedScore,
            EdgeType edge
    ) {
        this.current = current;
        this.predecessor = predecessor;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
        this.edge = edge;
    }

    public PathAwareNode(NodeType current) {
        this(
                current,
                null,
                Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                null
        );
    }

    @Override
    public String getId() {
        return current.getId();
    }

    @Override
    public int compareTo(PathAwareNode<NodeType, EdgeType> other) {
        return Double.compare(this.estimatedScore, other.estimatedScore);
    }

    public NodeType getCurrent() {
        return current;
    }

    public PathAwareNode<NodeType, EdgeType> getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(PathAwareNode<NodeType, EdgeType> predecessor) {
        this.predecessor = predecessor;
    }

    public void setEdge(EdgeType edge) {
        this.edge = edge;
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

    public EdgeType getEdge() {
        return edge;
    }
}

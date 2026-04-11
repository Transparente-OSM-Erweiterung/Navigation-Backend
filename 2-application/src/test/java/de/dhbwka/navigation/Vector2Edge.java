package de.dhbwka.navigation;

public class Vector2Edge implements Edge<Vector2Node>{

    private final Vector2Node origin;

    private final Vector2Node destination;

    private final boolean directed;

    public Vector2Edge(Vector2Node origin, Vector2Node destination, boolean directed) {
        this.origin = origin;
        this.destination = destination;
        this.directed = directed;
    }

    @Override
    public Vector2Node getOrigin() {
        return origin;
    }

    @Override
    public Vector2Node getDestination() {
        return destination;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }
}

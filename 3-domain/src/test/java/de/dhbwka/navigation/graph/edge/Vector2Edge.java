package de.dhbwka.navigation.graph.edge;

import de.dhbwka.navigation.graph.node.Vector2Node;

public class Vector2Edge implements Edge<Vector2Node> {

    private final Vector2Node origin;

    private final Vector2Node destination;

    public Vector2Edge(Vector2Node origin, Vector2Node destination) {
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public Vector2Node getOrigin() {
        return origin;
    }

    @Override
    public Vector2Node getDestination() {
        return destination;
    }
}

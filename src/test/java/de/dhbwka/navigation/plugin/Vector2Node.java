package de.dhbwka.navigation.plugin;

import de.dhbwka.navigation.domain.VectorNode;

public class Vector2Node implements VectorNode {

    private final String id;
    private final double[] components;

    public Vector2Node(String id, double[] components) {
        this.id = id;
        this.components = components.clone();
    }

    public Vector2Node(String id, double x, double y) {
        this.id = id;
        this.components = new double[]{x, y};
    }

    @Override
    public double[] getComponents() {
        return components.clone();
    }

    @Override
    public String getId() {
        return id;
    }

    public double getX() {
        return components[0];
    }

    public double getY() {
        return components[1];
    }
}

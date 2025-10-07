package de.dhbwka.navigation;

public record OSMNode(String id, String name, double latitude, double longitude) implements GraphNode {
}

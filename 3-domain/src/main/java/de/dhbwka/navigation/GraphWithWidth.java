package de.dhbwka.navigation;

public interface GraphWithWidth<T extends Node> extends Graph<T> {
    double getWidth(String fromId, String toId);
}

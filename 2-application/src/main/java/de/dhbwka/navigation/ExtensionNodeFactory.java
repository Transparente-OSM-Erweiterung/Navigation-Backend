package de.dhbwka.navigation;

@FunctionalInterface
public interface ExtensionNodeFactory<T extends Node> {
    T createNode(String id);
}

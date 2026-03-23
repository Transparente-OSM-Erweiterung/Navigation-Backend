package de.dhbwka.navigation;

@FunctionalInterface
public interface Scorer<T extends Node> {
    double computeCost(T from, T to);
}

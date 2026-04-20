package de.dhbwka.navigation;

@FunctionalInterface
public interface Heuristic<T extends Node> {
    double estimate(T from, T to);
}

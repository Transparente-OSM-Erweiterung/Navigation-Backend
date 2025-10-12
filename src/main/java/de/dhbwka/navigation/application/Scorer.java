package de.dhbwka.navigation.application;


import de.dhbwka.navigation.abstraction.Node;

@FunctionalInterface
public interface Scorer<T extends Node> {
    double computeCost(T from, T to);
}

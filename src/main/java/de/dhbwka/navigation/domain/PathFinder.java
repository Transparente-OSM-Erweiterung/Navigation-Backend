package de.dhbwka.navigation.domain;

import de.dhbwka.navigation.abstraction.Node;

import java.util.List;

public interface PathFinder<T extends Node> {
    List<T> findPath(T start, T destination);
}

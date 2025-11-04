package de.dhbwka.navigation;

import java.util.List;

public interface PathFinder<T extends Node> {
    List<T> findPath(T start, T destination);
}

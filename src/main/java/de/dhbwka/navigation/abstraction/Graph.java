package de.dhbwka.navigation.abstraction;

import java.util.Collection;
import java.util.Optional;

public interface Graph<T extends Node> {
    Optional<T> getNode(String id);

    Collection<T> getNeighbors(T node);
}

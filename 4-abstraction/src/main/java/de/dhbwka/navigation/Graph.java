package de.dhbwka.navigation;

import java.util.Collection;
import java.util.Optional;

public interface Graph<T extends Node> {
    Optional<T> getNode(String id);

    Collection<? extends T> getNeighbors(T node);
}

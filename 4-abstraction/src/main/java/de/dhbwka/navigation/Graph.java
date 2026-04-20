package de.dhbwka.navigation;

import java.util.Collection;
import java.util.Optional;

public interface Graph<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > {
    Optional<NodeType> getNode(String id);

    Collection<EdgeType> getEdgesFrom(String nodeId);
}

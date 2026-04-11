package de.dhbwka.navigation;

import java.util.Collection;
import java.util.Optional;

public interface Graph<NodeType extends Node, EdgeType extends Edge<NodeType>> {
    Optional<NodeType> getNode(String id);

    @Deprecated
    Collection<NodeType> getNeighbors(NodeType node);

    /**
     * @return an Edge that is guaranteed to have the node as its origin parameter.
     */
    Collection<EdgeType> getEdgesFrom(NodeType node);
}

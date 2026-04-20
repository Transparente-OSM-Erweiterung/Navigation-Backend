package de.dhbwka.navigation.extension;

import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.node.Node;

import java.util.List;

public record Compound<
        NodeType extends Node,
        BaseEdgeType extends Edge<? extends NodeType>,
        ExtensionEdgeType extends Edge<? extends NodeType>
    > (
        List<BaseEdgeType> baseEdges,
        List<ExtensionEdgeType> extensionEdges
    ){
}

package de.dhbwka.navigation.graph;

import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.node.Node;

import java.util.function.BiFunction;

public class TestGraphBuilder<
        NodeType extends Node,
        EdgeType extends Edge<? extends NodeType>
    > {
    private final TestGraph<NodeType, EdgeType> graph = new TestGraph<>();

    public static <NodeType extends Node, EdgeType extends Edge<? extends NodeType>> TestGraphBuilder<NodeType, EdgeType> create() {
        return new TestGraphBuilder<>();
    }

    public TestGraphBuilder<NodeType, EdgeType> node(NodeType node) {
        graph.addNode(node);
        return this;
    }

    @SafeVarargs
    public final TestGraphBuilder<NodeType, EdgeType> nodes(NodeType... nodes) {
        for (NodeType node : nodes) {
            graph.addNode(node);
        }
        return this;
    }

    public TestGraphBuilder<NodeType, EdgeType> connect(
            String fromId,
            String toId,
            BiFunction<NodeType, NodeType, EdgeType> edgeFactory
    ) {
        NodeType origin = graph.getNode(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + fromId));
        NodeType dest = graph.getNode(toId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + toId));

        graph.addEdge(edgeFactory.apply(origin, dest));
        graph.addEdge(edgeFactory.apply(dest, origin));
        return this;
    }

    public TestGraph<NodeType, EdgeType> build() {
        return graph;
    }
}

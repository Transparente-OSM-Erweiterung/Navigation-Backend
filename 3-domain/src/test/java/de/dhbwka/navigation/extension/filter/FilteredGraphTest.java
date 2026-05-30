package de.dhbwka.navigation.extension.filter;

import de.dhbwka.navigation.graph.TestGraph;
import de.dhbwka.navigation.graph.TestGraphBuilder;
import de.dhbwka.navigation.graph.edge.Vector2Edge;
import de.dhbwka.navigation.graph.node.Vector2Node;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FilteredGraphTest {

    @Test
    void getEdgesFromShouldFilterByPredicate() {
        // Arrange
        TestGraph<Vector2Node, Vector2Edge> graph = TestGraphBuilder.<Vector2Node, Vector2Edge>create()
                .nodes(
                        new Vector2Node("A", 0, 0),
                        new Vector2Node("B", 1, 0),
                        new Vector2Node("C", 0, 1)
                )
                .connect("A", "B", Vector2Edge::new)
                .connect("A", "C", Vector2Edge::new)
                .build();

        FilteredGraph<Vector2Node, Vector2Edge> filteredGraph = new FilteredGraph<>(
                graph,
                edge -> edge.getDestination().getId().equals("B")
        );

        // Act + Assert
        assertThat(filteredGraph.getEdgesFrom("A"))
                .extracting(edge -> edge.getDestination().getId())
                .containsExactly("B");
    }
}

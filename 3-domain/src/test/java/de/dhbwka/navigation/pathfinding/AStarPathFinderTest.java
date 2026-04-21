package de.dhbwka.navigation.pathfinding;

import de.dhbwka.navigation.graph.TestGraph;
import de.dhbwka.navigation.graph.TestGraphBuilder;
import de.dhbwka.navigation.graph.edge.Vector2Edge;
import de.dhbwka.navigation.graph.node.Vector2Node;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AStarPathFinderTest {

    @Test
    void findPathShouldReturnCorrectSequenceOfNodes() {
        // Arrange
        TestGraph<Vector2Node, Vector2Edge> graph = TestGraphBuilder.<Vector2Node, Vector2Edge>create()
                .nodes(
                        new Vector2Node("A", 0, 0),
                        new Vector2Node("B", 1, 0),
                        new Vector2Node("C", 0, 2),
                        new Vector2Node("D", 1, 1)
                )
                .connect("A", "B", Vector2Edge::new)
                .connect("A", "C", Vector2Edge::new)
                .connect("B", "D", Vector2Edge::new)
                .connect("C", "D", Vector2Edge::new)
                .build();

        AStarPathFinder<Vector2Node, Vector2Edge> pathFinder = new AStarPathFinder<>(
                graph,
                new EuclideanEdgeScorer<>(),
                new EuclideanHeuristic<>()
        );

        // Act
        List<Vector2Edge> path = pathFinder.findPath("A", "D");

        // Assert
        assertThat(path)
                .as("The Path from A to D should lead over B")
                .isNotEmpty()
                .extracting(edge -> edge.getDestination().getId())
                .containsExactly("B", "D");
    }
}

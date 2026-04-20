package de.dhbwka.navigation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AStarPathFinderTest {

    private static final InMemoryGraph<Vector2Node, Edge<Vector2Node>> graph = new InMemoryGraph<>();
    private static final PathFinder<Vector2Node, Edge<Vector2Node>> pathFinder =
            new AStarPathFinder<>(graph, new EuclideanEdgeScorer<>(), new EuclideanHeuristic<>());

    @BeforeAll
    static void setUp() {
        List.of(
                new Vector2Node("A", 0, 0),
                new Vector2Node("B", 1, 0),
                new Vector2Node("C", 0, 2),
                new Vector2Node("D", 1, 1)
        ).forEach(graph::addNode);

        Map.of(
                "A", "B",
                "C", "A",
                "B", "D",
                "D", "C"
        ).forEach((from, to) -> {
            Vector2Node n1 = graph.getNode(from).orElseThrow();
            Vector2Node n2 = graph.getNode(to).orElseThrow();
            graph.addEdge(new Vector2Edge(n1, n2));
        });
    }

    @Test
    void findPathShouldReturnCorrectSequenceOfNodes() {
        // Arrange
        String startId = "A";
        String  targetId = "D";

        // Act
        List<Edge<Vector2Node>> path = pathFinder.findPath(startId, targetId);

        // Assert
        assertThat(path)
                .as("The Path from A to D should lead over B")
                .isNotEmpty()
                .extracting(edge -> edge.getDestination().getId())
                .containsExactly("B", "D");
    }
}

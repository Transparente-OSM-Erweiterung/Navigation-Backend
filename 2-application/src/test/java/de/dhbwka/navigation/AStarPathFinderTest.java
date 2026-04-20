package de.dhbwka.navigation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AStarPathFinderTest {
    private static final InMemoryGraph<Vector2Node, Edge<Vector2Node>> graph = new InMemoryGraph<>();
    private static final PathFinder<Vector2Node, Edge<Vector2Node>> pathFinder = new AStarPathFinder<>(graph, new EuclideanEdgeScorer<>(), new EuclideanHeuristic<>());

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
        ).forEach((k, v) -> {
            graph.addEdge(new Vector2Edge(graph.getNode(k).orElseThrow(), graph.getNode(v).orElseThrow(), false));
            Vector2Node n1 = graph.getNode(k).orElseThrow();
            Vector2Node n2 = graph.getNode(v).orElseThrow();
            Edge<Vector2Node> edge = new Vector2Edge(n1, n2, false);
            graph.addEdge(edge);
        });
    }

    @Test
    void findPath() {
        List<Edge<Vector2Node>> path = pathFinder.findPath(graph.getNode("A").orElseThrow(), graph.getNode("D").orElseThrow());
        String[] pathString = path.stream().map(e->e.getDestination().getId()).toArray(String[]::new);
        assertArrayEquals(new String[]{"B", "D"}, pathString);
    }
}

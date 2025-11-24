package de.dhbwka.navigation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class AStarPathFinderTest {
    private static final InMemoryGraph<Vector2Node> graph = new InMemoryGraph<>();
    private static final PathFinder<Vector2Node> pathFinder = new AStarPathFinder<>(graph, new EuclideanScorer<>(), new EuclideanScorer<>());

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
        ).forEach(graph::addEdge);
    }

    @Test
    void findPath() {
        List<? extends Vector2Node> path = pathFinder.findPath(graph.getNode("A").orElseThrow(), graph.getNode("D").orElseThrow());
        String[] pathString = path.stream().map(Vector2Node::getId).toArray(String[]::new);
        assertArrayEquals(new String[]{"A", "B", "D"}, pathString);
    }
}
package de.dhbwka.navigation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RouteFinderTest {

    RouteFinder<OSMNode> routeFinder;
    Graph<OSMNode> graph;
    OSMNode startNode;
    OSMNode endNode;

    @BeforeEach
    void setUp() {
        startNode = new OSMNode("A", "A", 10, 10);
        endNode = new OSMNode("D", "D", 12, 12);
        graph = new Graph<>(
                Set.of(
                        startNode,
                        new OSMNode("B", "B", 12, 10),
                        new OSMNode("C", "C", 9, 12),
                        endNode
                ),
                Map.of(
                        "A", Set.of("B", "C"),
                        "B", Set.of("A", "D"),
                        "C", Set.of("A", "D"),
                        "D", Set.of("B", "C")
                )
        );
        routeFinder = new RouteFinder<>(graph, new HaversineScorer(), new HaversineScorer());
    }

    @Test
    void findRoute() {
        List<OSMNode> route = routeFinder.findRoute(startNode, endNode);
        String[] path = route.stream().map(OSMNode::name).toArray(String[]::new);
        assertArrayEquals(new String[]{"A", "B", "D"}, path);
    }
}
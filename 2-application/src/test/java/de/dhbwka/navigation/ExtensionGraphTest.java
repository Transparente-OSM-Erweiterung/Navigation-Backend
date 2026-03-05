package de.dhbwka.navigation;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExtensionGraphTest {

    @Test
    void getNode() {
    }

    @Test
    void getNeighbors() {
        GeoNode origin = new GeoTestNode("0", 0, 0);
        List<GeoNode> neighbors = List.of(
                new GeoTestNode("1", 0, 1),
                new GeoTestNode("2", 1, 0),
                new GeoTestNode("3", 0, -1),
                new GeoTestNode("4", -1, 0)
        );
        InMemoryGraph<GeoNode> graph = new InMemoryGraph<>();
        graph.addNode(origin);
        neighbors.forEach(graph::addNode);
        neighbors.forEach(n -> graph.addEdge(origin.getId(), n.getId()));
        GeoTestNode.Factory factory = new GeoTestNode.Factory();
        ExtensionGraph extGraph = new ExtensionGraph(graph);
        Collection<GeoNode> extNeighbours = extGraph.getNeighbors(origin);
        assertEquals(8, extNeighbours.size());
        assertTrue(extNeighbours.stream().map(GeoNode::getId).anyMatch(id -> id.equals("0a")));
        assertTrue(extNeighbours.stream().map(GeoNode::getId).anyMatch(id -> id.equals("0b")));
        assertTrue(extNeighbours.stream().map(GeoNode::getId).anyMatch(id -> id.equals("0c")));
        assertTrue(extNeighbours.stream().map(GeoNode::getId).anyMatch(id -> id.equals("0d")));
    }

    @Test
    void sortDegree() {
        GeoNode origin = new GeoTestNode("0", 0, 0);
        List<GeoNode> nodes = List.of(
                new GeoTestNode("3", -1, 0),
                new GeoTestNode("0", 0, 1),
                new GeoTestNode("1", 1, 0),
                new GeoTestNode("2", 0, -1)
        );
        List<GeoNode> sorted = ExtensionGraph.sortDegree(nodes, origin);

        System.out.println(sorted);

        for (int i = 0; i < 3; i++) {
            assertEquals(sorted.get(i).getId(), String.valueOf(i));
        }
    }


    @Test
    void calcDeg() {
        GeoNode origin = new GeoTestNode("0", 0, 0);
        assertEquals(0, ExtensionGraph.calcDeg(origin, new GeoTestNode("1", 0, 1)));
        assertEquals(45, ExtensionGraph.calcDeg(origin, new GeoTestNode("2", 1, 1)));
        assertEquals(90, ExtensionGraph.calcDeg(origin, new GeoTestNode("3", 1, 0)));
        assertEquals(135, ExtensionGraph.calcDeg(origin, new GeoTestNode("4", 1, -1)));
        assertEquals(180, ExtensionGraph.calcDeg(origin, new GeoTestNode("5", 0, -1)));
        assertEquals(225, ExtensionGraph.calcDeg(origin, new GeoTestNode("6", -1, -1)));
        assertEquals(270, ExtensionGraph.calcDeg(origin, new GeoTestNode("7", -1, 0)));
        assertEquals(315, ExtensionGraph.calcDeg(origin, new GeoTestNode("8", -1, 1)));
    }

    @Test
    void calcDegNotNormalized() {
        GeoNode origin = new GeoTestNode("0", 0, 0);
        assertEquals(0, ExtensionGraph.calcDeg(origin, new GeoTestNode("1", 0, 2)));
        assertEquals(45, ExtensionGraph.calcDeg(origin, new GeoTestNode("2", 2, 2)));
        assertEquals(90, ExtensionGraph.calcDeg(origin, new GeoTestNode("3", 2, 0)));
        assertEquals(135, ExtensionGraph.calcDeg(origin, new GeoTestNode("4", 2, -2)));
        assertEquals(180, ExtensionGraph.calcDeg(origin, new GeoTestNode("5", 0, -2)));
        assertEquals(225, ExtensionGraph.calcDeg(origin, new GeoTestNode("6", -2, -2)));
        assertEquals(270, ExtensionGraph.calcDeg(origin, new GeoTestNode("7", -2, 0)));
        assertEquals(315, ExtensionGraph.calcDeg(origin, new GeoTestNode("8", -2, 2)));
    }

    @Test
    void getNeighborsOfExtNode() {
        GeoNode origin = new GeoTestNode("0a", 1, 1);
        GeoNode main = new GeoTestNode("0", 0, 0);
        List<GeoNode> neighboursOfMain = List.of(
                new GeoTestNode("1", 0, 1),
                new GeoTestNode("2", 1, 0),
                new GeoTestNode("3", 0, -1),
                new GeoTestNode("4", -1, 0)
        );
        InMemoryGraph<GeoNode> graph = new InMemoryGraph<>();
        graph.addNode(main);
        neighboursOfMain.forEach(graph::addNode);
        neighboursOfMain.forEach(n -> graph.addEdge(main.getId(), n.getId()));
        GeoTestNode.Factory factory = new GeoTestNode.Factory();
        ExtensionGraph extGraph = new ExtensionGraph(graph);
        Collection<GeoNode> extNeighbours = extGraph.getNeighbors(origin);

        assertEquals(5, extNeighbours.size());
        extNeighbours.forEach(System.out::println);
        assertTrue(extNeighbours.stream().map(GeoNode::getId).anyMatch(id -> id.equals("0")));
        assertTrue(extNeighbours.stream().map(GeoNode::getId).anyMatch(id -> id.equals("0b")));
        assertTrue(extNeighbours.stream().map(GeoNode::getId).anyMatch(id -> id.equals("0d")));
        assertTrue(extNeighbours.stream().map(GeoNode::getId).anyMatch(id -> id.equals("1a")));
        assertTrue(extNeighbours.stream().map(GeoNode::getId).anyMatch(id -> id.equals("2a")));
    }
}
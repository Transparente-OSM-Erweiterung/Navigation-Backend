package de.dhbwka.navigation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

class ExtensionGraphTest {

    @Test
    void getEdgesOfBaseNodeShouldAssignCorrectIds() {
        GeoTestNode main = new GeoTestNode("0", 0, 0);
        List<GeoTestNode> neighboursOfMain = List.of(
                new GeoTestNode("1", 0, 1),
                new GeoTestNode("2", 1, 0),
                new GeoTestNode("3", 0, -1),
                new GeoTestNode("4", -1, 0)
        );

        InMemoryGraph<GeoTestNode, GeoTestEdge> graph = new InMemoryGraph<>();
        graph.addNode(main);
        neighboursOfMain.forEach(node -> {
            graph.addNode(node);
            graph.addEdge(new GeoTestEdge(main, node));
            graph.addEdge(new GeoTestEdge(node, main));
        });

        ExtensionGraph<GeoTestNode, GeoTestEdge> extGraph = new ExtensionGraph<>(graph);

        Collection<CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> extEdges = extGraph.getEdgesFrom(main.getId());

        assertThat(extEdges)
                .hasSize(8)
                .extracting(e -> e.getDestination().getId())
                .as("generated Node Ids should be of the expected pattern")
                .containsExactlyInAnyOrder("1", "2", "3", "4", "0a", "0b", "0c", "0d");
    }

    @Test
    void getEdgesOfExtensionNodeShouldAssignCorrectIds() {
        GeoTestNode origin = new GeoTestNode("0a", 1, 1);
        GeoTestNode main = new GeoTestNode("0", 0, 0);
        List<GeoTestNode> neighboursOfMain = List.of(
                new GeoTestNode("1", 0, 1),
                new GeoTestNode("2", 1, 0),
                new GeoTestNode("3", 0, -1),
                new GeoTestNode("4", -1, 0)
        );

        InMemoryGraph<GeoTestNode, GeoTestEdge> graph = new InMemoryGraph<>();
        graph.addNode(main);
        neighboursOfMain.forEach(node -> {
            graph.addNode(node);
            graph.addEdge(new GeoTestEdge(main, node));
            graph.addEdge(new GeoTestEdge(node, main));
        });

        ExtensionGraph<GeoTestNode, GeoTestEdge> extGraph = new ExtensionGraph<>(graph);

        Collection<CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> extEdges = extGraph.getEdgesFrom(origin.getId());

        assertThat(extEdges)
                .hasSize(5)
                .extracting(e -> e.getDestination().getId())
                .as("generated Node Ids should be of the expected pattern")
                .containsExactlyInAnyOrder("0", "0b", "0d", "1a", "2a");
    }

    @Test
    void getNeighborsPositioningRealOsmNode() {
        GeoTestNode main = new GeoTestNode("1599059100", 49.0040323, 8.4000678);
        List<GeoTestNode> neighboursOfMain = List.of(
                new GeoTestNode("1794126907", 49.0040313, 8.4000282),
                new GeoTestNode("21533398", 49.0040308, 8.4002886),
                new GeoTestNode("1600203549", 49.0040778, 8.4000694),
                new GeoTestNode("12599995589", 49.0039223, 8.4000615)
        );

        InMemoryGraph<GeoTestNode, GeoTestEdge> graph = new InMemoryGraph<>();
        graph.addNode(main);
        neighboursOfMain.forEach(node -> {
            graph.addNode(node);
            graph.addEdge(new GeoTestEdge(main, node));
            graph.addEdge(new GeoTestEdge(node, main));
        });

        ExtensionGraph<GeoTestNode, GeoTestEdge> extGraph = new ExtensionGraph<>(graph);

        Collection<CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> extEdges = extGraph.getEdgesFrom(main.getId());

        assertThat(extEdges)
                .hasSize(8)
                .extracting(e -> e.getDestination().getId())
                .as("generated Node Ids should be of the expected pattern")
                .containsExactlyInAnyOrder("1794126907", "21533398", "1600203549", "12599995589", "1599059100a", "1599059100b", "1599059100c", "1599059100d");
    }

    @ParameterizedTest(name = "Angle of Node {0} ({1}|{2}) should be {3} degrees")
    @CsvSource({
        "1,  0,  1,   0",
        "2,  1,  1,  45",
        "3,  1,  0,  90",
        "4,  1, -1, 135",
        "5,  0, -1, 180",
        "6, -1, -1, 225",
        "7, -1,  0, 270",
        "8, -1,  1, 315"
    })
    void calcDegShouldReturnCorrectDegrees(String id, double x, double y, double expectedDeg) {
        GeoNode origin = new GeoTestNode("0", 0, 0);
        GeoNode target = new GeoTestNode(id, x, y);

        double actualDeg = ExtensionGraph.calcDeg(origin, target);

        assertThat(actualDeg)
            .as("Angle of Destination Node %s", id)
            .isEqualTo(expectedDeg);
    }

    @ParameterizedTest(name = "Angle of Node {0} ({1}|{2}) should be {3} degrees")
    @CsvSource({
        "1,  0,  2,   0",
        "2,  2,  2,  45",
        "3,  2,  0,  90",
        "4,  2, -2, 135",
        "5,  0, -2, 180",
        "6, -2, -2, 225",
        "7, -2,  0, 270",
        "8, -2,  2, 315"
    })
    void calcDegShouldHandleNonNormalizedDistances(String id, double x, double y, double expectedDeg) {
        GeoNode origin = new GeoTestNode("0", 0, 0);
        GeoNode target = new GeoTestNode(id, x, y);

        double actualDeg = ExtensionGraph.calcDeg(origin, target);

        assertThat(actualDeg)
            .as("Angle of Node %s at (%s, %s)", id, x, y)
            .isEqualTo(expectedDeg);
    }
}
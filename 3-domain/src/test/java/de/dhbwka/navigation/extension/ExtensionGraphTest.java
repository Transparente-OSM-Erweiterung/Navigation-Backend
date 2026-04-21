package de.dhbwka.navigation.extension;

import de.dhbwka.navigation.graph.TestGraph;
import de.dhbwka.navigation.graph.TestGraphBuilder;
import de.dhbwka.navigation.graph.edge.GeoTestEdge;
import de.dhbwka.navigation.graph.node.GeoTestNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ExtensionGraphTest {

    @Test
    void getEdgesOfBaseNodeShouldAssignCorrectIds() {
        // Arrange
        TestGraph<GeoTestNode, GeoTestEdge> graph = TestGraphBuilder.<GeoTestNode, GeoTestEdge>create()
                .node(new GeoTestNode("0", 0, 0))
                .nodes(
                        new GeoTestNode("1", 0, 1),
                        new GeoTestNode("2", 1, 0),
                        new GeoTestNode("3", 0, -1),
                        new GeoTestNode("4", -1, 0)
                )
                .connect("0", "1", GeoTestEdge::new)
                .connect("0", "2", GeoTestEdge::new)
                .connect("0", "3", GeoTestEdge::new)
                .connect("0", "4", GeoTestEdge::new)
                .build();

        // Act
        ExtensionGraph<GeoTestNode, GeoTestEdge> extGraph = new ExtensionGraph<>(graph);

        // Assert
        assertThat(extGraph.getEdgesFrom("0"))
                .extracting(e -> e.getDestination().getId())
                .containsExactlyInAnyOrder("1", "2", "3", "4", "0a", "0b", "0c", "0d");
    }

    @Test
    void getEdgesOfExtensionNodeShouldAssignCorrectIds() {
        // Arrange
        TestGraph<GeoTestNode, GeoTestEdge> graph = TestGraphBuilder.<GeoTestNode, GeoTestEdge>create()
                .node(new GeoTestNode("0", 0, 0))
                .nodes(
                        new GeoTestNode("1", 0, 1),
                        new GeoTestNode("2", 1, 0),
                        new GeoTestNode("3", 0, -1),
                        new GeoTestNode("4", -1, 0)
                )
                .connect("0", "1", GeoTestEdge::new)
                .connect("0", "2", GeoTestEdge::new)
                .connect("0", "3", GeoTestEdge::new)
                .connect("0", "4", GeoTestEdge::new)
                .build();

        // Act
        ExtensionGraph<GeoTestNode, GeoTestEdge> extGraph = new ExtensionGraph<>(graph);

        // Assert
        assertThat(extGraph.getEdgesFrom("0a"))
                .extracting(e -> e.getDestination().getId())
                .containsExactlyInAnyOrder("0", "0b", "0d", "1a", "2a");
    }

    @Test
    void getNeighborsPositioningRealOsmNode() {
        // Arrange
        TestGraph<GeoTestNode, GeoTestEdge> graph = TestGraphBuilder.<GeoTestNode, GeoTestEdge>create()
                .node(new GeoTestNode("1599059100", 49.0040323, 8.4000678))
                .nodes(
                        new GeoTestNode("1794126907", 49.0040313, 8.4000282),
                        new GeoTestNode("21533398", 49.0040308, 8.4002886),
                        new GeoTestNode("1600203549", 49.0040778, 8.4000694),
                        new GeoTestNode("12599995589", 49.0039223, 8.4000615)
                )
                .connect("1599059100", "1794126907", GeoTestEdge::new)
                .connect("1599059100", "21533398", GeoTestEdge::new)
                .connect("1599059100", "1600203549", GeoTestEdge::new)
                .connect("1599059100", "12599995589", GeoTestEdge::new)
                .build();

        // Act
        ExtensionGraph<GeoTestNode, GeoTestEdge> extGraph = new ExtensionGraph<>(graph);

        // Assert
        assertThat(extGraph.getEdgesFrom("1599059100"))
                .extracting(e -> e.getDestination().getId())
                .containsExactlyInAnyOrder(
                        "1794126907",
                        "21533398",
                        "1600203549",
                        "12599995589",
                        "1599059100a",
                        "1599059100b",
                        "1599059100c",
                        "1599059100d"
                );
    }

    @ParameterizedTest(name = "Angle of Node {0} ({1}|{2}) should be {3}°")
    @CsvSource({
            // normalized distances
            "1,  0,  1,   0",
            "2,  1,  1,  45",
            "3,  1,  0,  90",
            "4,  1, -1, 135",
            "5,  0, -1, 180",
            "6, -1, -1, 225",
            "7, -1,  0, 270",
            "8, -1,  1, 315",
            // non normalized distances
            "1s, 0,  2,   0",
            "2s, 2,  2,  45",
            "3s, 2,  0,  90",
            "4s, 2, -2, 135",
            "5s, 0, -2, 180",
            "6s, -2, -2, 225",
            "7s, -2,  0, 270",
            "8s, -2,  2, 315"
    })
    void calcDegShouldReturnCorrectDegrees(String id, double x, double y, double expectedDeg) {
        GeoTestNode origin = new GeoTestNode("0", 0, 0);
        GeoTestNode target = new GeoTestNode(id, x, y);

        assertThat(ExtensionGraph.calcDeg(origin, target))
            .as("Calculated angle for node %s", id)
            .isEqualTo(expectedDeg);
    }
}
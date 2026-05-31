package de.dhbwka.navigation;

import de.dhbwka.navigation.extension.filter.ExtensionEdgeCategory;
import de.dhbwka.navigation.graph.InMemoryGraph;
import de.dhbwka.navigation.graph.edge.CategorizedWidthedSidewalkClassifiedGeoEdge;
import de.dhbwka.navigation.graph.node.GeoNode;
import de.dhbwka.navigation.parser.OsmXmlParser;
import de.dhbwka.navigation.server.NavigationServer;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        InMemoryGraph<
                GeoNode,
                CategorizedWidthedSidewalkClassifiedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>
            > graph = new InMemoryGraph<>();
        new OsmXmlParser(new FileInputStream("./run/map.osm")).parse(graph);
        new NavigationServer(graph).start();
    }
}

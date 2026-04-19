package de.dhbwka.navigation;

import de.dhbwka.navigation.osm.OsmEdge;
import de.dhbwka.navigation.osm.OsmXmlParser;
import de.dhbwka.navigation.server.NavigationServer;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Main {
    public static void main(String[] args) throws IOException, XMLStreamException {
        InMemoryGraph<GeoNode, OsmEdge<GeoNode>> graph = new InMemoryGraph<>();
        OsmXmlParser parser = new OsmXmlParser(graph);
        parser.parse(new FileInputStream("./run/map.osm"));
        AStarPathFinder<GeoNode, CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> pathFinder = new AStarPathFinder<>(new ExtensionGraph<>(graph), new HarversineEdgeScorer<>(), new HaversineHeuristic<>());
        List<CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> path = pathFinder.findPath(graph.getNode("21533398").orElseThrow(), graph.getNode("15105688").orElseThrow());
        System.out.println(path.stream().map(e -> e.getOrigin().getId()).toList());
        System.out.println(path.stream().map(e -> String.format(Locale.US, "[%.5f,%.5f]", e.getOrigin().getLongitude(), e.getOrigin().getLatitude())).toList());
        NavigationServer server = new NavigationServer();
        server.start();
    }
}
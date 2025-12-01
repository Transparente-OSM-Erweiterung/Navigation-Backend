package de.dhbwka.navigation;

import de.dhbwka.navigation.osm.OSMNodeFactory;
import de.dhbwka.navigation.osm.OsmNode;
import de.dhbwka.navigation.osm.OsmXmlParser;
import de.dhbwka.navigation.server.NavigationServer;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Main {
    public static void main(String[] args) throws IOException, XMLStreamException {
        InMemoryGraph<OsmNode> graph = new InMemoryGraph<>();
        OsmXmlParser parser = new OsmXmlParser(graph);
        parser.parse(new FileInputStream("./run/map.osm"));
        PathFinder<OsmNode> pathFinder = new AStarPathFinder<>(new ExtensionGraph<>(graph, new OSMNodeFactory()), new HaversineScorer<>(), new HaversineScorer<>());
        List<OsmNode> path = pathFinder.findPath(graph.getNode("21533398").orElseThrow(), graph.getNode("15105688").orElseThrow());
        System.out.println(path.stream().map(OsmNode::getId).toList());
        System.out.println(path.stream().map(node -> String.format(Locale.US, "[%.5f,%.5f]", node.getLongitude(), node.getLatitude())).toList());
        NavigationServer server = new NavigationServer();
        server.start();
    }
}
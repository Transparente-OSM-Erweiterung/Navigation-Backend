package de.dhbwka.navigation;

import de.dhbwka.navigation.adapter.InMemoryGraph;
import de.dhbwka.navigation.application.AStarPathFinder;
import de.dhbwka.navigation.application.HaversineScorer;
import de.dhbwka.navigation.domain.PathFinder;
import de.dhbwka.navigation.plugin.OsmNode;
import de.dhbwka.navigation.plugin.OsmXmlParser;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


public class Main {
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
        InMemoryGraph<OsmNode> graph = new InMemoryGraph<>();
        OsmXmlParser parser = new OsmXmlParser(graph);
        parser.parse(new FileInputStream("./run/map.osm"));
        PathFinder<OsmNode> pathFinder = new AStarPathFinder<>(graph, new HaversineScorer<>(), new HaversineScorer<>());
        List<OsmNode> path = pathFinder.findPath(graph.getNode("16718571").orElseThrow(), graph.getNode("15254833").orElseThrow());
        System.out.println(path.stream().map(OsmNode::getId).toList());
    }
}
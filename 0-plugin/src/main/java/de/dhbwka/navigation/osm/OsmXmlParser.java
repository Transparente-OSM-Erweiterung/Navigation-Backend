package de.dhbwka.navigation.osm;

import de.dhbwka.navigation.GeoNode;
import de.dhbwka.navigation.GraphBuilder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OsmXmlParser {
    private final GraphBuilder<GeoNode> graphBuilder;

    public OsmXmlParser(GraphBuilder<GeoNode> graphBuilder) {
        this.graphBuilder = graphBuilder;
    }

    public void parse(InputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

        Map<String, OsmNode> nodes = new HashMap<>();

        List<String> currentWayNodes = new ArrayList<>();
        boolean currentWayOneWay = false;

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                String name = reader.getLocalName();

                switch (name) {
                    case "node" -> {
                        String id = reader.getAttributeValue(null, "id");
                        double lat = Double.parseDouble(reader.getAttributeValue(null, "lat"));
                        double lon = Double.parseDouble(reader.getAttributeValue(null, "lon"));
                        OsmNode node = new OsmNode(id, lat, lon);
                        nodes.put(id, node);
                        graphBuilder.addNode(node);
                    }
                    case "way" -> {
                        currentWayNodes.clear();
                        currentWayOneWay = false;
                    }
                    case "nd" -> {
                        String ref = reader.getAttributeValue(null, "ref");
                        currentWayNodes.add(ref);
                    }
                    case "tag" -> {
                        String k = reader.getAttributeValue(null, "k");
                        String v = reader.getAttributeValue(null, "v");
                        if ("oneway".equals(k) && ("yes".equals(v) || "true".equals(v) || "1".equals(v))) {
                            currentWayOneWay = true;
                        }
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                String name = reader.getLocalName();

                if ("way".equals(name) && currentWayNodes.size() > 1) {
                    for (int i = 0; i < currentWayNodes.size() - 1; i++) {
                        String fromId = currentWayNodes.get(i);
                        String toId = currentWayNodes.get(i + 1);
                        graphBuilder.addEdge(fromId, toId, !currentWayOneWay);
                    }
                }
            }
        }

        reader.close();
    }
}

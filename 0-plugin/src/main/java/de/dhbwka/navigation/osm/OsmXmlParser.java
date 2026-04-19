package de.dhbwka.navigation.osm;

import de.dhbwka.navigation.GeoNode;
import de.dhbwka.navigation.GraphBuilder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;

public class OsmXmlParser {
    private final GraphBuilder<GeoNode, ? super OsmEdge<GeoNode>> graphBuilder;

    private final double DEFAULT_WIDTH = 2.75;

    public OsmXmlParser(GraphBuilder<GeoNode, ? super OsmEdge<GeoNode>> graphBuilder) {
        this.graphBuilder = graphBuilder;
    }

    public void parse(InputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

        Map<String, OsmNode> nodes = new HashMap<>();

        List<String> currentWayNodes = new ArrayList<>();

        double streetwidth = DEFAULT_WIDTH;
        boolean rejectedWay = false;

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
                        streetwidth = DEFAULT_WIDTH;
                        rejectedWay = false;
                    }
                    case "nd" -> {
                        String ref = reader.getAttributeValue(null, "ref");
                        currentWayNodes.add(ref);
                    }
                    case "tag" -> {
                        String k = reader.getAttributeValue(null, "k");
                        String v = reader.getAttributeValue(null, "v");
                        if ("width".equals(k) || "maxwidth".equals(k) || "est_width".equals(k)) {
                            streetwidth = parseWidth(v);
                        }
                        if ("train".equals(k) && "yes".equals(v) || "tram".equals(k) && "yes".equals(v) || "railway".equals(k)) {
                            rejectedWay = true;
                        }
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                String name = reader.getLocalName();

                if ("way".equals(name) && currentWayNodes.size() > 1 && !rejectedWay) {
                    for (int i = 0; i < currentWayNodes.size() - 1; i++) {
                        String fromId = currentWayNodes.get(i);
                        String toId = currentWayNodes.get(i + 1);
                        graphBuilder.addEdge(fromId, toId, streetwidth);
                        graphBuilder.addEdge(new OsmEdge<>(graphBuilder.getNode(fromId).orElseThrow(), graphBuilder.getNode(toId).orElseThrow(), true, StreetCategory.STREET , streetwidth));
                        graphBuilder.addEdge(new OsmEdge<>(graphBuilder.getNode(toId).orElseThrow(), graphBuilder.getNode(fromId).orElseThrow(), true, StreetCategory.STREET , streetwidth));
                    }
                }
            }
        }

        reader.close();
    }


    private double parseWidth(String widthStr) {
        if (widthStr == null || widthStr.trim().isEmpty()) return DEFAULT_WIDTH;
        String input = widthStr.trim();
        try {
            String numericPart = input.replaceAll("(?i)[^0-9.].*", "");
            String unitPart = input.substring(numericPart.length()).trim().toLowerCase();
            if (numericPart.isEmpty()) return DEFAULT_WIDTH;

            double value = Double.parseDouble(numericPart);
            return switch (unitPart) {
                case "m", "meter", "meters" -> value;
                case "km", "kilometer", "kilometers" -> value * 1000.;
                case "ft", "feet" -> value * 0.3048;
                case "in", "inch", "inches" -> value * 0.0254;
                case "mi", "miles" -> value * 1609.34;
                default -> value; // Assume meters if no unit or unrecognized unit
            };
        } catch (Exception e) {
            return DEFAULT_WIDTH;
        }
    }
}

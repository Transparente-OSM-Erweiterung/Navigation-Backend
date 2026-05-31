package de.dhbwka.navigation.parser;

import de.dhbwka.navigation.graph.GraphBuilder;
import de.dhbwka.navigation.osm.OsmEdge;
import de.dhbwka.navigation.osm.OsmNode;
import de.dhbwka.navigation.osm.StreetCategory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OsmXmlParser implements Parser<OsmNode, OsmEdge<OsmNode>> {

    private final double DEFAULT_WIDTH = 2.75;

    private final InputStream inputStream;

    public OsmXmlParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void parse(GraphBuilder<? super OsmNode, ? super OsmEdge<OsmNode>> builder) {

        Map<String, OsmNode> nodeCache = new java.util.HashMap<>();
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

            List<String> currentWayNodes = new ArrayList<>();

            double streetwidth = DEFAULT_WIDTH;
            boolean rejectedWay = false;
            boolean sideWalkLeft = true;
            boolean sideWalkRight = true;
            boolean isStreet = false;

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
                            nodeCache.put(id, node);
                            builder.addNode(node);
                        }
                        case "way" -> {
                            currentWayNodes.clear();
                            streetwidth = DEFAULT_WIDTH;
                            rejectedWay = false;
                            sideWalkLeft = true;
                            sideWalkRight = true;
                            isStreet = false;
                        }
                        case "nd" -> {
                            String ref = reader.getAttributeValue(null, "ref");
                            currentWayNodes.add(ref);
                        }
                        case "tag" -> {
                            String k = reader.getAttributeValue(null, "k");
                            String v = reader.getAttributeValue(null, "v");
                            if (k.equals("highway")){
                                isStreet = true;
                            }
                            if ("width".equals(k) || "maxwidth".equals(k) || "est_width".equals(k)) {
                                if(streetwidth == DEFAULT_WIDTH){
                                streetwidth = parseWidth(v);}
                            }
                            if ("train".equals(k) && "yes".equals(v) || "tram".equals(k) && "yes".equals(v) || "railway".equals(k)) {
                                rejectedWay = true;
                            }
                            if (k.equals("highway") && v.equals("traffic_signals") ||
                                k.equals("highway") && v.equals("speed_camera") ||
                                    k.equals("sidewalk:both")
                            ){
                                rejectedWay = true;
                            }

                            if (k.startsWith("sidewalk")) {
                                // 1. Der Haupt-Tag: sidewalk=yes/no/both/left/right/separate
                                if ("sidewalk".equals(k)) {
                                    switch (v) {
                                        case "both", "yes" -> {}
                                        case "left" -> sideWalkRight = false;
                                        case "right" -> sideWalkLeft = false;
                                        case "no", "none","separate"    -> rejectedWay = true;
                                    }
                                }
                                // 2. Spezifische Richtungs-Tags: sidewalk:left=yes/no/separate
                                if ("sidewalk:left".equals(k) && "separate".equals(v)
                                        || "sidewalk:right".equals(k) && "separate".equals(v) ||
                                        "cycleway:left".equals(k) && "separate".equals(v)
                                        || "cycleway:right".equals(k) && "separate".equals(v)) {
                                    rejectedWay = true;
                                }
                                else if ("sidewalk:left".equals(k)) {
                                    sideWalkRight = !"yes".equals(v); // "separate", "no", "none" setzen es auf false
                                }
                                else if ("sidewalk:right".equals(k)) {
                                    sideWalkLeft = !"yes".equals(v);
                                }
                                else if ("sidewalk:both".equals(k)) {
                                    boolean exists = "yes".equals(v);
                                    sideWalkLeft = exists;
                                    sideWalkRight = exists;
                                }
                            }
                            if (k.equals("foot") && v.equals("designated") ||
                                    k.equals("bicycle") && v.equals("designated") ||
                                    k.equals("highway") && v.equals("footway") ||
                                    k.equals("highway") && v.equals("path") ||
                                    k.equals("highway") && v.equals("track") ||
                                    k.equals("highway") && v.equals("pedestrian") ||
                                    k.equals("highway") && v.equals("cycleway") ||
                                    k.equals("highway") && v.equals("corridor") ||
                                    k.equals("highway") && v.equals("bridleway") ||
                                    k.equals("highway") && v.equals("steps") ||
                                    k.equals("playground") && v.equals("track") ||
                                    k.equals("leisure") && v.equals("barefoot")) {
                                streetwidth = 0.0;
                            }
                        }
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    String name = reader.getLocalName();

                    if ("way".equals(name) && currentWayNodes.size() > 1 && !rejectedWay && isStreet) {
                        for (int i = 0; i < currentWayNodes.size() - 1; i++) {
                            OsmNode fromNode = nodeCache.get(currentWayNodes.get(i));
                            OsmNode toNode = nodeCache.get(currentWayNodes.get(i + 1));
                            builder.addEdge(new OsmEdge<>(fromNode, toNode, StreetCategory.STREET, streetwidth, sideWalkLeft, sideWalkRight));
                            builder.addEdge(new OsmEdge<>(toNode, fromNode, StreetCategory.STREET, streetwidth, sideWalkRight, sideWalkLeft)); // Intentionally swapped Sidewalk parameters because mirrored edge
                        }
                    }
                }
            }

            reader.close();
        } catch (XMLStreamException e){
            throw new ParserException();
        }
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

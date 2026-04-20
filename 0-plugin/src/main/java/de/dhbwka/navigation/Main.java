package de.dhbwka.navigation;

import de.dhbwka.navigation.parser.OsmXmlParser;
import de.dhbwka.navigation.server.NavigationServer;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new NavigationServer(new OsmXmlParser(new FileInputStream("./run/map.osm"))).start();
    }
}

package de.dhbwka.navigation.osm;

import de.dhbwka.navigation.ExtensionNodeFactory;

public class OSMNodeFactory implements ExtensionNodeFactory<OsmNode> {
    @Override
    public OsmNode createNode(String id) {
        return new OsmNode(id, 0.0, 0.0);
    }
}

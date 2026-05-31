package de.dhbwka.navigation.extension.filter;

import de.dhbwka.navigation.graph.edge.EdgeCategory;

public enum ExtensionEdgeCategory implements EdgeCategory {
    BASE_TO_BASE,
    BASE_TO_EXTENSION,
    EXTENSION_TO_BASE,
    EXTENSION_TO_EXTENSION_CROSSING_STREET,
    EXTENSION_TO_EXTENSION_ALONG_STREET
}

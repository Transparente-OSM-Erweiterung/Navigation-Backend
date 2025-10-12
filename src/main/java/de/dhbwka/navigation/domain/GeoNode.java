package de.dhbwka.navigation.domain;

import de.dhbwka.navigation.abstraction.Node;

public interface GeoNode extends Node {
    double getLatitude();
    double getLongitude();
}

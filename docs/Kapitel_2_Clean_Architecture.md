# Kapitel 2: Clean Architecture

## Was ist Clean Architecture?
Clean Architecture ist ein Software-Architekturmuster, das darauf abzielt, Software in Schichten zu unterteilen, um eine hohe Wartbarkeit, Testbarkeit und Unabhängigkeit von externen Frameworks oder Datenbanken zu erreichen. Das Kernprinzip ist die Trennung von Belangen (*Separation of Concerns*). Dabei werden Geschäftsregeln im Zentrum der Anwendung platziert, während technische Details wie Benutzeroberflächen, Datenbanken oder externe APIs in den äußeren Schichten angesiedelt sind. Eine wesentliche Eigenschaft ist, dass die inneren Schichten keine Kenntnis über die äußeren Schichten haben dürfen, was die Anwendung robust gegenüber technologischen Änderungen macht.

## Analyse der Dependency Rule
Die *Dependency Rule* besagt, dass Abhängigkeiten im Quellcode nur von außen nach innen zeigen dürfen. Das bedeutet, dass ein Element in einer inneren Schicht (z. B. eine Entity) niemals Informationen über ein Element in einer äußeren Schicht (z. B. einen Datenbanktreiber) besitzen darf.

### Positiv-Beispiel: Dependency Rule
Ein positives Beispiel für die Einhaltung der Dependency Rule findet sich im Zusammenspiel zwischen dem Modul `2-application` und `3-domain`.

*   **Klasse:** `de.dhbwka.navigation.AStarPathFinder` (Schicht: Application)
*   **Abhängigkeit:** Die Klasse `AStarPathFinder` implementiert das Interface `PathFinder` aus der Domain-Schicht (`3-domain`).
*   **Analyse:**
    *   **Abhängigkeit nach innen:** `AStarPathFinder` (äußere Schicht) kennt und nutzt `PathFinder` sowie `Node` (innere Schicht). Dies entspricht der Regel.
    *   **Rückrichtung:** Die Domain-Interfaces (`PathFinder`, `Node`) haben keinerlei Kenntnis von der konkreten Implementierung des A*-Algorithmus oder anderen Klassen aus der Application-Schicht.
*   **UML-Inhalt:**
    *   Interface `PathFinder<T extends Node>` (aus `3-domain`).
    *   Klasse `AStarPathFinder<T extends Node>` (aus `2-application`).
    *   Ein Realisierungs-Pfeil (gestrichelt mit geschlossener Spitze) von `AStarPathFinder` zu `PathFinder`.

### Negativ-Beispiel: Dependency Rule
Ein negatives Beispiel findet sich im Modul `0-plugin`, welches eigentlich die äußerste Schicht darstellen sollte.

*   **Klasse:** `de.dhbwka.navigation.osm.OsmNode` (Schicht: Plugin/Frameworks)
*   **Problem:** Die Klasse `OsmNode` implementiert direkt das Interface `GeoNode` aus der Domain-Schicht (`3-domain`).
*   **Analyse:**
    *   **Direkte Kopplung:** Da `OsmNode` eine konkrete Datenstruktur des OSM-Parsers ist (äußerste Schicht), sollte sie nicht direkt Kern-Interfaces der Domain implementieren. Stattdessen sollte ein Adapter in der Adapter-Schicht (`1-adapters`) existieren, der zwischen den OSM-spezifischen Daten und dem Domain-Modell vermittelt. Durch die direkte Implementierung sickern technische Details der Datenquelle (OSM) potenziell in die Domain-Logik ein.
    *   **Abhängigkeit:** `OsmNode` (Schicht 0) hängt von `GeoNode` (Schicht 3) ab. Während die Richtung der Abhängigkeit formal stimmt (außen nach innen), verletzt die fehlende Abstraktion durch die Adapter-Schicht den Gedanken der Schichten-Isolierung.
*   **UML-Inhalt:**
    *   Interface `GeoNode` (aus `3-domain`).
    *   Klasse `OsmNode` (aus `0-plugin`).
    *   Direkte Implementierungsbeziehung zwischen einer Framework-Klasse und einem Domain-Kern-Element.

## Analyse der Schichten

### Schicht: Domain (Entities / Core)
*   **Klasse:** `de.dhbwka.navigation.GeoNode`
*   **Aufgabe:** Definiert die grundlegenden Eigenschaften eines geografischen Knotens (Breitengrad, Längengrad) im Navigationssystem.
*   **Einordnung & Begründung:** Diese Klasse (bzw. dieses Interface) gehört zur Domain-Schicht (entspricht dem `3-domain` Modul). Sie kapselt eine fundamentale betriebliche Enterprise-Regel: Was macht einen Ort in unserem System aus? Sie ist völlig unabhängig von der Art der Datenspeicherung oder dem verwendeten Algorithmus.
*   **UML-Inhalt:**
    *   Interface `GeoNode`.
    *   Methoden: `getLatitude(): double`, `getLongitude(): double`.
    *   Vererbung von `Node`.

### Schicht: Application (Use Cases)
*   **Klasse:** `de.dhbwka.navigation.ExtensionGraph`
*   **Aufgabe:** Implementiert die Logik für die "Transparente Erweiterung" des Graphen. Sie berechnet die Zusatzknoten und deren Nachbarschaftsbeziehungen basierend auf einem Basis-Graphen.
*   **Einordnung & Begründung:** `ExtensionGraph` gehört zur Application-Schicht (`2-application`). Sie orchestriert den Datenfluss zwischen den Domain-Objekten (`GeoNode`, `Graph`) und implementiert eine spezifische Anwendungslogik (die Dualgraph-Erweiterung), die über einfache Datenhaltung hinausgeht, aber noch kein technisches Detail der Außenwelt (wie XML-Parsing) darstellt.
*   **UML-Inhalt:**
    *   Klasse `ExtensionGraph`.
    *   Assoziation zu einem `Graph<GeoNode>`.
    *   Wichtige Methoden wie `getNeighbors(GeoNode)`.
    *   Zusammenspiel mit Hilfsklassen wie `Cramer2Solve` und `Vec2`.

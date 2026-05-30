# Kapitel 3: SOLID

## Analyse Single-Responsibility-Principle (SRP)
<!--[jeweils eine Klasse als positives und negatives Beispiel für SRP; jeweils UML der Klasse und Beschreibung der Aufgabe bzw. der Aufgaben und möglicher Lösungsweg des Negativ-Beispiels (inkl. UML)]-->

### Positiv-Beispiel
<!--[Klasse, UML und Beschreibung]-->

**Klasse:** `Cramer2Solve` (`/3-domain/src/main/java/de/dhbwka/navigation/Cramer2Solve.java`)

**Aufgabe:**
Die Klasse hat genau eine Verantwortung: die Berechnung von `x` in einem linearen 2x2-Gleichungssystem mit der Cramer-Regel.

```mermaid
classDiagram
    class Cramer2Solve {
        +solveX(Mat2 mat, Vec2 rhs) double
    }
    class Mat2
    class Vec2
    Cramer2Solve --> Mat2 : nutzt
    Cramer2Solve --> Vec2 : nutzt
```

**Begründung:**
Die Klasse kapselt ausschließlich mathematische Lösungslogik und mischt keine Parser-, I/O- oder Domänenverantwortung hinein.


### Negativ-Beispiel
<!--[Klasse, UML, Aufgabenbeschreibung und Lösungsweg]-->

**Klasse:** `ExtensionGraph` (`/2-application/src/main/java/de/dhbwka/navigation/ExtensionGraph.java`)

**Aufgaben (mehrere Verantwortlichkeiten):**
- Graph-Adapter (`Graph<GeoNode>`) für Nachbarschaften
- Berechnung geometrischer Projektionen und Schnittpunkte
- Erzeugung künstlicher Extension-Knoten
- Sortierung über Winkelberechnung und Nachbarschaftsordnung
- Parsing/Interpretation von Knoten-IDs (`isExtensionId`)

```mermaid
classDiagram
    class ExtensionGraph {
        +getNode(String id) Optional~GeoNode~
        +getNeighbors(GeoNode node) Collection~GeoNode~
        -getNeighborsOfBaseNode(GeoNode node) Compound
        -getNeighborsOfExtensionNode(GeoNode node) Compound
        +calcDeg(origin, dest, offset) double
        -isExtensionId(String id) boolean
    }
    class GraphWithWidth~GeoNode~
    class Projection
    class Cramer2Solve
    class ExtensionNode
    ExtensionGraph --> GraphWithWidth~GeoNode~
    ExtensionGraph --> Projection
    ExtensionGraph --> Cramer2Solve
    ExtensionGraph --> ExtensionNode
```

**Möglicher Lösungsweg:**
Verantwortlichkeiten aufteilen, z. B. in:
- `ExtensionTopologyService` (Topologie/Nachbarschaften)
- `IntersectionCalculator` (Geometrie/Schnittpunkte)
- `ExtensionNodeGenerator` (Erzeugung künstlicher Knoten)
- `ExtensionIdParser` (ID-Logik)

```mermaid
classDiagram
    class ExtensionGraph {
        +getNeighbors(GeoNode node) Collection~GeoNode~
    }
    class ExtensionTopologyService
    class IntersectionCalculator
    class ExtensionNodeGenerator
    class ExtensionIdParser

    ExtensionGraph --> ExtensionTopologyService
    ExtensionTopologyService --> IntersectionCalculator
    ExtensionTopologyService --> ExtensionNodeGenerator
    ExtensionTopologyService --> ExtensionIdParser
```

## Analyse Open-Closed-Principle (OCP)
[jeweils eine Klasse als positives und negatives Beispiel für OCP; jeweils UML der Klasse und Analyse mit Begründung, warum das OCP erfüllt/nicht erfüllt wurde – falls erfüllt: warum hier sinnvoll/welches Problem gab es? Falls nicht erfüllt: wie könnte man es lösen (inkl. UML)?]

### Positiv-Beispiel
<!--[Klasse, UML, Begründung und Sinnhaftigkeit]-->

**Klasse:** `AStarPathFinder` (`/2-application/src/main/java/de/dhbwka/navigation/AStarPathFinder.java`)

```mermaid
classDiagram
    class PathFinder~T~ {
        <<interface>>
        +findPath(T start, T destination) List~T~
    }
    class AStarPathFinder~T~ {
        -graph : Graph~T~
        -nextNodeScorer : Scorer~? super T~
        -heuristicScorer : Scorer~? super T~
        +findPath(T start, T destination) List~T~
    }
    class Graph~T~ {
        <<interface>>
    }
    class Scorer~T~ {
        <<interface>>
        +computeCost(T from, T to) double
    }

    PathFinder~T~ <|.. AStarPathFinder~T~
    AStarPathFinder~T~ --> Graph~T~
    AStarPathFinder~T~ --> Scorer~T~
```

**Begründung und Sinnhaftigkeit:**
Neue Bewertungslogiken (z. B. Zeitkosten, Straßentyp-Bewertung, Barrierefreiheit) können als neue `Scorer`-Implementierung ergänzt werden, ohne `AStarPathFinder` zu ändern. Das ist sinnvoll, weil die Route je nach Zielkriterium variieren soll, der Suchalgorithmus selbst aber stabil bleiben kann.


### Negativ-Beispiel
<!--[Klasse, UML, Analyse und Lösungsweg]-->

**Klasse:** `NavigationServer` (`/0-plugin/src/main/java/de/dhbwka/navigation/server/NavigationServer.java`)

```mermaid
classDiagram
    class NavigationServer {
        -graph : InMemoryGraph~GeoNode~
        -pathFinder : PathFinder~GeoNode~
        +start() void
        -handleNavigationRequest(HttpExchange) void
        -parseQueryParams(URI) Map~String,String~
    }
```

**Analyse:**
Die Klasse ist nicht gut für Erweiterungen geschlossen, weil Protokoll-Details, Request-Parsing, Antwortformat und Routing-Aufruf eng gekoppelt sind. Schon kleine neue Anforderungen (z. B. anderes Response-Format, weitere Endpunkte, andere CORS-Policy) erzwingen direkte Änderungen an derselben Klasse; z. B. würde ein zusätzliches strukturiertes JSON-Antwortschema direkt Anpassungen in `handleNavigationRequest` erfordern.

**Lösungsweg:**
HTTP-spezifische Verarbeitung, Anwendungslogik und Serialisierung trennen und über Schnittstellen erweiterbar machen.

```mermaid
classDiagram
    class NavigationServer
    class NavigationController
    class RouteService {
        <<interface>>
        +findRoute(String start, String end) List~GeoNode~
    }
    class ResponseSerializer {
        <<interface>>
        +serialize(List~GeoNode~) String
    }

    NavigationServer --> NavigationController
    NavigationController --> RouteService
    NavigationController --> ResponseSerializer
```

## Analyse LSP / ISP / DIP
[jeweils eine Klasse als positives und negatives Beispiel für entweder LSP oder ISP oder DIP); jeweils UML der Klasse und Begründung, warum man hier das Prinzip erfüllt/nicht erfüllt wird]

### Positiv-Beispiel (DIP)
<!--[Klasse, UML und Begründung]-->
**Klasse:** `OsmXmlParser` (`/0-plugin/src/main/java/de/dhbwka/navigation/osm/OsmXmlParser.java`)

```mermaid
classDiagram
    class OsmXmlParser {
        -graphBuilder : GraphBuilder~GeoNode~
        +parse(InputStream inputStream) void
    }
    class GraphBuilder~T~ {
        <<interface>>
        +addNode(T node) void
        +addEdge(String fromId, String toId, boolean bidirectional, double streetwidth) void
    }

    OsmXmlParser --> GraphBuilder~GeoNode~
```

**Begründung:**
`OsmXmlParser` hängt von der Abstraktion `GraphBuilder` ab statt von einer konkreten Implementierung wie `InMemoryGraph`. Dadurch kann der Parser mit anderen Graph-Speicherstrategien wiederverwendet werden, ohne im Parser Änderungen zu erzwingen.

### Negativ-Beispiel (DIP)
<!--[Klasse, UML und Begründung]-->
**Klasse:** `Main` (`/0-plugin/src/main/java/de/dhbwka/navigation/Main.java`)

```mermaid
classDiagram
    class Main {
        +main(String[] args) void
    }
    class InMemoryGraph~GeoNode~
    class OsmXmlParser
    class ExtensionGraph
    class HaversineScorer~GeoNode~
    class AStarPathFinder~GeoNode~
    class NavigationServer

    Main --> InMemoryGraph~GeoNode~
    Main --> OsmXmlParser
    Main --> ExtensionGraph
    Main --> HaversineScorer~GeoNode~
    Main --> AStarPathFinder~GeoNode~
    Main --> NavigationServer
```

**Begründung (nicht erfüllt):**
`Main` ist direkt an mehrere konkrete Klassen gebunden und erzeugt diese selbst. Damit hängt ein High-Level-Ablauf stark von Low-Level-Details ab; Austausch oder Tests werden unnötig aufwendig.

**Mögliche Verbesserung:**
Ein Composition-Root mit Fabriken oder Konfiguration einführen, sodass `Main` nur gegen Abstraktionen arbeitet und die konkreten Implementierungen zentral verdrahtet werden.

```mermaid
classDiagram
    class Main
    class ApplicationBootstrap {
        +createServer() NavigationServer
    }
    class GraphBuilder~GeoNode~ {
        <<interface>>
    }
    class PathFinder~GeoNode~ {
        <<interface>>
    }
    Main --> ApplicationBootstrap
    ApplicationBootstrap --> GraphBuilder~GeoNode~
    ApplicationBootstrap --> PathFinder~GeoNode~
```

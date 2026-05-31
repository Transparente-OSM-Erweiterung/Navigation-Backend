# Kapitel 1: Einführung

## Übersicht über die Applikation

Das herkömmliche Fußgänger-Routing in OpenStreetMap (OSM) führt oft zu unbefriedigenden Ergebnissen, da Fußgänger häufig auf verkehrsreichen Straßen navigiert werden, obwohl sicherere Fußwege am Straßenrand die bessere Alternative wären. Das Problem liegt darin, dass diese fahrbahnbegleitenden Fußwege im OSM-Datenmodell meist nicht explizit als separate Knoten und Kanten abgebildet sind, was ein direktes Routing auf ihnen unmöglich macht. Zweck dieser Applikation ist es daher, den bestehenden Graphen gezielt um solche Knoten und Kanten zu erweitern, um eine präzisere und sicherere Navigation für Fußgänger zu ermöglichen.

Dabei ist wichtig zu betonen, dass das erweiterte Netz das ursprüngliche OSM-Netz nicht ersetzt, sondern lediglich ergänzt. Ein Routing auf Fußwegen wird somit nicht erzwungen, sondern als zusätzliche Option angeboten, sofern dies einen kürzeren Weg darstellt. Diese Applikation konzentriert sich rein auf die strukturelle Erweiterung des Graphen; andere Lösungsansätze wie die Klassifizierung von Wegen oder die Einführung von Straffaktoren (Penalties) für Verkehrsstraßen sind nicht Bestandteil dieses Projekts.

### Funktionsweise und Konzept
Die vorliegende Applikation realisiert dies durch eine sogenannte "Transparente Erweiterung" des OSM-Ausgangsgraphen. Ziel ist es, den Graphen um zusätzliche Daten zu erweitern, ohne die ursprünglichen Daten zu verändern. Dies wird durch die Erzeugung eines dualen Graphen umgesetzt.

Dabei werden sogenannte Zusatzknoten eingeführt, die den Start- oder Endknoten einer Kante von Fuß- oder Radwegen darstellen. Die Anzahl dieser Zusatzknoten um einen Ursprungsknoten variiert je nach Kreuzungstyp und entspricht der Anzahl der anliegenden Kanten (Flächen im Dualgraphen). Zur eindeutigen Identifizierung werden diese Zusatzknoten über ihren Ursprungsknoten und einen Index referenziert, der durch eine winkelbasierte Sortierung im Einheitskreis bestimmt wird.

Die Applikation berechnet zudem die exakten geometrischen Positionen dieser Zusatzknoten unter Berücksichtigung der Straßenbreiten und der Richtungsvektoren der anliegenden Kanten. Dadurch wird ein Routingalgorithmus ermöglicht, der eine zielgerichtete Heuristik auf dem erweiterten Graphen anwenden kann.

## Wie startet man die Applikation?
Um die Applikation zu starten, müssen folgende Voraussetzungen erfüllt sein:

### Voraussetzungen
*   **Java:** Es muss ein Java Development Kit (JDK) in der Version **21** installiert sein.
*   **Gradle:** Das Projekt verwendet Gradle als Build-Tool. Die benötigte Gradle-Version ist **8.14**. Dank des mitgelieferten Gradle-Wrappers (`gradlew`) muss Gradle nicht separat installiert sein.
*   **OSM-Daten:** Im Verzeichnis `run/` muss die Datei namens `map.osm` vorhanden sein.
    *   *Zweck der Datei:* Diese Datei enthält die Rohdaten des Graphen (Knoten und Kanten) im XML-Format von OpenStreetMap. Sie dient als Grundlage für den Import und die anschließende Erweiterung des Graphen.

### Schritt-für-Schritt-Anleitung
1.  Stellen Sie sicher, dass sich die `map.osm` im Ordner `run/` befindet.
2.  Öffnen Sie ein Terminal im Projektstammverzeichnis.
3.  Bauen Sie das Projekt mit dem Befehl:
    ```bash
    ./gradlew build
    ```
4.  Starten Sie die Applikation über die Main-Klasse:
    ```bash
    ./gradlew run
    ```
    Der Server startet standardmäßig auf `http://localhost:8080`. Die Navigation kann über den Endpunkt `/nav?coords=[StartID,EndID]` aufgerufen werden.
#### Detaillierte Informationen zum Navigations-Endpunkt

Die Navigation wird über eine REST-API-Schnittstelle bereitgestellt, die Anfragen via `GET` entgegennimmt.

##### Endpunkt: `/nav`

* **Methode:** `GET`
* **Beschreibung:** Berechnet den optimalen Pfad zwischen zwei Knoten innerhalb des erweiterten Graphen.

##### Parameter

* **`coords`**: Ein String-Parameter, der die IDs des Start- und Zielknotens enthält.
* **Format:** `[StartID,EndID]`
* **Beispiel:** `?coords=[21533398,154916677]`
* *Hinweis:* Die IDs müssen als String-Werte interpretiert werden, die den Knoten-IDs in der `map.osm` entsprechen. Es handelt sich dabei um OSM-Knoten IDs. Da die in der Moodle Abgabe mitgeliferte `map.osm` Datei ein Kartensegment von Region Karlsruhe ist, können jegliche Straßenknoten-IDs innerhalb Karlsruhe aus dem OSM-Viewer (z. B. auf openstreetmap.org) zur testweisen Ausführung verwendet werden. Unten steht eine Liste an Beispielknoten bereit, die verwendet werden können.

##### Test-Knoten-IDs

Für erste Funktionstests, die sicherstellen, dass das Routing grundsätzlich funktioniert, eignen sich folgende ID-Kombinationen aus dem bereitgestellten Karlsruhe-Datensatz besonders gut:

| Start-ID      | Ziel-ID      |
|---------------|--------------|
| `25642389`    | `16718565`   |
| `15232424`    | `1886083872` |
| `15232424`    | `1722820055` |
| `1072734523`  | `34982432`   |
| `1832108909`  | `25278272`   |
| `10554554101` | `3746053829` |
| `1708378647`  | `21031223`   |
| `6561666311`  | `1725507001` |

##### Erwarteter Output

Der Server liefert eine Antwort im **JSON-Format**. Es handelt sich um ein Array von Koordinaten-Paaren, die den Pfad vom Start zum Ziel beschreiben.

* **Struktur:** `[[longitude1, latitude1], [longitude2, latitude2], ...]`
* **Beispiel:** `[[8.40029, 49.00403], [8.40050, 49.00410], ...]`

## Wie testet man die Applikation?
Die Applikation verfügt über eine Testsuite, die verschiedene Aspekte der Funktionalität abdeckt.

### Testarten und Umfang
*   **Unit Tests:** Diese testen einzelne Komponenten isoliert. Beispielsweise werden mathematische Berechnungen (Winkelberechnungen, Gleichungssysteme via `Cramer2Solve`) und grundlegende Graph-Operationen validiert.
*   **Integrationstests:** Die HTTP-Schnittstelle wurde durch manuelles Testen abgesichert. 

### Frameworks und Voraussetzungen
*   **JUnit 5 (Jupiter):** Wird als primäres Test-Framework verwendet.
*   **Gradle:** Die Tests werden über den Gradle-Test-Runner ausgeführt.

### Ausführung der Tests
Um alle Tests auszuführen, verwenden Sie den folgenden Befehl im Terminal:
```bash
./gradlew test
```
Die Testergebnisse und ein detaillierter Bericht werden anschließend im Verzeichnis `build/reports/tests/test/index.html` generiert.

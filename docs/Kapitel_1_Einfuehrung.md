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
*   **OSM-Daten:** Im Verzeichnis `run/` muss eine Datei namens `map.osm` vorhanden sein.
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

## Wie testet man die Applikation?
Die Applikation verfügt über eine Testsuite, die verschiedene Aspekte der Funktionalität abdeckt.

### Testarten und Umfang
*   **Unit Tests:** Diese testen einzelne Komponenten isoliert. Beispielsweise werden mathematische Berechnungen (Winkelberechnungen, Gleichungssysteme via `Cramer2Solve`) und grundlegende Graph-Operationen validiert.
*   **Integrationstests:** Diese prüfen das Zusammenspiel mehrerer Komponenten, insbesondere die korrekte Erzeugung des `ExtensionGraph` und die Pfadfindung mittels `AStarPathFinder` auf dem erweiterten Graphen.
*   **Geplante Bruno-Tests (API-Tests):** Zukünftig sollen API-Tests mittels Bruno implementiert werden.
    *   *Art:* Dies sind automatisierte Blackbox-Tests für die REST-Schnittstelle.
    *   *Was wird getestet:* Es wird geprüft, ob der Server auf Anfragen korrekt reagiert, gültiges JSON zurückgibt und ob die berechneten Pfade plausibel sind.
    *   *Voraussetzung:* Installierter Bruno-Client oder CLI (`bru`).
    *   *Ausführung:* Über die Bruno-GUI oder den CLI-Runner.

### Frameworks und Voraussetzungen
*   **JUnit 5 (Jupiter):** Wird als primäres Test-Framework verwendet.
*   **Gradle:** Die Tests werden über den Gradle-Test-Runner ausgeführt.

### Ausführung der Tests
Um alle Tests auszuführen, verwenden Sie den folgenden Befehl im Terminal:
```bash
./gradlew test
```
Die Testergebnisse und ein detaillierter Bericht werden anschließend im Verzeichnis `build/reports/tests/test/index.html` generiert.

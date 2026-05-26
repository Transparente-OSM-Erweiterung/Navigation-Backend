# Kapitel 6: Domain Driven Design

## Ubiquitous Language
[4 Beispiele für die Ubiquitous Language; jeweils Bezeichnung, Bedeutung und kurze Begründung, warum es zur Ubiquitous Language gehört]

| Bezeichnung                | Bedeutung                                                                                                           | Begründung                                                                                                                                                                                                                                                                                                                                      |
|:---------------------------|:--------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1. Extension Node          | Ein vom Extension Graph erzeugter Fuß- oder Radwegknoten                                                            | Der Begriff schafft eine eindeutige Abgrenzung zu Nodes des Ursprungsgraphen und verhindert, dass im Team unterschiedliche Begriffe wie Vertex oder Extra Node durcheinander genutzt werden. Damit ist Extension Node ein bewusst festgelegter Begriff, der konsistent in Code, Architektur und fachlicher Kommunikation verwendet werden kann. |
| 2. Extension Edge          | Eine vom Extension Graph erzeugte Kante, welche eine Extension Node mit einer anderen Node verbindet                | Extension Edge ist Teil der Ubiquitous Language, weil damit klar geregelt ist, welche Kanten zur Erweiterungslogik gehören und welche zum Ursprungsgraphen. Das reduziert Missverständnisse in Algorithmen und Tests und sorgt für stabile, wiedererkennbare Benennung in Klassen, Methoden und Datenstrukturen.                                |
| 3. Extension Graph         | Ein Graph, welcher Extension Nodes und Extension Edges zusätzlich zu Nodes und Edges eines Ursprungsgraphen erzeugt | Der Begriff dient zur fachlichen Trennung in zwei Ebenen. Der Extension Graph als Transparente Erweiterung und der Basis Graph als OSM-Graph-Struktur. Ohne diese feste Trennung würden schnell Inkonsistenzen in der Kommunikation und Code-Struktur entstehen.                                                                                |
| 4. Extension Edge Category | Eine Klassifizierung der Extension Edge, bezüglich der Arten an Knoten, welche die Kante verbindet                  | Die Klassifizierung der Kanten bildet die Basis für Regeln welche basierend auf der Kanten Klassifizierung Fehlerbehebungen im erweiterten Graphen durchführen. Die Definition des Begriffs erlaubt eine präzise Ausdrucksweise – sowohl im Gespräch als auch in der Logik im Produktiv-Code und in Tests.                                      |

## Entities
[UML, Beschreibung und Begründung des Einsatzes einer Entity; falls keine Entity vorhanden: ausführliche Begründung, warum es keines geben kann/hier nicht sinnvoll ist]

## Value Objects
[UML, Beschreibung und Begründung des Einsatzes eines Value Objects; falls kein Value Object vorhanden: ausführliche Begründung, warum es keines geben kann/hier nicht sinnvoll ist]

## Repositories
[UML, Beschreibung und Begründung des Einsatzes eines Repositories; falls kein Repository vorhanden: ausführliche Begründung, warum es keines geben kann/hier nicht sinnvoll ist]

## Aggregates
[UML, Beschreibung und Begründung des Einsatzes eines Aggregates; falls kein Aggregate vorhanden: ausführliche Begründung, warum es keines geben kann/hier nicht sinnvoll ist]

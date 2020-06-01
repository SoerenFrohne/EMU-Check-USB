# Anwendung zum Messen verschiedener Daten mithilfe eines EMU-Geräts

## Projekttermin 1 - Messdaten des EMU-Messgeräts erfassen und speichern
### Basisanwendung übernehmenund ergänzen
* Die Anwendung wurde mit IntelliJ statt mit Eclipse entwickelt (kann konvertiert werden) und nutzt englische Namen.
* Die SQL-Datenbank wurde entsprechend angelegt und die Verbindungsdaten entsprechend der Übung zu SQL angepasst (Verweis)
### Messungen mit dem EMU-Messgerät erfassen und speichern
* EmuService kümmert sich um die Abfrage als eigener Thread, der pausiert bis er das entsprechende EOF-Signal erhält
```java
// send request and await answer
synchronized (requestLock) {
    try {
        requestLock.wait();
        System.out.println("Result: " + result);
        System.out.println("Received result \n");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}

// Check for completion and notify
if (byteArray[0] == currentRequest.getEndOfSignalPointer()) {
    synchronized (requestLock) {
        requestLock.notify();
    }
}
```
* Beim Start des Programms wird automatisch in den Programming Mode geschaltet
* AsciUtitlity hilft beim Übersetzen der Control Characters in Bytecode (--> kein umständliches Hardcoden nötig) und hilft so beim Enum EmuRequest für das Stellen von Abfragen.
### Mehrere Messungenmit dem EMU-Messgerät erfassen und speichern

## Projekttermin 2 - JavaFX
### Oberfläche
### Messreihen erstellen und tabellarisch anzeigen
### Messungenzu einer Messreihe erfassenundspeichern

## Projekttermin 3 - 
### RESTful Webservice erstellen unter Verwendung von JSON
### Aufruf des RESTful Webservices vom Java-Client unter Verwendung vonJSON

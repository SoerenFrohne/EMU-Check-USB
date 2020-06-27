# Anwendung zum Messen verschiedener Daten mithilfe eines EMU-Geräts

## Projekttermin 1 - Messdaten des EMU-Messgeräts erfassen und speichern
### Basisanwendung übernehmenund ergänzen
* Die Anwendung wurde mit IntelliJ statt mit Eclipse entwickelt (kann konvertiert werden) und nutzt englische Namen.
* Die SQL-Datenbank wurde entsprechend angelegt und die Verbindungsdaten entsprechend der Übung zu SQL angepasst (Verweis)
### Messungen mit dem EMU-Messgerät erfassen und speichern
* EmuService kümmert sich um die Abfrage als eigener Thread, der pausiert bis er das entsprechende EOF-Signal erhält
```java
// send request and await answer in sendRequest()
synchronized (requestLock) {
    try {
        requestLock.wait();
        System.out.println("Result: " + result);
        System.out.println("Received result \n");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}

// Check for completion and notify in run()
if (byteArray[0] == currentRequest.getEndOfSignalPointer()) {
    synchronized (requestLock) {
        requestLock.notify();
    }
}
```
* Beim Start des Programms wird automatisch in den Programming Mode geschaltet
* AsciUtitlity hilft beim Übersetzen der Control Characters in Bytecode (--> kein umständliches Hardcoden nötig) und hilft so beim Enum EmuRequest für das Stellen von Abfragen.
### Mehrere Messungenmit dem EMU-Messgerät erfassen und speichern
* Nutzen eines [ScheduledExecutorService](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ScheduledExecutorService.html), der über die jeweiligen Knopfdrücke gestoppt und gestartet wird
```java
if (executorService == null) executorService = Executors.newSingleThreadScheduledExecutor();
        final int[] i = {0};
        executorService.scheduleAtFixedRate(() -> {
            selectedSeries.getMeasurements().add(getMeasurementFromEmu(String.valueOf(selectedSeries.getId()), Integer.toString(i[0])));
            i[0]++;
            table.refresh();
}, 0, selectedSeries.getTimeInterval(), TimeUnit.SECONDS);
```

## Projekttermin 2 - JavaFX
### Messreihen erstellen und tabellarisch anzeigen
* Oberfläche angelehnt an Vorgabe, aber um LayoutManager erweitert für Responsivität
### Messungenzu einer Messreihe erfassen und speichern
* MVC: View über FXML, aktuelles Modell in BasisModel, Control über TestSeriesController

## Projekttermin 3 - 
### RESTful Webservice erstellen unter Verwendung von JSON
* Webservice zu finden unter folgendem [Repository](https://github.com/SoerenFrohne/RestServer)
### Aufruf des RESTful Webservices vom Java-Client unter Verwendung von JSON
* BasisModel als Client angepasst
```java
WebResource webResource = client.resource(REST_URL + "/testseries/" + seriesId);
String input = objectMapper.writeValueAsString(measurement);
System.out.println("Saving Measurement: " + input);
ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);

String output = response.getEntity(String.class);
```

## Projekttermin 6 - Testabdeckung
### Kontrollflussgraph
```java
if (timeInterval < 15) { //1
    throw new IllegalArgumentException("time interval must be higher than 14"); //2
} else if (timeInterval > 3600) { //3
    throw new IllegalArgumentException("time interval must be lower than 3600"); //4
} else {
    this.timeInterval = timeInterval; //5
}
```
![Kontrollflussgraph für die Methode](https://raw.githubusercontent.com/SoerenFrohne/EMU-Check-USB/master/controlflow.png)

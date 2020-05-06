package main.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import main.business.Measurement;
import main.emu.EmuRequest;
import main.emu.EmuService;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeSeriesControl {
    public TextField idInput;
    public TextField consumerInput;
    public TextField measurandInput;
    public TextField intervalInput;

    private final EmuService service = new EmuService();
    private ScheduledExecutorService executorService;

    public void startScheduler(String seriesID) {
        if (executorService == null) executorService = Executors.newSingleThreadScheduledExecutor();
        final int[] i = {0};
        executorService.scheduleAtFixedRate(() -> {
            getMeasurementFromEmu(seriesID, Integer.toString(i[0]));
            i[0]++;
        }, 0, 3, TimeUnit.SECONDS);
        System.out.println("Scheduler started");
    }

    public void stopScheduler() {
        executorService.shutdown();
        System.out.println("Scheduler stopped");
    }

    public Measurement getMeasurementFromEmu(String messreihenId, String laufendeNummer) {
        Measurement measurement;
        int messId = Integer.parseInt(messreihenId);
        int lfdNr = Integer.parseInt(laufendeNummer);

        service.sendRequest(EmuRequest.WORK);
        measurement = new Measurement(lfdNr, service.parseResult());
        this.saveMeasurementsToDatabase(messId, measurement);
        return measurement;
    }

    private void saveMeasurementsToDatabase(int messreihenId, Measurement measurement) {
        try {
            this.basisModel.speichereMessungInDb(messreihenId, measurement);
        } catch (ClassNotFoundException cnfExc) {
            basisView.showError(
                    "Fehler bei der Verbindungerstellung zur Datenbank.");
        } catch (SQLException sqlExc) {
            basisView.showError(
                    "Fehler beim Zugriff auf die Datenbank.");
        }
    }

    void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehlermeldung");
        alert.setContentText(error);
        alert.showAndWait();
    }
}

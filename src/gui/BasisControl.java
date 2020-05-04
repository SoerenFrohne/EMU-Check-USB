package gui;

import business.*;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import emu.EmuRequest;
import emu.EmuService;
import javafx.stage.Stage;

public class BasisControl {

    private final BasisModel basisModel;
    private final BasisView basisView;

    private final EmuService service;
    private ScheduledExecutorService executorService;


    public BasisControl(Stage primaryStage) {
        this.basisModel = BasisModel.getInstance();
        this.basisView = new BasisView(this, primaryStage, this.basisModel);
        service = new EmuService();
        primaryStage.show();
    }

    public Measurement[] readMeasurementsFromDatabase(String seriesId) {
        Measurement[] measurements = null;
        int id = -1;

        try {
            id = Integer.parseInt(seriesId);
        } catch (NumberFormatException nfExc) {
            basisView.showError(
                    "Das Format der eingegebenen MessreihenId ist nicht korrekt.");
        }

        try {
            measurements = this.basisModel.readMeasurementsFromDatabase(id);
        } catch (ClassNotFoundException cnfExc) {
            basisView.showError(
                    "Fehler bei der Verbindungerstellung zur Datenbank.");
        } catch (SQLException sqlExc) {
            basisView.showError(
                    "Fehler beim Zugriff auf die Datenbank.");
            sqlExc.printStackTrace();
        }
        return measurements;
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

}

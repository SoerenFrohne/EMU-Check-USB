package main.business;

import javafx.collections.FXCollections;
import lombok.Getter;
import main.business.db.DatabaseService;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

public enum BasisModel {

    // Singleton instance
    INSTANCE;

    @Getter
    private final ObservableList<TestSeries> currentSeries;
    private final DatabaseService databaseService = new DatabaseService();

    BasisModel() {
        currentSeries = FXCollections.observableArrayList();
    }

    public Measurement[] readMeasurementsFromDatabase(int seriesId) throws ClassNotFoundException, SQLException {
        Measurement[] measurements;
        this.databaseService.connectDb();
        measurements = this.databaseService.readMeasurements(seriesId).toArray(new Measurement[0]);
        this.databaseService.closeDb();
        return measurements;
    }

    public void speichereMessungInDb(int messreihenId, Measurement measurement)
            throws ClassNotFoundException, SQLException {
        this.databaseService.connectDb();
        this.databaseService.InsertMeasurement(messreihenId, measurement);
        this.databaseService.closeDb();
    }

    public void readTestSeriesFromDatabaseInclusiveMeasurements() throws ClassNotFoundException, SQLException {
        this.databaseService.connectDb();
        TestSeries[] series = this.databaseService.readTestSeriesInclusiveMeasurements();
        this.databaseService.closeDb();
        int size = series.length;
        System.out.println("Read from database with size: " + Arrays.toString(series));
        if (size > 0) {
            this.currentSeries.clear();
            this.currentSeries.addAll(series);
        }
    }

    public void saveTestSeriesToDatabase(TestSeries testSeries) throws ClassNotFoundException, SQLException {
        this.databaseService.connectDb();
        this.databaseService.InsertTestSeries(testSeries);
        this.databaseService.closeDb();
        this.currentSeries.add(testSeries);
    }

    public String getDaten() {
        return "in getDaten";
    }
}

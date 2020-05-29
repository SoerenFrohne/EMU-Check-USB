package main.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.business.BasisModel;
import main.business.Measurement;
import main.business.TestSeries;
import main.emu.EmuRequest;
import main.emu.EmuService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TestSeriesControl {
    public TextField idInput;
    public TextField consumerInput;
    public TextField measurandInput;
    public TextField intervalInput;
    public TableView<TestSeries> table;
    public TableColumn<TestSeries, Integer> idColumn;
    public TableColumn<TestSeries, Integer> intervalColumn;
    public TableColumn<TestSeries, String> consumerColumn;
    public TableColumn<TestSeries, String> measurandColumn;
    public TableColumn<TestSeries, ArrayList<Measurement>> measurementsColumn;
    public Button stopButton;
    public Button startButton;

    private final EmuService emuService = new EmuService();
    private final BasisModel basisModel = BasisModel.INSTANCE;
    private ScheduledExecutorService executorService;
    private TestSeries selectedSeries;

    @FXML
    private void initialize() {

        // Init table view
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        intervalColumn.setCellValueFactory(new PropertyValueFactory<>("timeInterval"));
        consumerColumn.setCellValueFactory(new PropertyValueFactory<>("consumer"));
        measurandColumn.setCellValueFactory(new PropertyValueFactory<>("measurand"));
        measurementsColumn.setCellValueFactory(new PropertyValueFactory<>("measurements"));
        measurementsColumn.setCellFactory(cellDataFeatures -> new TableCell<TestSeries,ArrayList<Measurement>>() {
            @Override
            protected void updateItem(ArrayList<Measurement> measurements, boolean empty) {
                super.updateItem(measurements, empty);
                if (measurements == null || empty) setText("");
                else if (measurements.size() == 0) setText("No measurements available");
                else setText(measurements.stream().map(Object::toString).collect(Collectors.joining(", ")));
            }
        });

        // Check current selection
        table.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (table.getSelectionModel().getSelectedItem() != null) {
                startButton.setDisable(false);
                stopButton.setDisable(true);
                int i = table.getSelectionModel().getSelectedIndex();
                selectedSeries = basisModel.getCurrentSeries().get(i);
                //selectedSeries = table.getSelectionModel().getSelectedItem();
                //System.out.println(selectedSeries);
            } else {
                startButton.setDisable(true);
                stopButton.setDisable(true);
            }
        });

        // Display or Update table view when the data is changed
        basisModel.getCurrentSeries().addListener((InvalidationListener) observable -> table.setItems(basisModel.getCurrentSeries()));
    }

    @FXML
    public void startScheduler() {
        if (executorService == null) executorService = Executors.newSingleThreadScheduledExecutor();
        final int[] i = {0};
        executorService.scheduleAtFixedRate(() -> {
            selectedSeries.getMeasurements().add(getMeasurementFromEmu(String.valueOf(selectedSeries.getId()), Integer.toString(i[0])));
            i[0]++;
            table.refresh();
        }, 0, selectedSeries.getTimeInterval(), TimeUnit.SECONDS);
        System.out.println("Scheduler started");

        startButton.setDisable(true);
        stopButton.setDisable(false);
    }

    @FXML
    public void stopScheduler() {
        startButton.setDisable(false);
        stopButton.setDisable(true);

        executorService.shutdown();
        System.out.println("Scheduler stopped");
    }

    public Measurement getMeasurementFromEmu(String messreihenId, String laufendeNummer) {
        Measurement measurement;
        int messId = Integer.parseInt(messreihenId);
        int lfdNr = Integer.parseInt(laufendeNummer);

        emuService.sendRequest(EmuRequest.WORK);
        measurement = new Measurement(lfdNr, emuService.parseResult());
        this.saveMeasurementsToDatabase(messId, measurement);
        return measurement;
    }

    private void saveMeasurementsToDatabase(int seriesId, Measurement measurement) {
        try {
            this.basisModel.saveMeasurementToDb(seriesId, measurement);
        } catch (JsonProcessingException e) {
            showError("Fehler bei der Serialisierung der Messreihe.");
            e.printStackTrace();
        }
    }

    void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehlermeldung");
        alert.setContentText(error);
        alert.showAndWait();
    }

    @FXML
    public void readTestSeries() {
        try {
            basisModel.readTestSeriesFromDatabaseInclusiveMeasurements();
        } catch (Exception e) {
            showError("Fehler beim Datenzugriff!");
            e.printStackTrace();
        }
    }

    @FXML
    public void createSeries() {
        try {
            basisModel.saveTestSeriesToDatabase(
                    new TestSeries(
                            Integer.parseInt(idInput.getText()),
                            Integer.parseInt(intervalInput.getText()),
                            consumerInput.getText(),
                            measurandInput.getText())
            );
        } catch (ClassNotFoundException | SQLException e) {
            showError("Messreihe kann nicht gespeichert werden");
        }
    }
}

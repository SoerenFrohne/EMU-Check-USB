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
import java.util.stream.Stream;

public class TestSeriesControl {
    public Spinner<Integer> idInput;
    public TextField consumerInput;
    public ComboBox<String> measurandInput;
    public Spinner<Integer> intervalInput;
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

        // Init menu
        idInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        intervalInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));

        String[] items = Stream.of(EmuRequest.values())
                .filter(emuRequest -> !emuRequest.getUnit().equals(""))
                .map(Enum::name)
                .toArray(String[]::new);
        measurandInput.getItems().setAll(items);
        measurandInput.getSelectionModel().selectFirst();

        // Init table view

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        idColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15); // 50% width
        intervalColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15); // 30% width
        consumerColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        measurandColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        measurementsColumn.setMaxWidth(1f * Integer.MAX_VALUE * 40);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        intervalColumn.setCellValueFactory(new PropertyValueFactory<>("timeInterval"));
        consumerColumn.setCellValueFactory(new PropertyValueFactory<>("consumer"));
        measurandColumn.setCellValueFactory(new PropertyValueFactory<>("measurand"));
        measurementsColumn.setCellValueFactory(new PropertyValueFactory<>("measurements"));
        measurementsColumn.setCellFactory(cellDataFeatures -> new TableCell<TestSeries, ArrayList<Measurement>>() {
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
        executorService = Executors.newSingleThreadScheduledExecutor();
        final int[] i = {selectedSeries.getMeasurements().size()};
        executorService.scheduleAtFixedRate(() -> {
            selectedSeries.getMeasurements().add(getMeasurementFromEmu(String.valueOf(selectedSeries.getId()), Integer.toString(i[0]),
                    EmuRequest.valueOf(selectedSeries.getMeasurand())));
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

    public Measurement getMeasurementFromEmu(String messreihenId, String laufendeNummer, EmuRequest request) {
        Measurement measurement;
        int messId = Integer.parseInt(messreihenId);
        int lfdNr = Integer.parseInt(laufendeNummer);

        emuService.sendRequest(request);
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
                            idInput.getValue(),
                            intervalInput.getValue(),
                            consumerInput.getText(),
                            measurandInput.getValue())
            );
            readTestSeries();
        } catch (JsonProcessingException e) {
            showError("Fehler bei der Serialisierung der Daten.");
            e.printStackTrace();
        }
    }
}

package main.gui;

import main.business.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class BasisView {

    private final BasisModel basisModel;
    private final BasisControl basisControl;

    private final Pane pane = new Pane();
    private final Label lblMessreihenId = new Label("MessreihenId");
    private final Label lblLaufendeNummer = new Label("lfd. Nr. der Messung");
    private final TextField txtMessreihenId = new TextField();
    private final TextField txtLaufendeNummer = new TextField();
    private final TextField txtAnzeige = new TextField();
    private final Button btnLeseMessungenAusDB = new Button("Messungen aus DB lesen");
    private final Button btnHoleMessungVonEMU = new Button("Messung aus EMU aufnehmen");
    private final Button btnStarteMessreihe = new Button("Start: Messreihe aufnehmen");
    private final Button btnStoppeMessreihe = new Button("Stopp: Messreihe aufnehmen");

    public BasisView(BasisControl basisControl, Stage stage, BasisModel basisModel) {
        Scene scene = new Scene(this.pane, 510, 240);
        stage.setScene(scene);
        stage.setTitle("EMU-Anwendung");
        this.basisControl = basisControl;
        this.basisModel = basisModel;
        this.initKomponenten();
        this.initListener();
    }

    private void initKomponenten() {
        // Labels
        lblMessreihenId.setLayoutX(10);
        lblMessreihenId.setLayoutY(30);
        lblLaufendeNummer.setLayoutX(10);
        lblLaufendeNummer.setLayoutY(70);
        pane.getChildren().addAll(lblMessreihenId, lblLaufendeNummer);
        // Textfelder
        txtMessreihenId.setLayoutX(140);
        txtMessreihenId.setLayoutY(30);
        txtLaufendeNummer.setLayoutX(140);
        txtLaufendeNummer.setLayoutY(70);
        txtAnzeige.setLayoutX(10);
        txtAnzeige.setLayoutY(110);
        txtAnzeige.setPrefWidth(480);
        pane.getChildren().addAll(txtMessreihenId, txtLaufendeNummer, txtAnzeige);
        // Buttons
        btnLeseMessungenAusDB.setLayoutX(310);
        btnLeseMessungenAusDB.setLayoutY(30);
        btnLeseMessungenAusDB.setPrefWidth(180);
        btnHoleMessungVonEMU.setLayoutX(310);
        btnHoleMessungVonEMU.setLayoutY(70);
        btnHoleMessungVonEMU.setPrefWidth(180);
        btnStarteMessreihe.setLayoutX(310);
        btnStarteMessreihe.setLayoutY(150);
        btnStarteMessreihe.setPrefWidth(180);
        btnStoppeMessreihe.setLayoutX(310);
        btnStoppeMessreihe.setLayoutY(190);
        btnStoppeMessreihe.setPrefWidth(180);
        pane.getChildren().addAll(
                btnLeseMessungenAusDB, btnHoleMessungVonEMU,
                btnStarteMessreihe, btnStoppeMessreihe);
    }

    private void initListener() {
        btnLeseMessungenAusDB.setOnAction(event -> {
            Measurement[] measurements = basisControl.readMeasurementsFromDatabase(txtMessreihenId.getText());
            StringBuilder erg = new StringBuilder();
            for (Measurement measurement : measurements) {
                erg.append(measurement.toString()).append(" / ");
            }
            txtAnzeige.setText(erg.toString());
        });

        btnHoleMessungVonEMU.setOnAction(aEvent -> {
            txtAnzeige.setText(basisControl
                    .getMeasurementFromEmu(txtMessreihenId.getText(),
                            txtLaufendeNummer.getText()).toString());
        });

        btnStarteMessreihe.setOnAction(actionEvent -> basisControl.startScheduler(txtMessreihenId.getText()));

        btnStoppeMessreihe.setOnAction(actionEvent -> basisControl.stopScheduler());
    }

    void showError(String error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Fehlermeldung");
        alert.setContentText(error);
        alert.showAndWait();
    }

}


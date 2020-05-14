package main;

import main.emu.EmuRequest;
import main.emu.EmuService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/timeseries.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Hello World");
        primaryStage.show();
    }

    private static void getCommand() {
        try {
            boolean b = true;
            EmuService ecc = new EmuService();
            BufferedReader ein = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Zur Eingabe bereit:");
            while (b) {
                String eingabe = ein.readLine();
                switch (eingabe) {
                    case "exit":
                        ecc.sendRequest(EmuRequest.DISCONNECT);
                        b = false;
                        break;
                    case "connect":
                        ecc.sendRequest(EmuRequest.CONNECT);
                        break;
                    case "pmode":
                        ecc.sendRequest(EmuRequest.PMODE);
                        break;
                    case "seriennummer":
                        ecc.sendRequest(EmuRequest.SERIAL_NUMBER);
                        break;
                    case "text":
                        ecc.sendRequest(EmuRequest.TEXT);
                        break;
                    case "work":
                        ecc.sendRequest(EmuRequest.WORK);
                        break;
                    case "":
                        break;
                    default:
                        System.out.println("Falscher Befehl!");
                        break;
                }
            }
        } catch (IOException ioExc) {
            System.out.println("IOException");
            ioExc.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

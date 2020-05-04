package emu;

import gui.BasisControl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //To-Do: Testcases erstellen
        //System.out.println(Arrays.toString(AsciiUtility.convertAsciiToHexadecimal("SOH R1 STX 96.1.0() ETX", " ")));
        //System.out.println(Arrays.toString(AsciiUtility.convertAsciiToHexadecimal("SOH R1 STX 128.128() ETX", " ")));
        //getCommand();

        new BasisControl(primaryStage);
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

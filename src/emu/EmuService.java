package emu;

import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.FTDIInterface;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An EmuService is an interface between an emu device and the user.
 *
 * @author Sören Frohne
 */
public class EmuService extends Thread {

    private String result;
    private Device device = null;
    private EmuRequest currentRequest;
    private boolean connected;
    private final Object requestLock = new Object();
    private final byte[] byteArray = new byte[1];

    public EmuService() {
        Device[] devices;
        try {
            devices = FTDIInterface.getDevices();
            System.out.printf("FTD2 Devices found: %d%n", devices.length);

            for (Device d : devices) {
                if (d.getDescription().startsWith("NZR")) device = d;
            }

            device.open();
            device.setDataCharacteristics((byte) 7, (byte) 1, (byte) 2);
            device.setBaudRate(300);
            System.out.printf("Verbunden mit Device: %s%n", device.getDescription());
        } catch (FTDIException e) {
            System.out.println("Error getting devices by calling native library: " + e);
        }

        // establish connection
        connect();

        // switch to programming mode
        sendRequest(EmuRequest.PMODE);
    }

    /**
     * Establishes a connection to the emu device.
     * If a connection is already established, the call will be ignored.
     */
    public void connect() {
        connected = true;
        if (!isAlive()) {
            start();
            sendRequest(EmuRequest.CONNECT);
        }
    }

    /**
     * Stops the connection to the emu device
     */
    public void disconnect() {
        connected = false;
    }

    /**
     * Parses the requested result to double value
     * @return current result as a double
     */
    public double parseResult() {
        if (result.contains("*")) {
            int a = result.indexOf("(");
            int m = result.indexOf("*");
            result = result.substring(a + 1, m);
        }
        return Double.parseDouble(result);
    }

    /**
     * Sends a request to the device and awaits its answer
     * @param request
     */
    public void sendRequest(EmuRequest request) {
        System.out.println("Sending request started...");
        currentRequest = request;
        result = "";

        try {
            device.write(request.getRequest());
            System.out.printf("Request %s %s%n", request.getObis(), request.toString());
        } catch (FTDIException e) {
            e.printStackTrace();
        }

        synchronized (requestLock) {
            try {
                requestLock.wait();
                System.out.println("Result: " + result);
                System.out.println("Received result \n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        while (connected) {
            try {
                int queueStatus = device.getQueueStatus();
                if (queueStatus != 0) {
                    device.read(byteArray);
                    result = String.format("%s%s", result, (char) byteArray[0]);

                    // Check for completion and notify
                    if (byteArray[0] == currentRequest.getEndOfSignalPointer()) {
                        synchronized (requestLock) {
                            requestLock.notify();
                        }
                    }
                }
            } catch (FTDIException e) {
                e.printStackTrace();
            }

        }
    }
}


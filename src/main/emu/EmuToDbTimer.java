package main.emu;

import lombok.Setter;

public class EmuToDbTimer extends Thread{

    private final long timeout;
    @Setter private boolean stop = false;

    public EmuToDbTimer(long timeout) {
        super();
        this.timeout = timeout;
    }

    public void run() {
        while(! this.stop) {
            try {
                sleep(timeout);
            }
            catch(Exception exc) {
                exc.printStackTrace();
            }
        }
    }
}

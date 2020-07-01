package main.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestSeries {

    private int id;
    private int timeInterval;
    private String consumer;
    private String measurand;
    private ArrayList<Measurement> measurements;


    public TestSeries(int id, int timeInterval) {
        setId(id);
        setTimeInterval(timeInterval);
    }

    public TestSeries(int id, int timeInterval,
                      String consumer, String measurand) {
        super();
        setId(id);
        setTimeInterval(timeInterval);
        setConsumer(consumer);
        setMeasurand(measurand);
    }

    public void setTimeInterval(int timeInterval) throws IllegalArgumentException {
        if (timeInterval < 15) {
            throw new IllegalArgumentException("time interval must be higher than 14");
        } else if (timeInterval > 3600) {
            throw new IllegalArgumentException("time interval must be lower than 3600");
        } else {
            this.timeInterval = timeInterval;
        }
    }

    public void setConsumer(String consumer) {
        if (consumer.equals(" ") || consumer.equals(""))
            throw new IllegalArgumentException("consumer can't be blank/empty/null");
        else
            this.consumer = consumer;
    }

    public void setMeasurand(String measurand) {
        if (!measurand.equals("Arbeit") ^ measurand.equals("Leistung"))
            throw new IllegalArgumentException("consumer can't be blank/empty/null");
        else
            this.measurand = measurand;
    }
}

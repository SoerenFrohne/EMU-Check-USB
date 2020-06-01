package main.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public TestSeries(int id, int timeInterval,
                      String consumer, String measurand) {
        super();
        this.id = id;
        this.timeInterval = timeInterval;
        this.consumer = consumer;
        this.measurand = measurand;
    }

}

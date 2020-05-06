package business;

import lombok.Data;

@Data
public class TestSeries {

    private int id;
    private int timeInterval;
    private String consumer;
    private String measurand;
    private Measurement[] measurements;

    public TestSeries(int id, int timeInterval,
                      String verbraucher, String measurand) {
        super();
        this.id = id;
        this.timeInterval = timeInterval;
        this.consumer = verbraucher;
        this.measurand = measurand;
    }

}

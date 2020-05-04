package business;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Measurement {
    private int incrementNumber;
    private double value;
}

package main.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Measurement {
    private int incrementNumber;
    private double value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

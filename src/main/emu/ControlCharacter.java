package main.emu;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ControlCharacter {
    public String controlCode;
    public byte hexCode;
}

package main.emu;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor()
public enum EmuRequest {
    CONNECT("1.7.1", new byte[]{0x2F, 0x3F, 0x21, 0x0D, 0x0A}, "", (byte) 0x0A),

    DISCONNECT("1.7.1", new byte[]{0x01, 0x42, 0x30, 0x03}, ""),

    PMODE("1.7.1", new byte[]{0x06, 0x30, 0x30, 0x31, 0x0D, 0x0A}, ""),

    /**
     * Leistung
     */
    POWER("1.7.1", new byte[]{1, 82, 49, 2, 49, 46, 55, 46, 49, 40, 41, 3}, "W"),

    /**
     * Scheinleistung
     */
    APPARENT_POWER("9.7.1", new byte[]{1, 82, 49, 2, 57, 46, 55, 46, 49, 40, 41, 3}, "VA"),

    /**
     * Arbeit (kWh)
     */
    WORK("1.8.1", AsciiUtility.convertAsciiToHexadecimal("SOH R1 STX 1.8.1() ETX", " "), "kWh"),

    /**
     * Spannung
     */
    VOLTAGE("12.7", AsciiUtility.convertAsciiToHexadecimal("SOH R1 STX 12.7() ETX", " "), "V"),

    /*
     * Induktive Blindleistung
     *  induktive Verbraucher (Drosselspule, Transformator, Asynchronmotor)
     * Aufbau von magnetischem Feld. Die Spannung eilt dem Strom voraus -->
     * Phasenverschiebungswinkel > 0 !!!
     * Beispiele: Laptop, MP3-Player/Radio, Schneidegeraet
     */
    INDUCTIVE_REACTIVE_POWER("3.7.1", new byte[]{1, 82, 49, 2, 51, 46, 55, 46, 49, 40, 41, 3}, "VAR"),

    /*
     * Kapazitive Blindleistung
     * kapazitive Verbraucher (Kondensatormotoren, Erdkabel)
     * Aufbau von elektrischem Feld. Der Strom eilt der Spannung voraus -->
     * Phasenverschiebungswinkel < 0 !!!
     * Beispiele: Monitor, Ventilator, Halogenlampe, Laptop, Pumpe
     */
    CAPACITIVE_REACTIVE_POWER("4.7.1", new byte[]{1, 82, 49, 2, 52, 46, 55, 46, 49, 40, 41, 3}, "VAR"),

    /**
     * Leistungsfaktor
     */
    PERFORMANCE_FACTOR("13.7", new byte[]{1, 82, 49, 2, 49, 51, 46, 55, 40, 41, 3}, "cos Phi"),

    /**
     * Frequenz
     */
    FREQUENCY("14.7", AsciiUtility.convertAsciiToHexadecimal("SOH R1 STX 14.7() ETX", " "), "Hz"),

    /**
     * Seriennummer
     */
    SERIAL_NUMBER("96.1.0", new byte[]{1, 82, 49, 2, 57, 54, 46, 49, 46, 48, 40, 41, 3}, "Serial Number"),

    /**
     * Text
     */
    TEXT("128.128", new byte[]{1, 82, 49, 2, 49, 50, 56, 46, 49, 50, 56, 40, 41, 3}, "Text");


    EmuRequest(String obis, byte[] request, String unit) {
        this.obis = obis;
        this.request = request;
        this.unit = unit;
        this.endOfSignalPointer = 0x03; //Default: "ETX"
    }

    @Getter
    private final String obis;
    @Getter
    private final byte[] request;
    @Getter
    private final String unit;
    @Getter
    private final byte endOfSignalPointer;
}

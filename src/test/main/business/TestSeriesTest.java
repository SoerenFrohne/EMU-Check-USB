package main.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TestSeriesTest {

    private TestSeries sut;

    @BeforeEach
    void setUp() {
        sut = new TestSeries();
    }

    /**
     * Die  MessreihenID  sei 1, das  Zeitintervall 20,  der  Verbraucher LED
     * und  die  Messgröße Leistung.
     */
    @Test
    void testConstructor() {
        sut = new TestSeries(1, 20, "LED", "Leistung");

        assertEquals(1, sut.getId());
        assertEquals(20, sut.getTimeInterval());
        assertEquals("LED", sut.getConsumer());
        assertEquals("Leistung", sut.getMeasurand());
    }

    /**
     * Erweitern Sie die Testmethode unter Verwendung eines Parameters,
     * so dass auch der Testfall (1, 20, LED, Arbeit) getestet wird.
     * {Integer} x {15, 16,...} x {Strings} x {"Arbeit", "Leistung"}
     */
    @ParameterizedTest
    @ValueSource(strings = {"Leistung", "Arbeit"})
    void testParametrizedConstructor(String input) {
        sut = new TestSeries(1, 20, "LED", input);

        assertEquals(1, sut.getId());
        assertEquals(20, sut.getTimeInterval());
        assertEquals("LED", sut.getConsumer());
        assertEquals(input, sut.getMeasurand());
    }

    /**
     * Äquivalenzklassen:
     * id - Menge aller Integer-Werte: {Integer.NegativeInfinity; Integer.Infinity} part. Partionierung
     * timeInterval - 15, 16, ...: {-2, -1, 0, ..., 14} {15, 16, ...} vollst. Partionierung
     * consumer - Strings ohne "" und null: {Strings}, {" ", null, ""} vollst. Partionierung
     * measurand - Arbeit und Leistung: {"Arbeit", "Leistung"} part. Partionierung
     */

    @ParameterizedTest(name = "Run {index}: id={0}, timeInterval={1}, consumer={2}, measurand={3}")
    @MethodSource("validArgumentsStream")
    void testEquivalenceClasses(int id, int timeInterval, String consumer, String measurand) {
        sut = new TestSeries(id, timeInterval, consumer, measurand);

        assertEquals(id, sut.getId());
        assertEquals(timeInterval, sut.getTimeInterval());
        assertEquals(consumer, sut.getConsumer());
        assertEquals(measurand, sut.getMeasurand());
    }

    @ParameterizedTest(name = "Run {index}: id={0}, timeInterval={1}, consumer={2}, measurand={3}")
    @MethodSource("invalidArgumentsStream")
    void testInvalidEquivalenceClasses(int id, int timeInterval, String consumer, String measurand) {
        assertThrows(IllegalArgumentException.class, () -> sut = new TestSeries(id, timeInterval, consumer, measurand));
    }

    static Stream<Arguments> validArgumentsStream() {
        return Stream.of(
                arguments(1, 15, "Monitor", "Arbeit"),
                arguments(1, 15, "Monitor", "Leistung"),
                arguments(Integer.MAX_VALUE, 15, "Monitor", "Leistung")
        );
    }

    static Stream<Arguments> invalidArgumentsStream() {
        return Stream.of(
                arguments(1, 14, "Monitor", "Arbeit"),
                arguments(1, 15, "", "Leistung"),
                arguments(1, 15, " ", "Leistung"),
                arguments(1, 15, "Monitor", ""),
                arguments(1, 15, "Monitor", "Keine Arbeit")
        );
    }

    @Test
    void testNewConstructorIntervalTooLow() {
        assertThrows(IllegalArgumentException.class, () -> new TestSeries(1, 14));
    }

    @Test
    void testNewConstructorIntervalTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> new TestSeries(1, 3601));
    }

    @ParameterizedTest
    @ValueSource(ints = {15, 160, 3600})
    void testNewConstructorIntervalValid() {
        sut = new TestSeries(1, 3600);

    }


    @Test
    void testIdIsValid() {
        // {Integer.NegativeInfinity; Integer.Infinity}
        sut.setId(Integer.MIN_VALUE);
        assertEquals(sut.getId(), Integer.MIN_VALUE);

        sut.setId(Integer.MAX_VALUE);
        assertEquals(sut.getId(), Integer.MAX_VALUE);
    }

    @Test
    void testTimeIntervalIsValid() {
        // {15, 16, ...}
        sut.setTimeInterval(15);
        assertEquals(15, sut.getTimeInterval());
    }

    @Test
    void testTimeIntervalIsNotValid() {
        // {-2, -1, 0, ..., 14}
        assertThrows(IllegalArgumentException.class, () -> sut.setTimeInterval(14));
    }

    @Test
    void testConsumerIsValid() {
        // {Strings}
        sut.setConsumer("Monitor");
        assertEquals("Monitor", sut.getConsumer());
    }

    @ParameterizedTest
    //@NullAndEmptySource
    @ValueSource(strings = {" ", ""})
    void testConsumerIsNotValid(String input) {
        assertThrows(IllegalArgumentException.class, () -> sut.setConsumer(input));
        //sut.setConsumer(input);
        //assertNotEquals(input, sut.getConsumer());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Arbeit", "Leistung"})
    void testMeasurandIsValid(String input) {
        // {"Arbeit", "Leistung"}
        sut.setMeasurand(input);
        assertEquals(input, sut.getMeasurand());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Keine Arbeit", "Keine Leistung", "", " "})
    void testMeasurandIsNotValid(String input) {
        assertThrows(IllegalArgumentException.class, () -> sut.setMeasurand(input));
    }
}
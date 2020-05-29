package main.business;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.core.header.MediaTypes;
import javafx.collections.FXCollections;
import lombok.Getter;
import main.business.db.DatabaseService;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public enum BasisModel {

    // Singleton instance
    INSTANCE;

    @Getter
    private final ObservableList<TestSeries> currentSeries;
    private final DatabaseService databaseService = new DatabaseService();

    private final static String REST_URL = "http://localhost:8080/RestServer/rest/service";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Client client;

    BasisModel() {
        currentSeries = FXCollections.observableArrayList();
        client = Client.create();
    }

    public Measurement[] readMeasurementsFromDatabase(int seriesId) throws IOException {

        WebResource webResource = client.resource(REST_URL + "/testseries/" + seriesId);
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        String output = response.getEntity(String.class);

        return objectMapper.readValue(output, Measurement[].class);

    }

    public void saveMeasurementToDb(int seriesId, Measurement measurement) throws JsonProcessingException {

        WebResource webResource = client.resource(REST_URL + "/testseries/" + seriesId);
        String input = objectMapper.writeValueAsString(measurement);
        System.out.println("Saving Measurement: " + input);
        ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);

        String output = response.getEntity(String.class);
        System.out.println(output);
    }

    public void readTestSeriesFromDatabaseInclusiveMeasurements() throws IOException {

        WebResource webResource = client.resource(REST_URL + "/testseries");
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        String output = response.getEntity(String.class);

        TestSeries[] series = objectMapper.readValue(output, TestSeries[].class);
        System.out.println("Read from database: " + Arrays.toString(series));
        if (series.length > 0) {
            this.currentSeries.clear();
            this.currentSeries.addAll(series);
        }
    }

    public void saveTestSeriesToDatabase(TestSeries testSeries) throws ClassNotFoundException, SQLException {
        this.databaseService.connectDb();
        this.databaseService.InsertTestSeries(testSeries);
        this.databaseService.closeDb();
        this.currentSeries.add(testSeries);
    }

    public String getDaten() {
        return "in getDaten";
    }
}

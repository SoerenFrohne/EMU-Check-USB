package main.business.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import main.business.*;

public class DatabaseService {

    private Statement statement;
    private Connection connection;

    public ArrayList<Measurement> readMeasurements(int seriesID) throws SQLException {
        ResultSet resultSet;
        resultSet = this.statement.executeQuery("SELECT * FROM Messung WHERE MessreihenId = " + seriesID);
        ArrayList<Measurement> measurements = new ArrayList<>();
        while (resultSet.next()) {
            measurements.add(
                    new Measurement(
                            Integer.parseInt(resultSet.getString(1)),
                            Double.parseDouble(resultSet.getString(2))));
        }
        resultSet.close();
        return measurements;
    }

    public void InsertMeasurement(int seriesId, Measurement measurement)
            throws SQLException {
        String insertMessungStatement = "INSERT INTO Messung "
                + "(LaufendeNummer, Wert, MessreihenId) "
                + "VALUES(" + measurement.getIncrementNumber() + ", "
                + measurement.getValue() + ", " + seriesId + ")";
        System.out.println(insertMessungStatement);
        this.statement.executeUpdate(insertMessungStatement);
    }

    public TestSeries[] readTestSeriesInclusiveMeasurements()
            throws SQLException {
        ResultSet resultSet;
        resultSet = this.statement.executeQuery("SELECT * FROM Messreihe");
        ArrayList<TestSeries> series = new ArrayList<>();
        while (resultSet.next()) {
            series.add(
                    new TestSeries(Integer.parseInt(resultSet.getString(1)),
                            Integer.parseInt(resultSet.getString(2)),
                            resultSet.getString(3), resultSet.getString(4)));
        }
        for (TestSeries testSeries : series) {
            testSeries.setMeasurements((ArrayList<Measurement>) this.readMeasurements(testSeries.getId()));
        }
        resultSet.close();
        return series.toArray(new TestSeries[0]);
    }

    public void InsertTestSeries(TestSeries testSeries) throws SQLException {
        String insertMessreiheStatement = "INSERT INTO messreihe "
                + "(MessreihenId, Zeitintervall, Verbraucher, Messgroesse) "
                + "VALUES(" + testSeries.getId() + ", "
                + testSeries.getTimeInterval() + ", '"
                + testSeries.getConsumer() + "', '"
                + testSeries.getMeasurand() + "')";
        System.out.println(insertMessreiheStatement);
        this.statement.executeUpdate(insertMessreiheStatement);
    }


    public void connectDb()
            throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/emu-check-db?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC&sslMode=DISABLED",
                "root", "MySQLPW.80");
        statement = connection.createStatement();
        System.out.println(connection.isValid(2));
    }

    public void closeDb()
            throws SQLException {
        connection.close();
    }
}    


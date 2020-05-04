package business.db;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

import business.*;

public class DatabaseService {
 
    Statement statement;
    Connection connection;
    
    public Measurement[] readMeasurements(int seriesID)
        throws SQLException { 
        ResultSet resultSet;
        resultSet = this.statement.executeQuery(
        	"SELECT * FROM Messung WHERE MessreihenId = " + seriesID);
        Vector<Measurement> measurements = new Vector<>();
        while(resultSet.next()) {
        	measurements.add(
                new Measurement(
               	Integer.parseInt(resultSet.getString(1)),
                Double.parseDouble(resultSet.getString(2))));
         }
         resultSet.close();
         return measurements.toArray(new Measurement[0]);
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
        ResultSet ergebnis;
        ergebnis = this.statement.executeQuery(
        	"SELECT * FROM Messreihe");
        ArrayList<TestSeries> messreihen = new ArrayList<TestSeries>();
        while(ergebnis.next()) {
           	messreihen.add( 
               	new TestSeries(Integer.parseInt(ergebnis.getString(1)),
           		Integer.parseInt(ergebnis.getString(2)),
           		ergebnis.getString(3), ergebnis.getString(4)));
        }
        for(int i = 0; i < messreihen.size(); i++) {
       		messreihen.get(i).setMeasurements(
       			this.readMeasurements(messreihen.get(i).getId()));
        }
        ergebnis.close();
        return messreihen.toArray(new TestSeries[0]);
    }
    
    public void InsertTestSeries(TestSeries testSeries)
        throws SQLException {
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
		    "root", "TVNeheim1884");
        statement = connection.createStatement();
        System.out.println(connection.isValid(2));
	}
	  	
	public void closeDb()
	    throws SQLException {
		connection.close();
	}
}    


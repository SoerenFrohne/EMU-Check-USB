package business;

import business.db.DatabaseService;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.Collections;

public final class BasisModel {
	
	private static BasisModel basisModel;
	
	public static BasisModel getInstance(){
		if (basisModel == null){
			basisModel = new BasisModel();
		}
		return basisModel;
	}
	
	private BasisModel(){		
	}
	
	private final DatabaseService databaseService = new DatabaseService();
	
	// wird zukuenftig noch instanziiert
	private final ObservableList<TestSeries> messreihen = null;
	
	public Measurement[] readMeasurementsFromDatabase(int seriesId)
		throws ClassNotFoundException, SQLException{
		Measurement[] measurements;
		this.databaseService.connectDb();
		measurements = this.databaseService.readMeasurements(seriesId);
		this.databaseService.closeDb();
		return measurements;
	} 
	
	public void speichereMessungInDb(int messreihenId, Measurement measurement)
		throws ClassNotFoundException, SQLException{
		this.databaseService.connectDb();
		this.databaseService.InsertMeasurement(messreihenId, measurement);
		this.databaseService.closeDb();
	} 
	
	public void leseMessreihenInklusiveMessungenAusDb()
		throws ClassNotFoundException, SQLException{
		this.databaseService.connectDb();
		TestSeries[] messreihenAusDb
		    = this.databaseService.readTestSeriesInclusiveMeasurements();
		this.databaseService.closeDb();
		int anzahl = this.messreihen.size();
		if (anzahl > 0) {
			this.messreihen.subList(0, anzahl).clear();
			Collections.addAll(this.messreihen, messreihenAusDb);
		}
	}
		  
	public void speichereMessreiheInDb(TestSeries testSeries)
	  	throws ClassNotFoundException, SQLException{
	  	this.databaseService.connectDb();
	  	this.databaseService.InsertTestSeries(testSeries);
	  	this.databaseService.closeDb();
	  	this.messreihen.add(testSeries);
	} 
	
  	public String getDaten(){
    	return "in getDaten";
	} 
 }

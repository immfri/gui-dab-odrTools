package model;

import java.io.File;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import model.output.*;



public class Multiplex {
	
	private StringProperty dabmode, nbframes;
	private BooleanProperty syslog, writescca, tist;
	private StringProperty managementport, telnetport;
	private IntegerProperty totalCU;
	
	private static Multiplex instance;	
	
	private Ensemble ensemble;
	
	private ObservableList<Service> serviceList;
	private ObservableList<Component> componentList;
	private ObservableList<Subchannel> subchannelList;	
	private ObservableList<Output> outputList;
	
	private File projectFolder = null;
	private StringProperty mail;
	
	
	private Multiplex() {		
		
		dabmode = 			new SimpleStringProperty("1");	
		nbframes = 			new SimpleStringProperty("0");
		managementport = 	new SimpleStringProperty("0"); 	
		telnetport = 		new SimpleStringProperty("0");
		syslog = 			new SimpleBooleanProperty(false);
		writescca = 		new SimpleBooleanProperty(false);
		tist =				new SimpleBooleanProperty(false);
		totalCU = 			new SimpleIntegerProperty(0);
		
		ensemble = 			new Ensemble();
		
		serviceList = 		FXCollections.observableArrayList();
		componentList = 	FXCollections.observableArrayList();	
		subchannelList = 	FXCollections.observableArrayList();
		outputList =  		FXCollections.observableArrayList();
		
		// Subchannel Listener -> CU
		subchannelList.addListener((ListChangeListener<Subchannel>) change -> updateCU());	
		
		
		mail = new SimpleStringProperty("");
	}
	
	public static synchronized Multiplex getInstance() {
	    if (Multiplex.instance == null) {
	      Multiplex.instance = new Multiplex();
	    }
	    return Multiplex.instance;
	}
	
	public ObservableList<String> getDabModeList() {
		return FXCollections.observableArrayList("1","2","3","4");
	}
	

	public StringProperty getDabMode() {
		return dabmode;
	}

	public StringProperty getNbframes() {
		return nbframes;
	}

	public StringProperty getManagementport() {
		return managementport;
	}

	public StringProperty getTelnetPort() {
		return telnetport;
	}

	public BooleanProperty getSyslog() {
		return syslog;
	}

	public BooleanProperty getWritescca() {
		return writescca;
	}

	public BooleanProperty getTist() {
		return tist;
	}

	public IntegerProperty getTotalCU() {
		return totalCU;
	}

	public Ensemble getEnsemble() {
		return ensemble;
	}

	public ObservableList<Subchannel> getSubchannelList() {
		return subchannelList;
	}

	public ObservableList<Service> getServiceList() {
		return serviceList;
	}

	public ObservableList<Output> getOutputList() {
		return outputList;
	}

	public ObservableList<Component> getComponentList() {
		return componentList;
	}

	public File getProjectFolder() {
		return projectFolder;
	}
	
	public void setProjectFolder(File folder) {
		projectFolder = folder;
	}
	
	public void updateCU() {
		int cu = 0;
		for (Subchannel subCh: Multiplex.getInstance().getSubchannelList()) {
			cu += subCh.getInput().getCu().get();
		}
		totalCU.set(cu);
	}

	public void newGeneral() {
		dabmode.setValue("1");	
		nbframes.setValue("0");
		managementport.setValue("0"); 	
		telnetport.setValue("0");
		syslog.setValue(false);
		writescca.setValue(false);
		tist.setValue(false);
	}
	
	public void newEnsemble() {
		ensemble.getEcc().setValue("");
		ensemble.getId().setValue("");
		ensemble.getIntTable().setValue("1");
		ensemble.getLabel().setValue("");
		ensemble.getShortLabel().setValue("");
		ensemble.getLocalTimeOffset().setValue("auto");
	}

	public StringProperty getEMail() {
		return mail;
	}	
}

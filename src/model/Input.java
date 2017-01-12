package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Input {
	
	protected StringProperty source, path, type, bitrate, output, protectionProfile, protectionLevel;
	protected IntegerProperty cu;
	
	protected ObservableList<String> sourceList, bitrateList, protectionLevelList;
	
	
	public Input(String type, String profile) {
		this.type = 		new SimpleStringProperty(type);	

		source = 			new SimpleStringProperty("");	
		output = 			new SimpleStringProperty("");
		path = 				new SimpleStringProperty("");		
		bitrate =			new SimpleStringProperty("");
		protectionProfile =	new SimpleStringProperty(profile);	
		protectionLevel =	new SimpleStringProperty("");	
		cu =				new SimpleIntegerProperty(0);	
		
		bitrateList = 			FXCollections.observableArrayList();
		protectionLevelList = 	FXCollections.observableArrayList();
		sourceList = 			FXCollections.observableArrayList();
		
		cu.addListener(change -> Multiplex.getInstance().updateCU());			// update CU on ProgressPane
	}


	public StringProperty getOutput() {
		return output;
	}
	
	public StringProperty getType() {
		return type;
	}
		
	public StringProperty getSource() {
		return source;
	}

	public StringProperty getPath() {
		return path;
	}

	public StringProperty getProtectionLevel() {
		return protectionLevel;
	}
	
	public StringProperty getProtectionProfile() {
		return protectionProfile;
	} 
	
	public ObservableList<String> getProtectionLevelList() {
		return protectionLevelList;
	}

	public IntegerProperty getCu() {
		return cu;
	}

	public StringProperty getBitrate() {
		return bitrate;
	}

	public ObservableList<String> getBitrateList() {
		return bitrateList;
	}

	
	public ObservableList<String> getSourceList() {
		return sourceList;
	}
	
	
	public abstract void updateCU(); 
	public abstract void updatePlList(boolean advancedView);


}

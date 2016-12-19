package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class Component extends Element {
	
	private StringProperty type, id, figtype;
	private IntegerProperty address;
	private BooleanProperty datagroup;
	
	private Service service;
	private Subchannel subchannel;
	
	
	
	public Component(String name) {
		super(name);
		
		type = 			new SimpleStringProperty(getTypeList().get(0));
		id = 			new SimpleStringProperty("");
		figtype = 		new SimpleStringProperty("");
		address = 		new SimpleIntegerProperty(0);
		datagroup = 	new SimpleBooleanProperty(false);
		
	}

	
	
	public ObservableList<String> getTypeList() {
		
		if (subchannel != null) {
			String inputType = subchannel.getInput().getType().get();
			
			if (inputType.contains("audio") || inputType.contains("dabplus")) {
				return FXCollections.observableArrayList("foreground","background","multi-channel");
			} 
			return FXCollections.observableArrayList("unspecified","TMC","EWS","ITTS","paging","TDC","IP","MOT","proprietary");
		}
		return FXCollections.observableArrayList("select first subchannel");
	}
	

	public StringProperty getType() {
		return type;
	}
	
	public StringProperty getFigtype() {
		return figtype;
	}

	public IntegerProperty getAddress() {
		return address;
	}
	
	public BooleanProperty getDatagroup() {
		return datagroup;
	}
	
	public Subchannel getSubchannel() {
		return subchannel;	
	}

	public void setService(Service s) {
		service = s;
	}

	public Service getService() {
		return service;
	}

	public StringProperty getId() {
		return id;
	}	

	public void setSubchannel(Subchannel sub) {
		subchannel = sub;	
	}
}

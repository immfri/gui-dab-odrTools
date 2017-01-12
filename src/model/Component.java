package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;



public class Component extends Element {
	
	private Service service;
	private Subchannel subchannel;
	
	private StringProperty type, id, figtype, address;
	private BooleanProperty datagroup;
	
	
	public Component(String name) {
		super(name);
		
		type = 			new SimpleStringProperty("0");
		id = 			new SimpleStringProperty("");
		figtype = 		new SimpleStringProperty("");
		address = 		new SimpleStringProperty("");
		datagroup = 	new SimpleBooleanProperty(false);
	}
	

	public StringProperty getType() {
		return type;
	}
	
	public StringProperty getFigtype() {
		return figtype;
	}

	public StringProperty getAddress() {
		return address;
	}
	
	public BooleanProperty getDatagroup() {
		return datagroup;
	}
	
	public StringProperty getId() {
		return id;
	}	
	
	
	public void setSubchannel(Subchannel sub) {
		subchannel = sub;	
	}
	public Subchannel getSubchannel() {
		return subchannel;	
	}

	
	public void setService(Service serv) {
		service = serv;	
	}
	public Service getService() {
		return service;
	}

	
}

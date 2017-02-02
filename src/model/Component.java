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
		
		// set fig-type 0x2 if SLS in audio selected
		if (sub != null) {
			if (!sub.getType().getValue().contentEquals("data") && !sub.getType().getValue().contains("packet")) {
				
				Audio audio = (Audio)sub.getInput();
				
				if (audio.getPad().getSlsEnabled().getValue()) {
					figtype.setValue("0x2");
				}
				else {
					figtype.setValue("");
				}
				
				audio.getPad().getSlsEnabled().removeListener(c -> changeFigtype(audio.getPad().getSlsEnabled()));
				audio.getPad().getSlsEnabled().addListener(c -> changeFigtype(audio.getPad().getSlsEnabled()));			
			}
		}
	}
	
	
	private void changeFigtype(BooleanProperty slsEnabled) {
			
		if (slsEnabled.getValue()) {
			figtype.setValue("0x2");
		} 
		else {	
			figtype.setValue("");
			figtype.setValue("");
		}
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

package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.Element;


public abstract class Output extends Element {

	protected StringProperty format, destination;
	
	
	public Output(String name, String format, String destination) {
		super(name);
		
		this.format = 		new SimpleStringProperty(format);
		this.destination = 	new SimpleStringProperty(destination);
	}
	
	
	
	
	public StringProperty getFormat() {
		return format;
	}

	public StringProperty getDestination() {
		return destination;
	}	
}

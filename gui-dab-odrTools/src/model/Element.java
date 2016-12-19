package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;



public class Element {
	
	protected StringProperty name;
	
	
	public Element(String name) {
		
		this.name = new SimpleStringProperty(name);
	}
	
	
	
	public StringProperty getName() {
		return name;
	}

}

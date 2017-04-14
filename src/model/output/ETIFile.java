package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class ETIFile extends Output {

	private StringProperty type;
	private ObservableList<String> typeList;
	
	
	public ETIFile(String name) {
		super(name, "fifo", "/dev/stdout");
		
		type = new SimpleStringProperty("raw");
		typeList = FXCollections.observableArrayList("raw","streamed","framed");
	}


	
	public StringProperty getType() {
		return type;
	}
	
	

	public ObservableList<String> getTypeList() {
		return typeList;
	}
	
}

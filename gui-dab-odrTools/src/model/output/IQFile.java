package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class IQFile extends Modulator {
	
	private StringProperty format, filename;
	private ObservableList<String> formatList;
	
	

	public IQFile() {
		super("file");
		
		format =	new SimpleStringProperty("complexf");	
		filename = 	new SimpleStringProperty("/dev/stdout");
		
		formatList = FXCollections.observableArrayList("complexf","s8");
	}



	public StringProperty getFormat() {
		return format;
	}

	public StringProperty getFilename() {
		return filename;
	}

	public ObservableList<String> getFormatList() {
		return formatList;
	}
	
}

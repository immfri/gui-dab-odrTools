package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class IQFile extends Modulator {
	
	private StringProperty format, file;	
	private ObservableList<String> formatList;

	public IQFile() {
		super("file");
		
		formatList = FXCollections.observableArrayList("complexf", "s8");
		
		format =	new SimpleStringProperty(formatList.get(0));	
		file = 		new SimpleStringProperty("/dev/stdout");
				
	}



	public StringProperty getFormat() {
		return format;
	}

	public StringProperty getFile() {
		return file;
	}

	public ObservableList<String> getFormatList() {
		return formatList;
	}
	
}

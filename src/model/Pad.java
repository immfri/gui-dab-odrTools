package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Pad {

	
	private StringProperty charset, filePad, dirSlides, fileDls, length, delay;
	private BooleanProperty dlsEnabled, slsEnabled, removeDls, rawDls, rawSlides, eraseSlides;  
	
	private ObservableList<String> charsetList;
	
	
	
	public Pad() { 								// new Project
		
		charsetList =		FXCollections.observableArrayList("UTF-8","EBU Latin","UCS-2 BE");
				
		dlsEnabled =   		new SimpleBooleanProperty(false);
		fileDls = 			new SimpleStringProperty("");
		charset =			new SimpleStringProperty(charsetList.get(0));	// covert to -> "15" e.g. Hashmap
		rawDls =   			new SimpleBooleanProperty(false);
		removeDls = 		new SimpleBooleanProperty(false);
		
		
		slsEnabled = 		new SimpleBooleanProperty(false);
		dirSlides =	 		new SimpleStringProperty("");
		delay =   			new SimpleStringProperty("10");
		rawSlides = 		new SimpleBooleanProperty(false);
		eraseSlides = 		new SimpleBooleanProperty(false);
			
		length = 			new SimpleStringProperty("58");
		filePad =			new SimpleStringProperty("");
		
	}



	public StringProperty getCharset() {
		return charset;
	}
	
	public ObservableList<String> getCharsetList() {
		return charsetList;
	}


	public StringProperty getFileDls() {
		return fileDls;
	}


	public StringProperty getDirSlides() {
		return dirSlides;
	}


	public StringProperty getDelay() {
		return delay;
	}


	public StringProperty getFilePad() {
		return filePad;
	}


	public StringProperty getLength() {
		return length;
	}


	public BooleanProperty getRemoveDls() {
		return removeDls;
	}


	public BooleanProperty getRawDls() {
		return rawDls;
	}


	public BooleanProperty getRawSlides() {
		return rawSlides;
	}
	
	public BooleanProperty getEraseSlides() {
		return eraseSlides;
	}


	public BooleanProperty getSlsEnabled() {
		return slsEnabled;
	}
	
	public BooleanProperty getDlsEnabled() {
		return dlsEnabled;
	}
	

}

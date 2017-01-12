package model;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Ensemble {
	
	private StringProperty id, ecc, international_table, local_time_offset, label, shortlabel;
	
	public Ensemble() {
		
		id = 					new SimpleStringProperty("");
		ecc = 					new SimpleStringProperty("");
		label = 				new SimpleStringProperty("");
		shortlabel = 			new SimpleStringProperty("");
		local_time_offset = 	new SimpleStringProperty("auto");
		international_table = 	new SimpleStringProperty("1");

	}


	
	public StringProperty getLabel() {
		return label;
	}

	public StringProperty getShortLabel() {
		return shortlabel;
	}

	public StringProperty getId() {
		return id;
	}

	public StringProperty getEcc() {
		return ecc;
	}
	
	public StringProperty getIntTable() {
		return international_table;
	}

	public StringProperty getLocalTimeOffset() {
		return local_time_offset;
	}
	
	public ObservableList<String> getLocalTimeOffsetList() {
		ArrayList<String> offsetList = new ArrayList<>();
		for (double offset = -12; offset <= 12; offset+=0.5) {
			offsetList.add(""+offset);
		}
		offsetList.add(24, "auto");
		return FXCollections.observableArrayList(offsetList);
	}

}

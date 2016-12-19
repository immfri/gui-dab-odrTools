package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Ensemble {
	
	private StringProperty id, label, shortLabel, ecc, country, localTimeOffset;
	private IntegerProperty internationalTable;
	
	
	public Ensemble() {
		
		id = 				new SimpleStringProperty("");
		ecc = 				new SimpleStringProperty(""); 
		country = 			new SimpleStringProperty(Locale.getDefault().getDisplayCountry()); 
		label = 			new SimpleStringProperty("");
		shortLabel = 		new SimpleStringProperty("");
		localTimeOffset = 	new SimpleStringProperty("auto");
		internationalTable = new SimpleIntegerProperty(1);
		
		country.addListener(chnage -> setInternationalTabel());
		setInternationalTabel();
	}


	public ObservableList<String> getLocalTimeOffsetList() {
		ArrayList<String> offsetList = new ArrayList<>();
		for (double offset = -12; offset <= 12; offset+=0.5) {
			offsetList.add(""+offset);
		}
		offsetList.add(24, "auto");
		return FXCollections.observableArrayList(offsetList);
	}
	
	
	private void setInternationalTabel() {
		
		if(country.get().contains("US") || country.get().contains("CA")) {
			internationalTable.set(2);
		} 
		else {
			internationalTable.set(1);
		}
	}


	public StringProperty getId() {
		return id;
	}


	public ObservableList<String> getCountryList() {
		
		String[] isoCountries = Locale.getISOCountries();
		String[] countries = new String[isoCountries.length];
		
		for(int i=0; i<isoCountries.length; i++) {
			Locale locale = new Locale(Locale.getDefault().getCountry(),isoCountries[i]);
			countries[i] = locale.getDisplayCountry();
		}
		Arrays.sort(countries);
		return FXCollections.observableArrayList(countries);
	}
	
	public StringProperty getCountry() {
		return country;
	}

	public StringProperty getEcc() {
		return ecc;
	}


	public StringProperty getLabel() {
		return label;
	}


	public StringProperty getShortLabel() {
		return shortLabel;
	}


	public StringProperty getLocalTimeOffset() {
		return localTimeOffset;
	}


	public IntegerProperty getInternationalTable() {
		return internationalTable;
	}
	

}

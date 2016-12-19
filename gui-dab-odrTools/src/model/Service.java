package model;

import java.util.Arrays;
import java.util.Locale;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class Service extends Element {

	private StringProperty id, label, shortLabel, pty, language;
	
	
	public Service(String name) {
		super(name);
		
		id = 			new SimpleStringProperty("");
		label = 		new SimpleStringProperty("");
		shortLabel = 	new SimpleStringProperty("");
		pty = 			new SimpleStringProperty(getPtyList().get(0));
		language = 		new SimpleStringProperty(Locale.getDefault().getDisplayLanguage());
	}



	public StringProperty getId() {
		return id;
	}

	public StringProperty getLabel() {
		return label;
	}

	public StringProperty getShortLabel() {
		return shortLabel;
	}

	public StringProperty getPty() {
		return pty;
	}

	public StringProperty getLanguage() {
		return language;
	}





	public ObservableList<String> getLanguageList() {	//TODO etsi dab languages
		String[] isoLanguages = Locale.getISOLanguages();
		String[] languages = new String[isoLanguages.length];
		
		for(int i=0; i<isoLanguages.length; i++) {
			 Locale local = new Locale(isoLanguages[i]);
			 languages[i] = local.getDisplayLanguage();
		}
		Arrays.sort(languages);
		return FXCollections.observableArrayList(languages);
	}


	public ObservableList<String> getPtyList() {
		
		String country = Locale.getDefault().getCountry();
		if(country.contains("US") || country.contains("CA")) {
			return FXCollections.observableArrayList(
						"No program type",
						"News",
						"Information",
						"Sports",
						"Talk",
						"Rock",
						"Classic Rock",
						"Adult Hits",
						"Soft Rock",
						"Top 40",
						"Country",
						"Oldies",
						"Soft",
						"Nostalgia",
						"Jazz",
						"Classical",
						"Rhythm and Blues",
						"Soft Rhythm and Blues",
						"Foreign Language",
						"Religious Music",
						"Religious Talk",
						"Personality",
						"Public",
						"College",
						"Weather"
						);
		}
		return FXCollections.observableArrayList(
				"No programme type",
				"News",
				"Current Affairs",
				"Information",
				"Sport",
				"Education",
				"Drama",
				"Culture",
				"Arts",
				"Science",
				"Varied",
				"Pop Music",
				"Rock Music",
				"Easy Listening Music",
				"Light Classical",
				"Serious Classical",
				"Other Music",
				"Weather",
				"Finance",
				"Children's programmes",
				"Social Affairs",
				"Religion",
				"Phone In",
				"Travel",
				"Leisure",
				"Jazz Music",
				"Country Music",
				"National Music",
				"Oldies Music",
				"Folk Music",
				"Documentary"
				);
	}
}

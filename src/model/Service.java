package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;


public class Service extends Element {

	private StringProperty id, label, shortlabel, language, pty;
	
	
	public Service(String name) {
		super(name);
		
		id = 			new SimpleStringProperty("");
		label = 		new SimpleStringProperty("");
		shortlabel = 	new SimpleStringProperty("");
		language = 		new SimpleStringProperty("0x00");
		pty = 			new SimpleStringProperty("0");
		
	}



	public StringProperty getId() {
		return id;
	}

	public StringProperty getLabel() {
		return label;
	}

	public StringProperty getShortLabel() {
		return shortlabel;
	}

	public StringProperty getPty() {
		return pty;
	}

	public StringProperty getLanguage() {
		return language;
	}



	public ObservableMap<String, Integer> getPtyList() {
		
		String ecc = Multiplex.getInstance().getEnsemble().getEcc().getValue();
		ObservableMap<String, Integer> ptyMap = FXCollections.observableHashMap();
		  
		  
		if (ecc.contentEquals("0xa0") || ecc.contentEquals("0xa1")) { // only for North America
			
			ptyMap.put("No program type",		0);
			ptyMap.put("News", 					1);
			ptyMap.put("Information", 			2);
			ptyMap.put("Sports", 				3);
			ptyMap.put("Talk", 					4);
			ptyMap.put("Rock", 					5);
			ptyMap.put("Classic Rock", 			6);
			ptyMap.put("Adult Hits", 			7);
			ptyMap.put("Soft Rock", 			8);
			ptyMap.put("Top 40", 				9);
			ptyMap.put("Country", 				10);
			ptyMap.put("Oldies", 				11);
			ptyMap.put("Soft", 					12);
			ptyMap.put("Nostalgia", 			13);
			ptyMap.put("Jazz", 					14);
			ptyMap.put("Classical", 			15);
			ptyMap.put("Rhythm and Blues", 		16);
			ptyMap.put("Soft Rhythm and Blues",	17);
			ptyMap.put("Foreign Language", 		18);
			ptyMap.put("Religious Music", 		19);
			ptyMap.put("Religious Talk", 		20);
			ptyMap.put("Personality", 			21);
			ptyMap.put("Public", 				22);
			ptyMap.put("College", 				23);
			ptyMap.put("Weather", 				29);
		}
		else {
			ptyMap.put("No programme type", 	0);
			ptyMap.put("News", 					1);
			ptyMap.put("Current Affairs", 		2);
			ptyMap.put("Information", 			3);
			ptyMap.put("Sport", 				4);
			ptyMap.put("Education", 			5);
			ptyMap.put("Drama", 				6);
			ptyMap.put("Culture", 				7);
			ptyMap.put("Arts", 					8);
			ptyMap.put("Science", 				9);
			ptyMap.put("Varied", 				10);
			ptyMap.put("Pop Music", 			11);
			ptyMap.put("Rock Music", 			12);
			ptyMap.put("Easy Listening Music", 	13);
			ptyMap.put("Light Classical", 		14);
			ptyMap.put("Serious Classical", 	15);
			ptyMap.put("Other Music", 			16);
			ptyMap.put("Weather", 				17);
			ptyMap.put("Finance", 				18);
			ptyMap.put("Children's programmes", 19);
			ptyMap.put("Social Affairs", 		20);
			ptyMap.put("Religion", 				21);
			ptyMap.put("Phone In", 				22);
			ptyMap.put("Travel", 				23);
			ptyMap.put("Leisure", 				24);
			ptyMap.put("Jazz Music", 			25);
			ptyMap.put("Country Music", 		25);
			ptyMap.put("National Music", 		26);
			ptyMap.put("Oldies Music", 			27);
			ptyMap.put("Folk Music", 			28);
			ptyMap.put("Documentary", 			29);
		}	
			
		return ptyMap;
	}
}

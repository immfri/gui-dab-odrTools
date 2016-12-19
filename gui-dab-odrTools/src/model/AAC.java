package model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AAC extends Audio {
	

	public AAC() {
		super("dabplus");
		
		bitrateList = 			getDabPlusBitrateList();			
		protectionLevelList = 	FXCollections.observableArrayList("1-A","2-A","3-A","4-A");
		
		bitrate.set(bitrateList.get(0));								// 8 kbit/s	
		protectionLevel.set(protectionLevelList.get(0));				// PL 1-A
		
		updateCU();
	}

	
	
	private ObservableList<String> getDabPlusBitrateList() {
	
		ArrayList<String> list = new ArrayList<>();
		
		for (int i=1; i<25; i++) {
			list.add(""+8*i);
		}	
		return FXCollections.observableArrayList(list);
	}
	
	
	public void updatePlList(boolean advancedView) {
		
		if (advancedView && Integer.parseInt(bitrate.get())%32 == 0 && !protectionLevelList.contains("1-B")) {
			for (int i=1; i<5; i++) {
				protectionLevelList.add(i + "-B");
			}
		} 
		else {
			protectionLevelList.removeAll("1-B","2-B","3-B","4-B");	
		}
		
		if (protectionLevel.get().contains(protectionLevelList.get(0))) {	// if old PL same to current
			updateCU();
		} 
		else {
			protectionLevel.set(protectionLevelList.get(0));
		}
	}
	
	
	public void updateCU() {
		int plIndex = (int) protectionLevel.get().charAt(0) - 49;
		int rate = Integer.parseInt(bitrate.get());

		if (protectionLevel.get().contains("A")) {		// EEP_A
			int[] plFactor = {12,8,6,4};
			cu.set(rate*plFactor[plIndex]/8);
		} 
		else {											// EEP_B
			int[] plFactor = {27,21,18,15};
			cu.set(rate*plFactor[plIndex]/32);
		}
	}
}

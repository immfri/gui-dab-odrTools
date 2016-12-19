package model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Data extends Input {

	public Data() {
		super("data");
		
		sourceList = 			FXCollections.observableArrayList("File");
		bitrateList = 			FXCollections.observableArrayList(getDabPlusBitrateList());
		protectionLevelList = 	FXCollections.observableArrayList("1-A","2-A","3-A","4-A");	
		
		source.set(sourceList.get(0));
		bitrate.set(bitrateList.get(1));
		protectionLevel.set(protectionLevelList.get(0));	
		
		updateCU();
	}

	
	
	@Override
	public void updateCU() {
		int plIndex = (int) protectionLevel.get().charAt(0) - 49;
		int[] plFactor = {12,8,6,4};
		int rate = Integer.parseInt(bitrate.get());
		
		cu.set(rate*plFactor[plIndex]/8);
	}
	
	
	@Override
	public void updatePlList(boolean advancedView) {
		
		if (protectionLevel.get().contains(protectionLevelList.get(0))) {	// if old PL same to current
			updateCU();
		} 
		else {
			protectionLevel.set(protectionLevelList.get(0));
		}
	}
	
	
	private ObservableList<String> getDabPlusBitrateList() {
		
		ArrayList<String> list = new ArrayList<>();
		
		for (int i=1; i<25; i++) {
			list.add(""+8*i);
		}	
		return FXCollections.observableArrayList(list);
	}
}

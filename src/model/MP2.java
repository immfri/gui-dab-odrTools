package model;

import javafx.collections.FXCollections;


public class MP2 extends Audio {

	
	public MP2() {
		super("audio", "UEP");
		
		bitrateList = 			FXCollections.observableArrayList("32","48","56","64","80","96","112","128","160","192","224","256","320","384");										
		protectionLevelList = 	FXCollections.observableArrayList("1","2","3","4","5");		 						
		
		
		bitrate.set(bitrateList.get(0));								// 32 kbit/s	
		protectionLevel.set(protectionLevelList.get(0));				// PL 1
		
		updateCU();
		bitrate.addListener(c -> updateCU());
	}

	
	public void updatePlList(boolean advancedView) {
		
		for (int i=1; i<6; i++) {
			if (!protectionLevelList.contains(""+i)) protectionLevelList.add(i-1, ""+i);
		}
		
		switch (bitrate.get()) {
			case "56": 
				protectionLevelList.remove("1");
				break;
				
			case "112": 
				protectionLevelList.remove("1");
				break;
				
			case "320": 
				protectionLevelList.removeAll("1","3");
				break;
				
			case "384": 
				protectionLevelList.removeAll("2","4");
		}
		
		// Set Protection and Update CU
//		if (protectionLevel.getValue().contentEquals(protectionLevelList.get(0))) {
//			updateCU();
//		} 
//		else {
			protectionLevel.set(protectionLevelList.get(0));
//		}	
	}
	
	public void updateCU() {
		
		int brIndex = getBitrateList().indexOf(bitrate.getValue());
		int plIndex = (int) protectionLevel.get().charAt(0) - 49;	
		
		int[][] cuList = {
				{ 35,  29,  24,  21,  16},
				{ 52,  42,  35,  29,  24},
				{ -1,  52,  42,  35,  29},
				{ 70,  58,  48,  42,  32},
				{ 84,  70,  58,  52,  40},
				{104,  84,  70,  58,  48},
				{ -1, 104,  84,  70,  58},
				{140, 116,  96,  84,  64},
				{168, 140, 116, 104,  80},
				{208, 168, 140, 116,  96},
				{224, 208, 168, 140, 116},
				{280, 232, 192, 168, 128},
				{-1,  280,  -1, 208, 160},
				{416,  -1, 280,  -1, 192}
		};	
		cu.set(cuList[brIndex][plIndex]);	
	}
}

package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class USRP1 extends UHD {

	private StringProperty subdevice;
	private ObservableList<String> subdeviceList;
	
	
	public USRP1() {
		super("usrp1", 20.0, 31.0);
		
		subdeviceList = FXCollections.observableArrayList("A:0","A:1","B:0","B:1");
		subdevice = 	new SimpleStringProperty(subdeviceList.get(0));
	}


	public StringProperty getSubdevice() {
		return subdevice;
	}


	public ObservableList<String> getSubdeviceList() {
		return subdeviceList;
	}

}

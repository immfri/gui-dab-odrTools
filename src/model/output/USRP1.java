package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class USRP1 extends UHD {

	private StringProperty subdevice;
	
	
	public USRP1() {
		super("usrp1", 20.0, 31.0);
		
		master_clock_rate.setValue("");
		subdevice = new SimpleStringProperty("A:0");
		
	}


	public StringProperty getSubdevice() {
		return subdevice;
	}


}

package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class USRP2 extends UHD {

	private StringProperty master_clock_rate;
	
	
	public USRP2() {
		super("usrp2", 20.0, 31.0);
		
		rate.setValue("5000000");
		master_clock_rate = new SimpleStringProperty("32768000");
	}
	
	

	public StringProperty getMaster_clock_rate() {
		return master_clock_rate;
	}
}

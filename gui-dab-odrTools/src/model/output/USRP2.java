package model.output;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class USRP2 extends UHD {

	private IntegerProperty master_clock_rate;
	
	
	public USRP2() {
		super("usrp2", 20.0, 31.0);
		
		rate.set(5000000);
		master_clock_rate = new SimpleIntegerProperty(32768000);
	}
	
	

	public IntegerProperty getMaster_clock_rate() {
		return master_clock_rate;
	}
}

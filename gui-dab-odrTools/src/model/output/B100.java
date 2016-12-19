package model.output;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class B100 extends UHD {
	
	private IntegerProperty master_clock_rate;

	
	public B100() {
		super("b100", 2.0, 31.0);
		
		master_clock_rate = new SimpleIntegerProperty(32768000);
	}
	
	public IntegerProperty getMaster_clock_rate() {
		return master_clock_rate;
	}
}

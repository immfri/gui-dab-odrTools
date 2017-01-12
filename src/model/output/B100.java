package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class B100 extends UHD {
	
	private StringProperty master_clock_rate;

	
	public B100() {
		super("b100", 2.0, 31.0);
		
		master_clock_rate = new SimpleStringProperty("32768000");
	}
	
	public StringProperty getMaster_clock_rate() {
		return master_clock_rate;
	}
}

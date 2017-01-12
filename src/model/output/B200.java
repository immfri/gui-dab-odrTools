package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class B200 extends UHD {
	
	private StringProperty master_clock_rate;

	public B200() {
		super("b200", 40.0, 89.0);
		
		master_clock_rate = new SimpleStringProperty("32768000");
	}

	public StringProperty getMaster_clock_rate() {
		return master_clock_rate;
	}
}

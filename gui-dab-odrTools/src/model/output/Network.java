package model.output;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Network extends Output {

	protected StringProperty port, source;
	protected IntegerProperty ttl;
	protected BooleanProperty multicast;
	
	
	
	public Network(String name, String format) {
		super(name, format, "192.168.0.4");
		
		port = 				new SimpleStringProperty("7000");
		multicast = 		new SimpleBooleanProperty(false);
		source = 			new SimpleStringProperty("214.67.2.2");
		ttl = 				new SimpleIntegerProperty(1);
	}

	
	
	public StringProperty getDestinationPort() {
		return port;
	}

	public StringProperty getSource() {
		return source;
	}

	public IntegerProperty getTtl() {
		return ttl;
	}

	public BooleanProperty getMulticast() {
		return multicast;
	}
	

}

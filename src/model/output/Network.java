package model.output;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Network extends Output {

	protected StringProperty port, source, ttl;
	protected BooleanProperty multicast;
	
	
	
	public Network(String name, String format) {
		super(name, format, "10.10.10.10");
		
		port = 			new SimpleStringProperty("12000");
		
		source = 		new SimpleStringProperty("");
		ttl = 			new SimpleStringProperty("");
		
		multicast = 	new SimpleBooleanProperty(false);
		multicast.addListener(c -> changeMulticast());
	}

	


	public StringProperty getDestinationPort() {
		return port;
	}

	public StringProperty getSource() {
		return source;
	}

	public StringProperty getTtl() {
		return ttl;
	}

	public BooleanProperty getMulticast() {
		return multicast;
	}
	
	
	
	private void changeMulticast() {
		
		if (multicast.getValue()) {
			source.setValue("192.168.0.1");
			ttl.setValue("1");
		} 
		else {
			source.setValue("");
			ttl.setValue("");
		}
	}

}

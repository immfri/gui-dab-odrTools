package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class IQZeromq extends Modulator {

	
	private StringProperty listen, socket_type;
	private ObservableList<String> socketTypeList;
	
	
	public IQZeromq() {
		super("zmq");
		
		socketTypeList = FXCollections.observableArrayList("pub","rep");
		
		listen =		new SimpleStringProperty("54000");
		socket_type = 	new SimpleStringProperty(socketTypeList.get(0));	
	}


	
	
	public StringProperty getListen() {
		return listen;
	}

	public StringProperty getSocketType() {
		return socket_type;
	}

	public ObservableList<String> getSocketTypeList() {
		return socketTypeList;
	}
	
}

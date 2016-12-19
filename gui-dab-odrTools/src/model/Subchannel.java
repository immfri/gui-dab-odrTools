package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Subchannel extends Element {
	
	private StringProperty inputfile, protectionProfile, id;
	private StringProperty secretKey, publicKey, encoderKey;
	private IntegerProperty zmqBuffer, zmqPreBuffer;
	private BooleanProperty encryption;
	
	private Input input;

	public Subchannel(String name, Input input) {	
		super(name);
		
		this.input = 		input;
		inputfile = 		new SimpleStringProperty("");
		
		protectionProfile = new SimpleStringProperty("UEP");
		zmqBuffer = 		new SimpleIntegerProperty(40);
		zmqPreBuffer = 		new SimpleIntegerProperty(20);
		encryption = 		new SimpleBooleanProperty(false);	// to File covert false to 0 and true to 1
		secretKey = 		new SimpleStringProperty("");
		publicKey = 		new SimpleStringProperty("");
		encoderKey = 		new SimpleStringProperty("");
		
		id = 				new SimpleStringProperty("");
		
		inputfile.bind(input.getOutput());
	}
	
	public ObservableList<String> getTypeList() {
		return FXCollections.observableArrayList("audio","dabplus","data","packet","enhancedpacket");
	}
	
	public ObservableList<String> getTypeNameList() {
		return FXCollections.observableArrayList("MP2-Audio","AAC-Audio","Data","Packet","EPM - Enhanced Packet Mode");
	}


	public Input getInput() {
		return input;
	}
	
	public void setInput(String typeName) {
		
		switch (typeName) {
		
			case "MP2-Audio":
				input = new MP2();
				break;
				
			case "AAC-Audio":
				input = new AAC();
				break;
				
			case "Data":
				input = new Data();
				break;
				
			case "Packet":
				input = new Packet("packet"); 
				break;
				
			case "EPM - Enhanced Packet Mode":
				input = new Packet("enhancedpacket"); 
				break;
		}	
	}
	
	public StringProperty getInputfile() {
		return inputfile;
	}

	public StringProperty getProtectionProfile() {
		return protectionProfile;
	}

	public IntegerProperty getZmqBuffer() {
		return zmqBuffer;
	}

	public IntegerProperty getZmqPreBuffer() {
		return zmqPreBuffer;
	}

	public BooleanProperty getEncryption() {
		return encryption;
	}

	public StringProperty getSecretKey() {
		return secretKey;
	}

	public StringProperty getPublicKey() {
		return publicKey;
	}

	public StringProperty getEncoderKey() {
		return encoderKey;
	}

	public StringProperty getId() {
		return id;
	}
	
	
	
	
}

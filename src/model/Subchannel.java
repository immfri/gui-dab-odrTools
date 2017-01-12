package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Subchannel extends Element {
	
	private StringProperty type, inputfile, bitrate, id, protection_profile, protection, zmq_buffer, zmq_prebuffering;
	
	private BooleanProperty encryption;
	private StringProperty  secret_key, public_key, encoder_key;
	
	private Input input;
	
	

	public Subchannel(String name, Input input) {	
		super(name);
		
		// Input
		this.input = 		input;
		
		// Attribute
		type = 					new SimpleStringProperty();
		inputfile = 			new SimpleStringProperty();
		
		bitrate =				new SimpleStringProperty();
		id = 					new SimpleStringProperty("");
		protection_profile = 	input.getProtectionProfile();	
		protection = 			new SimpleStringProperty();	
		
		zmq_buffer = 			new SimpleStringProperty();
		zmq_prebuffering = 		new SimpleStringProperty();
		
		encryption = 		new SimpleBooleanProperty();
		secret_key = 		new SimpleStringProperty();
		public_key = 		new SimpleStringProperty();
		encoder_key = 		new SimpleStringProperty();
		
		setZmq();
		
		// Binding
		type.bind(input.getType());
		bitrate.bind(this.input.getBitrate());
		protection.bind(this.input.getProtectionLevel());
		
		// Inputfile
		inputfile.bindBidirectional(this.input.getOutput());
	}
	
	

	private void setZmq() {
		
		encryption.setValue(false);
		secret_key.setValue("");
		public_key.setValue("");
		encoder_key.setValue("");
		
		if (input.getType().getValue().contains("audio") || input.getType().getValue().contains("dabplus")) {	
			zmq_buffer.setValue("40"); 
			zmq_prebuffering.setValue("20");
		}
		else {
			zmq_buffer.setValue(""); 
			zmq_prebuffering.setValue("");
		}
	}



	public void setInput(String typeName) {
		
		// Unbind
		inputfile.unbindBidirectional(input.getOutput());
		bitrate.unbind();
		protection.unbind();
		
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
				input = new Packet(getTypeList().get(3)); 
				break;
				
			case "EPM - Enhanced Packet Mode":
				input = new Packet(getTypeList().get(4)); 
				break;
		}	
		setZmq();
		
		type.bind(input.getType());
		protection_profile = input.getProtectionProfile();
		
		// Binding
		inputfile.bindBidirectional(input.getOutput());
		bitrate.bind(this.input.getBitrate());
		protection.bind(this.input.getProtectionLevel());
	}
	
	public Input getInput() {
		return input;
	}
	
	public ObservableList<String> getTypeList() {
		return FXCollections.observableArrayList("audio","dabplus","data","packet","enhancedpacket");
	}
	
	public ObservableList<String> getTypeNameList() {
		return FXCollections.observableArrayList("MP2-Audio","AAC-Audio","Data","Packet","EPM - Enhanced Packet Mode");
	}

	public StringProperty getInputfile() {
		return inputfile;
	}
	
	public StringProperty getType() {
		return type;
	}

	public StringProperty getProtection() {
		return protection;
	}
	
	public StringProperty getProtectionProfile() {
		return protection_profile;
	}

	public StringProperty getZmqBuffer() {
		return zmq_buffer;
	}

	public StringProperty getZmqPreBuffer() {
		return zmq_prebuffering;
	}

	public BooleanProperty getEncryption() {
		return encryption;
	}

	public StringProperty getSecretKey() {
		return secret_key;
	}

	public StringProperty getPublicKey() {
		return public_key;
	}

	public StringProperty getEncoderKey() {
		return encoder_key;
	}

	public StringProperty getId() {
		return id;
	}
	

}

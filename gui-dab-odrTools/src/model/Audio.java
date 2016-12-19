package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public abstract class Audio extends Input {
	
	
	protected StringProperty encodingQuality, samplerate, channel;
	private BooleanProperty driftCompensation;	
	private ObservableList<String> channelList, samplerateList;


	private Pad pad;

	
	
	public Audio(String type) {	
		super(type);
		
		sourceList = 			FXCollections.observableArrayList("File","Stream","ALSA","JACK");
		samplerateList =		FXCollections.observableArrayList("32000","48000");
		channelList = 			FXCollections.observableArrayList("1","2");
		
		encodingQuality = 		new SimpleStringProperty("");	
		driftCompensation = 	new SimpleBooleanProperty(false);		
		channel =				new SimpleStringProperty(channelList.get(1));			// stereo
		samplerate = 			new SimpleStringProperty(samplerateList.get(1));		// 48 kHz
		
		source.set(sourceList.get(0));													// File
		
		pad = new Pad();
	}
	
	public ObservableList<String> getSamplerateList() {
		return samplerateList;
	}
	
	public StringProperty getSamplerate() {
		return samplerate;
	}
	
	public StringProperty getEncodingQuality() {
		return encodingQuality;
	}
	
	public StringProperty getChannel() {
		return channel;
	}
	
	public BooleanProperty getDriftCompensation() {
		return driftCompensation;
	}
	
	public Pad getPad() {
		return pad;
	}

	public ObservableList<String> getChannelList() {
		return channelList;
	}
	
	
}


package model.output;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HackRf extends IQFile {

	private StringProperty frequency, gain, bandwidth;
	private ObservableList<String> bandwidthList;
	
	
	public HackRf() {
		
		this.getFormat().setValue("s8");
		this.getRate().setValue("4096000");
		this.getFile().setValue("./ofdm.fifo");
		
		frequency = new SimpleStringProperty("223936000");			// Channel: 12A
		gain = 		new SimpleStringProperty("47");
		bandwidth = new SimpleStringProperty("1750000");			// BW: for DAB
		
		bandwidthList = getBandwidths();
	}

	public StringProperty getFrequency() {
		return frequency;
	}

	public StringProperty getGain() {
		return gain;
	}

	public StringProperty getBandwidth() {
		return bandwidth;
	}

	public ObservableList<String> getBandwidthList() {
		return bandwidthList;
	}
	
	private ObservableList<String> getBandwidths() {
		String[] list = {"175","250","350","550","600","700","800","900","1000","1200","1400","1500","2000","2400","2800"};
		
		for (String s: list) {
			s = s + "0000";
		}
		
		return FXCollections.observableArrayList(list);
	}
}

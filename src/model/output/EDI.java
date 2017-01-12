package model.output;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class EDI extends Network {

	private StringProperty sourceport, fec, interleave, chunk_len, tagpacket_alignment;
	private BooleanProperty enable_pft, dump, verbose;
	
	
	
	public EDI(String name) {
		super(name, "edi");
		
		sourceport = 			new SimpleStringProperty("13000");
		fec = 					new SimpleStringProperty("2");
		interleave = 			new SimpleStringProperty("0");
		chunk_len = 			new SimpleStringProperty("207");
		tagpacket_alignment = 	new SimpleStringProperty("8");
		
		enable_pft = 			new SimpleBooleanProperty(true);
		dump = 					new SimpleBooleanProperty(false);
		verbose = 				new SimpleBooleanProperty(false);
	}



	public StringProperty getSourceport() {
		return sourceport;
	}

	public StringProperty getFec() {
		return fec;
	}
	
	public StringProperty getInterleave() {
		return interleave;
	}

	public StringProperty getChunk_len() {
		return chunk_len;
	}

	public StringProperty getTagpacket_alignment() {
		return tagpacket_alignment;
	}

	public BooleanProperty getEnable_pft() {
		return enable_pft;
	}

	public BooleanProperty getDump() {
		return dump;
	}

	public BooleanProperty getVerbose() {
		return verbose;
	}
}

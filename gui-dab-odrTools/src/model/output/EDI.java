package model.output;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class EDI extends Network {

	private StringProperty sourceport;
	private IntegerProperty fec, chunk_len, tagpacket_alignment;
	private BooleanProperty enable_pft, dump, verbose;
	
	
	
	public EDI(String name) {
		super(name, "edi");
		
		sourceport = 			new SimpleStringProperty("13000");
		
		fec = 					new SimpleIntegerProperty(2);
		chunk_len = 			new SimpleIntegerProperty(207);
		tagpacket_alignment = 	new SimpleIntegerProperty(8);
		
		enable_pft = 			new SimpleBooleanProperty(true);
		dump = 					new SimpleBooleanProperty(false);
		verbose = 				new SimpleBooleanProperty(false);
	}



	public StringProperty getSourceport() {
		return sourceport;
	}

	public IntegerProperty getFec() {
		return fec;
	}

	public IntegerProperty getChunk_len() {
		return chunk_len;
	}

	public IntegerProperty getTagpacket_alignment() {
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

package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.*;


public class LoaderForEncoders {

	// Encoders Name
	private final String audioEnc = 	"odr-audioenc";
	private final String padEnc = 		"odr-padenc";

	private BufferedReader reader;
	private String line;
	
	public LoaderForEncoders(File bashFile) throws IOException {
		
		reader = new BufferedReader(new FileReader(bashFile));
		line = reader.readLine(); 
		
		// Find Encoder
		while (line != null) {
			
			// Load Audio-Encoder
			if (line.contains(audioEnc)) loadAudio(bashFile);
			
			line = reader.readLine(); 
		}
		
		reader.close();
	}

	private void loadAudio(File bashFile) throws IOException {
		Subchannel subch = null;
		
		// Find Subchannel
		for (Subchannel sub: Multiplex.getInstance().getSubchannelList()) {
			
			// Only Audio
			if (sub.getType().getValue().contentEquals("audio") || sub.getType().getValue().contentEquals("dabplus")) {	
				
				String output = line.substring(line.indexOf(" -o ") + 3).replaceAll("\\s","");
				output = output.replace("localhost", "*");
				
				// Check Encoder-Output
				if (sub.getInputfile().getValue().contentEquals(output)) {
					subch = sub;
					break;
				}
			}
		}
		
		ArrayList<String> strings = new ArrayList<>(Arrays.asList(line.split(" ")));
		for (String s: strings) {
			if (s.contains(" ")) s.replaceAll("\\s","");	// Remove Blank Spaces
		}
		
		// Set up Subchannel
		if (subch != null && !line.contains(" --")) {
			Audio audio = (Audio) subch.getInput();
			
			// Input + Path
			if (line.contains(" -v ")) {	
				audio.getSource().setValue(audio.getSourceList().get(0));	// Webstream	 
				audio.getPath().setValue(strings.get(strings.indexOf("-v") + 1));
			} 
			else if (line.contains(" -d ")) { 
				audio.getSource().setValue(audio.getSourceList().get(1)); 	// ALSA
				audio.getPath().setValue(strings.get(strings.indexOf("-d") + 1));
			}
			else if (line.contains(" -j ")) {	
				audio.getSource().setValue(audio.getSourceList().get(2));	// JACK
				audio.getPath().setValue(strings.get(strings.indexOf("-j") + 1));
			}
			
			// Drift
			if (line.contains(" -D ")) audio.getDriftCompensation().setValue(true);
				
			// Channel
			if (line.contains(" -c ")) {
				String channels = strings.get(strings.indexOf("-c") + 1);
				
				if (audio.getChannelList().contains(channels)) audio.getChannel().setValue(channels);
			}
			
			// Samplerate
			if (line.contains(" -r ")) {
				String rate = strings.get(strings.indexOf("-r") + 1);
				
				if (audio.getSamplerateList().contains(rate)) {
					audio.getSamplerate().setValue(rate);
				}
			}
			
			// Pad
			if (line.contains(" -P ") && line.contains(" -p ")) {
				loadPad(bashFile, audio.getPad(), strings.get(strings.indexOf("-P") + 1), strings.get(strings.indexOf("-p") + 1));
			}
			
			// Bitrate & DAB/DAB+ & Secret File -> MuxFileLoader -> loadToMux(...)
		}
		
		// Warnings
		else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Load Warning");
			
			if (line.contains(" --")) {
				alert.setHeaderText("Syntax with -- in " + audioEnc + " not supported!");
			}
			else {
				alert.setHeaderText("Suchannel for " + audioEnc + " not found!");
			}
			
		}
	}

	private void loadPad(File bashFile, Pad pad, String padFileName, String padSize) throws IOException {
		BufferedReader padReader = new BufferedReader(new FileReader(bashFile));
		String padLine = padReader.readLine(); 
		
		
		// Find Pad-Encoder
		while (padLine != null) {
			
			// Load Pad-Encoder
			if (padLine.contains(padEnc) && padLine.contains(padFileName) && padLine.contains(padSize)) {	
				pad.getFilePad().setValue(padFileName);
				pad.getLength().setValue(padSize);
				
				// List with Entries from PAD
				ArrayList<String> strings = new ArrayList<>(Arrays.asList(padLine.split(" ")));
				for (String s: strings) {
					if (s.contains(" ")) s.replaceAll("\\s","");	// Remove Blank Spaces
				}
				
				// DLS
				if (padLine.contains(" -t ")) {
					pad.getDlsEnabled().setValue(true);
					pad.getFileDls().setValue(strings.get(strings.indexOf("-t") + 1));
					
					if (padLine.contains(" -C ")) pad.getRawDls().setValue(true);
					if (padLine.contains(" -r ")) pad.getRemoveDls().setValue(true);
					
					// Charset
					if (padLine.contains(" -c ")) {
						
						switch (strings.get(strings.indexOf("-c") + 1)) {	
						case "15": 	pad.getCharset().setValue(pad.getCharsetList().get(0));		break;	// UTF-8
						case "0": 	pad.getCharset().setValue(pad.getCharsetList().get(1));		break;	// EBU-Latin
						case "6": 	pad.getCharset().setValue(pad.getCharsetList().get(2));				// UCS-BE
						}
					}	
				}
				
				// SLS
				if (padLine.contains(" -d ")) {
					pad.getSlsEnabled().setValue(true);
					pad.getDirSlides().setValue(strings.get(strings.indexOf("-d") + 1));
					
					if (padLine.contains(" -s ")) pad.getDelay().setValue(strings.get(strings.indexOf("-s") + 1));
					if (padLine.contains(" -e "))	pad.getEraseSlides().setValue(true);
					if (padLine.contains(" -R ")) pad.getRawSlides().setValue(true);
				}
				
				break;
			}	
			padLine = padReader.readLine(); 
		}
		
		padReader.close();
	}

}

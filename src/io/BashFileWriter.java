package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import model.*;
import model.output.ETIZeromq;
import model.output.Output;


public class BashFileWriter extends FileWriter {
	
	// Tools Name
	String audioEnc = 	"odr-audioenc";
	String padEnc = 	"odr-padenc";
	String dabmux =	 	"odr-dabmux";
	String dabmod = 	"odr-dabmod";
	

	public BashFileWriter(File file, Multiplex mux) throws IOException {
		super(file);
		
		// bash file init.
		this.write("#!/bin/bash\n\n");
		this.write("killall screen\n");						// Kill all screens subprocesses
		this.write("killall -9 screen\n");						// Kill all screens subprocesses
		this.write("find . -maxdepth 1 -type p -delete\n");				// Remove all FIFOs

		//-------------- FIFOs --------------------------------
		for (Subchannel subch: mux.getSubchannelList()) {
			String type = subch.getType().getValue();

			if (type.contains("audio") || type.contains("dabplus")) {
				Audio audio = (Audio) subch.getInput();

				if (audio.getPad().getDlsEnabled().getValue() || audio.getPad().getSlsEnabled().getValue()) {
					
					// write Line in Bash File
					this.write("mkfifo pad-" + subch.getName().getValue() + ".fifo\n");
				}
			}
		}
		
		for (Output out: mux.getOutputList()) {
			if (out.getFormat().getValue().contentEquals("fifo")) {
				this.write("mkfifo " + out.getDestination().getValue() + "\n");
				this.write("screen -dm -S dablin dablin_gtk " +out.getDestination().getValue() +"\n");
			}
		}
		this.write("sleep 2\n\n");
		
		//-------------- PAD-Encoders --------------------------------
		for (Subchannel subch: mux.getSubchannelList()) {
			String type = subch.getType().getValue();

			if (type.contains("audio") || type.contains("dabplus")) {
				Audio audio = (Audio) subch.getInput();

				if (audio.getPad().getDlsEnabled().getValue() || audio.getPad().getSlsEnabled().getValue()) {
					
					// write Line in Bash File
					writePadEncoder(subch, "pad-" + subch.getName().getValue() + ".fifo");
				}
			}
		}

		//------------- Audio-Encoders -------------------------------
		for (Subchannel subch: mux.getSubchannelList()) {
			String type = subch.getType().getValue();
			
			if (type.contains("audio") || type.contains("dabplus")) {
				writeAudioEncoder(subch);
			}
		}
		this.write("\nsleep 2\n\n");
		
		//----------------- DabMux -------------------------------------
		this.write("\nscreen -dm -S dabmux "+dabmux+ " -e dab.mux");
		
		
		//----------------- DabMods ------------------------------------
		int i = 0;
		for (Output out: mux.getOutputList()) {
			
			if (out.getFormat().getValue().contains("zmq")) {
				
				// need Modulator Conf.
				if (((ETIZeromq)out).getMod() != null) {		
					
					// add new Line at first "dabmod"
					if (i++ == 0) this.write("\n");
					
					String fileName = out.getName().getValue();
					this.write("\nsudo nice -n 5 screen -dm -S " +fileName+ " " +dabmod+ " -C " +fileName+ ".ini");
				}
			}
		}
	}

	
	
	private void writeAudioEncoder(Subchannel subch) throws IOException {
		
		// Audio
		Audio audio = (Audio) subch.getInput();
		
		//------- start ----------------
		String screenName = "audioenc-" + subch.getName().getValue();
		this.write("screen -dm -S " + screenName + " " + audioEnc + " ");
	
		// Input
		String source = audio.getSource().getValue();
		if (source.contains(audio.getSourceList().get(0))) {	// Webstream
			this.write("-v ");
		} 
		else if (source.contains(audio.getSourceList().get(1))) {	// Alsa
			this.write("-d ");
		} 
		else {														// Jack
			this.write("-j ");
		}
		this.write(audio.getPath().getValue() + " ");
		
		// Drift
		if (!audio.getSource().getValue().contains("JACK") && audio.getDriftCompensation().getValue() == true) {
			this.write("-D ");
		}
		
		// Bitrate, Channel, Samplerate
		this.write("-b " + audio.getBitrate().getValue() + " ");
		this.write("-c " + audio.getChannel().getValue() + " ");
		this.write("-r " + audio.getSamplerate().getValue() + " ");
		
		// Audiocodec (for DAB+ nothing)
		if (audio.getType().getValue().contains("audio")) {
			this.write("-a ");
		}
		
		// Secret File
		if (subch.getEncryption().getValue() && !subch.getEncoderKey().getValue().isEmpty()) {
			this.write("-k " + subch.getEncoderKey().getValue() + " ");
		}
		
		// Pad
		if (audio.getPad().getDlsEnabled().getValue() || audio.getPad().getSlsEnabled().getValue()) {	
			this.write("-P " + audio.getPad().getFilePad().getValue() + " ");
			this.write("-p " + audio.getPad().getLength().getValue() + " ");
		}

		// Output
		String ip = audio.getOutput().getValue().replace("*", "localhost");
		this.write("-o " + ip + " ");
		
		// ------- End with Logfile --------
		this.write("\n");
	}
	
	

	private void writePadEncoder(Subchannel subch, String fileName) throws IOException {
		
		Pad pad = ((Audio)subch.getInput()).getPad();
		
		//------- start ----------------
		String screenName = "padenc-" + subch.getName().getValue();
		this.write("screen -dm -S " + screenName + " " + padEnc + " ");

		// DLS Text
		if (!pad.getFileDls().getValue().isEmpty() && pad.getDlsEnabled().getValue()) {
			this.write("-t " + pad.getFileDls().getValue() +" ");
			
			
			if (pad.getRawDls().getValue()) 	this.write("-C ");
			if (pad.getRemoveDls().getValue()) 	this.write("-r ");
			
			// Charset
			String charset = pad.getCharset().getValue();
			if (charset.contains(pad.getCharsetList().get(0))) {		// UTF-8
				this.write("-c 15 ");
			}
			else if (charset.contains(pad.getCharsetList().get(1))) {	// EBU Latin
				this.write("-c 0 ");
			} 
			else {														// UCS-2 BE
				this.write("-c 6 ");
			}
		}
		
		// Slideshow	
		if (!pad.getDirSlides().getValue().isEmpty() && pad.getSlsEnabled().getValue()) {
			this.write("-d " + pad.getDirSlides().getValue() +" ");
			this.write("-s " + pad.getDelay().getValue() +" ");
			
			if (pad.getEraseSlides().getValue()) 	this.write("-e ");
			if (pad.getRawSlides().getValue()) 		this.write("-R ");
		}
		
		// PAD
		pad.getFilePad().setValue(fileName);
			
		this.write("-p " + pad.getLength().getValue() +" ");
		this.write("-o " + pad.getFilePad().getValue());
		

		// ------- End with LogFile --------
		this.write("\n");
	}

	
}

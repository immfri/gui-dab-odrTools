package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class Load {
	
	// Tools Name
	String audioEnc = 	"odr-audioenc";
	String padEnc = 	"odr-padenc";
	String dabmux =	 	"odr-dabmux";
	String dabmod = 	"odr-dabmod";
	

	public Load(File loadFolder, File bashFile) throws IOException {
		
		//------------ load Multiplex from DabMux ------------------
		File muxFile = getDabMuxFile(bashFile);
		
		if (muxFile != null) {			
			new MuxFileLoader(muxFile, bashFile);

			//------------ load advanced Pad/Audio Parameters ------------------
			new LoaderForEncoders(bashFile);
			
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("File for DabMux not found or invalid");
			alert.setHeaderText("Rename or create a valid DabMux-Configuration-File");
		}
	}



	private File getDabMuxFile(File bashFile) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(bashFile));
		String line;
		do {
			line = reader.readLine();
			if (line.contains(dabmux)) {
				String fileName = line.substring(line.indexOf("-e") + 3);
				
				// fileName invalid
				if (fileName.contains(" ")) {
					break;
				}
				
				reader.close();
				return new File(bashFile.getParentFile() +"/"+ fileName);
			}		
		} while (line != null);	
		reader.close();	
		
		return null;
	}
}
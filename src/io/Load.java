package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
				
			new MuxFileLoader(muxFile);

			//------------ load advanced Pad/Audio Parameters ------------------
			new LoaderForEncoders(bashFile);
			
			//------------ load advanced Output Parameters from DabMod ------------------
			
			
			ArrayList<File> modFiles = getDabModFiles(bashFile);
			if (modFiles != null) {
				
				for (File modFile: modFiles) {
					new ModFileLoader(modFile);
				}
			} 
			else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Files for DabMod not found or invalid");
				alert.setHeaderText("Rename or create valide DabMod-Configuration-Files");
			}
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("File for DabMux not found or invalid");
			alert.setHeaderText("Rename or create a valid DabMux-Configuration-File");
		}
	}


	private ArrayList<File> getDabModFiles(File bashFile) throws IOException {
		
		ArrayList<File> modFiles = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(bashFile));
		
		String line = reader.readLine();
		while (line != null) {
			
			if (line.contains(dabmod)) {
				String fileName = line.substring(line.indexOf("-C") + 3);

				// fileName is valide
				if (!fileName.contains(" ")) {
					modFiles.add(new File(bashFile.getParentFile() +"/"+ fileName));
				}
			}
			line = reader.readLine();
		}
		reader.close();	
		
		return modFiles;
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
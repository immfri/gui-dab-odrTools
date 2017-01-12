package io;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import model.Multiplex;
import model.output.*;


public class Save {	
	
	public Save(File saveFolder) throws IOException {
		
		if (saveFolder.listFiles().length > 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Overwrite");
			alert.setHeaderText("Files will be overwrite");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				
				// all files inside folder delete 
				for (File file: saveFolder.listFiles()) {
				    file.delete();
				}
				startSaving(saveFolder);
			} 
		}
		else {
			startSaving(saveFolder);
		}
	}	
		
	
	private void startSaving(File saveFolder) throws IOException {
		
		// Mux File write
		File muxFile = new File(saveFolder.getAbsolutePath() +"/dab.mux");

		MuxFileWriter writer = new MuxFileWriter(muxFile, "general", Multiplex.getInstance(), 0,6);
		writer.writeSection("remotecontrol", Multiplex.getInstance(), 6,7);
		writer.writeSection("ensemble", Multiplex.getInstance().getEnsemble(), 0,6);
		writer.writeSectionList("services", Multiplex.getInstance().getServiceList(), 0,6);
		writer.writeSectionList("subchannels", Multiplex.getInstance().getSubchannelList(), 0,13);
		writer.writeSectionList("components", Multiplex.getInstance().getComponentList(), 0,8);
		writer.writeOutputList(Multiplex.getInstance().getOutputList());
		writer.close();
				
		
		// Mod Files written
		for (Output out: Multiplex.getInstance().getOutputList()) {
			
			if (out.getFormat().getValue().contains("zmq")) {
				
				// need Modulator Conf.?
				if (((ETIZeromq)out).getMod() != null) {
					
					File modFile = new File(saveFolder.getAbsolutePath()+"/"+out.getName().getValue()+".ini");
					Modulator modulator = ((ETIZeromq)out).getMod();
					ModFileWriter modFw = new ModFileWriter(modFile, modulator);
					modFw.close();
				}
			}
		}

		
		// Bash File
		File bashFile = new File(saveFolder.getAbsolutePath() +"/dab.sh");
		BashFileWriter bashFw = new BashFileWriter(bashFile, Multiplex.getInstance());
		bashFw.close();
	}

}

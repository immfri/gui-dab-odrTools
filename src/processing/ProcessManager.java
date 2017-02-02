package processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import model.Multiplex;


public class ProcessManager {
	
	// Tools Name
	private final String audioEnc = "odr-audioenc";
	private final String padEnc = 	"odr-padenc";
	private final String dabMux =	"odr-dabmux";
	private final String dabMod = 	"odr-dabmod";
	
	private Button runningButton;
	private ArrayList<Process> processList;
	
	
	public ProcessManager(Button runningButton) {
		
		this.runningButton = runningButton;
		this.processList = new ArrayList<>();
	}
	
	
	// Start all Processes from Project
	public void start() {
		File projectFolder = Multiplex.getInstance().getProjectFolder();
		
		try {
			
			// Kill old processes
			Runtime.getRuntime().exec("killall -9 " + padEnc);
			Runtime.getRuntime().exec("killall -9 " + audioEnc);
			Runtime.getRuntime().exec("killall -9 " + dabMux);
			Runtime.getRuntime().exec("killall -9 " + dabMod);	
			
			for (String command: getAllCommands()) {
				
				// start Process
				Process process = null;
				try {
					process = Runtime.getRuntime().exec(command, null, projectFolder);
				} 
				catch (IOException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Process failed");
					alert.setHeaderText("Process with command <" +command+ "> can not started!");
					alert.show();
				}
				
				if (!command.contains("mkfifo")) {
					
					// Add Process to Lists
					processList.add(process);
					
					// start Process Listener
					new ProcessEndNotifier(this, process, command);
				}
			}
		} 
		catch (IOException e) {
			System.out.println("IOException in ProcessManager.start()");
		}
	}
	
	
	// Stop all executable Processes
	public void stop() {	
		
		// Change Running Button
		Platform.runLater(() -> runningButton.setText("Start"));
		
		
		// Kill all processes
		for (Process p: processList) {
			if (p.isAlive()) p.destroy();
		}
		
		// Remove FIFO-Files
		for (File file: Multiplex.getInstance().getProjectFolder().listFiles()) {
			if (file.getName().contains(".fifo")) file.delete();
		}
		
		// Clear
		processList.clear();
		System.gc();
	}

	
	// -> ProcessEndNotifier
	public void cancel(Process process, String processCmd) {
		
		if (runningButton.getText().contentEquals("Stop")) {
			
			// if all process dead -> stop running
			if (!getOneProcessAlive()) stop();
						
			Platform.runLater(() -> {
	
				// Running Error
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Running - Error");
						
				if (runningButton.getText().contentEquals("Start")) {
					alert.setAlertType(AlertType.INFORMATION);
					alert.setTitle("Running - Finish");
				}
				
				// Send E-Mail
				if (!Multiplex.getInstance().getEMail().getValue().isEmpty()){
					sendMail(alert.getTitle(), processCmd);
				}
				
				alert.setHeaderText("Process terminated");
				alert.setContentText("Command: " + processCmd);	
				alert.setResizable(true);	
				alert.show();
			});	
		}
	}


	private boolean getOneProcessAlive() {
		
		for (Process p: processList) {
			
			if (p.isAlive()) return true;
		}
		return false;
	}
	
	
	private void sendMail(String subject, String processCmd) {
		String cmd = "mail -s \"" +subject+ "\" " +Multiplex.getInstance().getEMail().getValue()+ " <<< \"Command: " +processCmd+ "\"";
		
		try {
			new ProcessBuilder(new String[]{"bash", "-c", cmd}).start();
		} 
		catch (IOException e) {
			System.out.println("ProcessManager.sendMail() - IOException");
		}
		System.out.println(cmd);
	}


	private ArrayList<String> getAllCommands() throws IOException {
		
		File projectFolder = Multiplex.getInstance().getProjectFolder();
		ArrayList<String> commandList = new ArrayList<>();
		
		if (projectFolder != null) {
			if (projectFolder.exists()) {
				
				// Search valid Bash File
				for (File bashFile: projectFolder.listFiles()) {	
					
					// Bash File is valid
					if (bashFile.getName().length() > 3 && bashFile.getName().indexOf(".sh") == (bashFile.getName().length()-3)) {
						
						BufferedReader reader = null;
						try {
							reader = new BufferedReader(new FileReader(bashFile));
						} catch (FileNotFoundException e) {
							System.out.println("FileNotFoundException in ProcessManager.getAllCommands()");
						}
						
						String line = reader.readLine();
						
						while (line != null) {
							
							// correct Line found
							if (line.contains("mkfifo")) {
								commandList.add(line.substring(line.indexOf("mkfifo")));
							}
							else if (line.contains("dablin_gtk")) {
								commandList.add(line.substring(line.indexOf("dablin_gtk")));
							}
							else if (line.contains(padEnc)) {
								commandList.add(line.substring(line.indexOf(padEnc)));
							}
							else if (line.contains(audioEnc)) {
								commandList.add(line.substring(line.indexOf(audioEnc)));
							}
							else if (line.contains(dabMux)) {
								commandList.add(line.substring(line.indexOf(dabMux)));
							}
							else if (line.contains(dabMod)) {
								commandList.add(line.substring(line.indexOf(dabMod)));
							}
							line = reader.readLine();
						}
						
						reader.close();
						break;
					}
				}
			}
			else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Path to Project File is wrong!");
				alert.show();
			}
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Project File not create!");
			alert.show();
		}
		
		return commandList;
	}

	
}

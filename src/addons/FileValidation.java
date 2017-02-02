package addons;

import java.io.File;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import model.Multiplex;


public class FileValidation extends Validation {

	
	public FileValidation(TextField textField, StringProperty fileName, boolean isFileExist) {

		// Binding
		textField.textProperty().unbindBidirectional(fileName);
		textField.textProperty().bindBidirectional(fileName);

		// Listener
		textField.textProperty().addListener(c -> checkFile(textField, isFileExist));
		
		checkFile(textField, isFileExist);
	}
	
	
	private void checkFile(TextField textField, boolean isFileExist) {
		textField.setStyle(nok);

		if (!textField.getText().isEmpty()) {
			
			if (!textField.getPromptText().contains("Data")) {
				
				// Relative Path
				if (textField.getText().indexOf(".") == 0) {
					File projectFolder = Multiplex.getInstance().getProjectFolder();
					
					if (projectFolder!= null) {
						String absolutePath = projectFolder.getAbsolutePath() + textField.getText().substring(1);
						File checkFile = new File(absolutePath);
						
						if (checkFile != null) {
							if (checkFile.getParentFile().isDirectory() && !isFileExist)	textField.setStyle(ok);	// File isn't now exist
							else if (checkFile.exists()) 				textField.setStyle(ok);	// File exist
						}
					}
				}
				
				// Absolute Path
				else {
					
					File checkFile = new File(textField.getText());	
					if (checkFile != null) {
						
						if (checkFile.getParentFile().isDirectory() && !isFileExist) 	textField.setStyle(ok);		// File isn't now exist
						else if (checkFile.exists()) 					textField.setStyle(ok);		// File exist
					}
				}	
			}		
			
			// only for DataVBox -> Data Subchannel -> Input-File: PRBS
			else if (textField.getText().contains("://")) {
				textField.setStyle(ok);
			}
		}
	}
}

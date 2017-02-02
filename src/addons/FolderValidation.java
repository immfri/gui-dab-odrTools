package addons;

import java.io.File;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import model.Multiplex;


public class FolderValidation extends Validation {

	
	public FolderValidation(TextField textField, StringProperty folderName) {

		// Binding
		textField.textProperty().unbindBidirectional(folderName);;
		textField.textProperty().bindBidirectional(folderName);

		// Listener
		textField.textProperty().removeListener(c -> checkFolder(textField));
		textField.textProperty().addListener(c -> checkFolder(textField));
		
		checkFolder(textField);
	}
	
	
	private void checkFolder(TextField textField) {
		textField.setStyle(nok);

		if (!textField.getText().isEmpty()) {
			
			// Relative Path
			if (textField.getText().indexOf(".") == 0) {
				File projectFolder = Multiplex.getInstance().getProjectFolder();
				
				if (projectFolder!= null) {
					String absolutePath = projectFolder.getAbsolutePath() + textField.getText().substring(1);
					File checkFolder = new File(absolutePath);
					
					if (checkFolder != null) {
						if (checkFolder.isDirectory() && checkFolder.exists())	textField.setStyle(ok);
					}
				}
			}
			
			// Absolute Path
			else {
				File checkFolder = new File(textField.getText());
			
				if (checkFolder.isDirectory() && checkFolder.exists()) textField.setStyle(ok);
			}
		}
	}
}

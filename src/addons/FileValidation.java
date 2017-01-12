package addons;

import java.io.File;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;


public class FileValidation extends Validation {

	
	public FileValidation(TextField textField, StringProperty fileName) {

		// Binding
		textField.textProperty().unbindBidirectional(fileName);
		textField.textProperty().bindBidirectional(fileName);

		// Listener
		textField.textProperty().addListener(c -> checkFile(textField));
		
		checkFile(textField);
	}
	
	
	private void checkFile(TextField textField) {
		textField.setStyle(nok);

		if (!textField.getText().isEmpty()) {
			File file = new File(textField.getText());
			
			if (file != null && !textField.getPromptText().contains("Data")) {
				if (!file.isDirectory() && file.getParentFile().isDirectory()) textField.setStyle(ok);	// File isn't now exist
				else if (file.isFile()) textField.setStyle(ok);											// File exist
			}		
			
			// only for DataVBox -> Data Subchannel -> Input-File: PRBS
			else if (textField.getText().contains("://")) {
				textField.setStyle(ok);
			}
		}
	}
}

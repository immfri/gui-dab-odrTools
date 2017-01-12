package addons;

import java.io.File;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;


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
			File folder = new File(textField.getText());
			
			if (folder.isDirectory()) textField.setStyle(ok);
		}
	}
}

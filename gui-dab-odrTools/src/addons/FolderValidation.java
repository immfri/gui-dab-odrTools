package addons;

import java.io.File;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;


public class FolderValidation extends Validation {

	
	public FolderValidation(TextField textField, StringProperty fileName) {

		// Binding
		textField.textProperty().unbind();
		textField.textProperty().bindBidirectional(fileName);

		// Listener
		textField.textProperty().addListener(c -> checkFolder(textField));
		
		checkFolder(textField);
	}
	
	
	private void checkFolder(TextField textField) {
		textField.setStyle(nok);

		if (!textField.getText().isEmpty()) {
			File file = new File(textField.getText());
			
			if (file.getParentFile().exists()) {
				if (!file.isDirectory()) textField.setStyle(ok);
			}
		}
	}
}

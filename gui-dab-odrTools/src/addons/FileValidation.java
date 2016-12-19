package addons;

import java.io.File;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;


public class FileValidation extends Validation {

	
	public FileValidation(TextField textField, StringProperty fileName) {

		// Binding
		textField.textProperty().unbind();
		textField.textProperty().bindBidirectional(fileName);

		// Listener
		textField.textProperty().addListener(c -> checkFile(textField));
		
		checkFile(textField);
	}
	
	
	private void checkFile(TextField textField) {
		textField.setStyle(nok);

		if (!textField.getText().isEmpty()) {
			File file = new File(textField.getText());
			
			if (file.exists()) textField.setStyle(ok);
			
			// only for DataVBox -> Data Subchannel -> Input-File: PRBS
			else if (textField.getPromptText().contains("Data") && textField.getText().contains("prbs://")) textField.setStyle(ok);
		}
	}
}

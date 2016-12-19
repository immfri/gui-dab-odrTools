package addons;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;


public class UrlValidation extends Validation {

	private final String urlRegEx = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	
	public UrlValidation(TextField textField, StringProperty url) {
		
		//Binding
		textField.textProperty().unbind();
		textField.textProperty().bindBidirectional(url);
		
		// Listener
		textField.textProperty().addListener(c -> checkUrl(textField));
		checkUrl(textField);
	}
	
	private void checkUrl(TextField textField) {
		textField.setStyle(nok);
		
		if (textField.getText() != null) {
			if (textField.getText().matches(urlRegEx)) textField.setStyle(ok);
		}
	}

}

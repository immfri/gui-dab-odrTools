package addons;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;


public class  IpValidation extends Validation {
	
	private final String ipRegEx = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
	
	public IpValidation(TextField textField, StringProperty value) {
		
		// Text Formatter
		textField.setTextFormatter(new TextFormatter<> (c -> {  

			if (c.getControlNewText().isEmpty()) c.setText("10.10.10.10");
			if (c.getControlNewText().matches("\\p{Alpha}")) return null;
			
			return c;
		}));

		// Binding
		textField.textProperty().unbindBidirectional(value);
		textField.textProperty().bindBidirectional(value);
		
		// Listener
		textField.textProperty().addListener(c -> checkIp(textField));
		checkIp(textField);
	}
		
	private void checkIp(TextField textField) {
		textField.setStyle(nok);
		
		if ((textField.getText().contentEquals("*") && textField.isDisable()) || textField.getText().matches(ipRegEx)) {
			textField.setStyle(ok);
		}
	}

}

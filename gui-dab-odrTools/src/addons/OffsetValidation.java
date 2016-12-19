package addons;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;


public class OffsetValidation extends Validation {
	

	public OffsetValidation(TextField textField, StringProperty value, double min, double max) {
		
		// Text Formatter
		textField.setTextFormatter(new TextFormatter<> (c -> {  

			if (c.getControlNewText().isEmpty()) {  
				c.setText("0.0");
			}
			
			checkOffset(textField, min, max);
			
			return c;
		}));

		// Binding
		textField.textProperty().bindBidirectional(value);
		checkOffset(textField, min, max);
	}
		
	private void checkOffset(TextField textField, double min, double max) {
		textField.setStyle(nok);
		
		if (textField.getText().matches("\\d+\\.\\d+")) {
			double offset = Double.parseDouble(textField.getText());
			
			if (offset>min || offset<max) textField.setStyle(ok);
		}
		
		
	}

}

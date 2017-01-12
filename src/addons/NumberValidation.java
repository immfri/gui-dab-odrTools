package addons;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;


public class NumberValidation extends Validation {

	
	public NumberValidation(TextField textField, StringProperty number, int minNumber, int maxNumber, int step, Label label) {

		// Formatter
		textField.setTextFormatter(getNumberFormatter(minNumber, maxNumber, step, label));
		
		// Binding
		textField.textProperty().unbindBidirectional(number);
		textField.textProperty().bindBidirectional(number);
	}




	private TextFormatter<String> getNumberFormatter(int min, int max, int step, Label label) {
		return new TextFormatter<> (c -> {
			
			// Spinner Text not empty
			if (c.getControlNewText().isEmpty()) {
				c.setText("0");   
			}
			
			// only decimal chars
			if (c.getControlNewText().matches("\\d*")) {
				
				// not more chars, if maxNumber need
				if (c.getControlNewText().length() > (""+max).length() || c.getControlNewText().length() > 9) {
					return null;
				}
				
				// only numbers, not "0123" , "0..."
				if (c.getControlNewText().length() > 1 && c.getControlNewText().charAt(0) == '0') {
					return null;
				}
				
				int number = Integer.parseInt(c.getControlNewText());
				
				// only number inside range
				if (number > max) {
					return null;
				}
				
				// only number inside steps
				if ((number-min)%step != 0) {
					return null;
				}
				
				// Only for ensemble
				if (label != null) {
					label.setVisible(number == 0);
				}
				
				return c;
			}
			return null;
		});
	}
	
	
}

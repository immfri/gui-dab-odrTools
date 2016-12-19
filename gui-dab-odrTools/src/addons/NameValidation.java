package addons;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import model.*;


public class NameValidation extends Validation {
	
	
	public NameValidation(TextField textField, StringProperty name, ObservableList<? extends Element> list) {
		
		// Text Formatter
		textField.setTextFormatter(new TextFormatter<String>(c -> {
			if (c.getControlNewText().length() > 16) return null;
			
			checkName(textField, list);	
			return c;
		}));
		
		// Binding
		textField.textProperty().bindBidirectional(name);
		
		checkName(textField, list);
	}

	
	
	private void checkName(TextField textField, ObservableList<? extends Element> list) {
		textField.setStyle(nok);

		if (textField.getText().length() > 0) {
			int count = 0;
			for (Object obj: list) {

				if (((Element)obj).getName().getValue().contains(textField.getText())) count++;
				if (count > 1) break;
			}

			if (count < 2) {
				textField.setStyle(ok);
			}	
		}
	}
}

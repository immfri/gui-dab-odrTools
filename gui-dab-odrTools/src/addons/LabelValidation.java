package addons;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import model.*;


public class LabelValidation extends Validation {
	
	public LabelValidation(TextField textField, StringProperty label, ObservableList<Service> list) {
		
		// Text Formatter
		textField.setTextFormatter(new TextFormatter<String>(c -> {
			if (c.getControlNewText().length() > 16) return null;

			checkLabel(textField, list);	
			return c;
		}));

		// Binding
		textField.textProperty().bindBidirectional(label);

		checkLabel(textField, list);
	}
	
	
	private void checkLabel(TextField textField, ObservableList<Service> list) {
		textField.setStyle(nok);

		if (!textField.getText().isEmpty()) {

			if (list != null) {	// if Service-Label

				int count = 0;
				for (Service serv: list) {

					if (serv.getLabel().getValue().contains(textField.getText())) count++;
					if (count > 1) break;
				}
				if (count < 2) textField.setStyle(ok);
	
			} 
			else textField.setStyle(ok);
		} 
	}
}

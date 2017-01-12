package addons;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import model.*;


public class ShortLabelValidation extends Validation {

	
	public ShortLabelValidation(TextField textField, StringProperty label, StringProperty shortLabel, ObservableList<Service> list) {
		
		// Text Formatter
		textField.setTextFormatter(new TextFormatter<String>(c -> {
			if (c.getControlNewText().length() > 8) return null;

			checkShortLabel(textField, label, list);	
			return c;
		}));

		// Binding
		textField.textProperty().unbindBidirectional(shortLabel);
		textField.textProperty().bindBidirectional(shortLabel);

		checkShortLabel(textField, label, list);
	}
	
	
	
	private void checkShortLabel(TextField textField, StringProperty label, ObservableList<Service> list) {
		textField.setStyle(nok);
		
		if (!textField.getText().isEmpty() && !label.getValue().isEmpty()) {
			
			int shortIndex = 0, labelIndex;
			
			for (labelIndex=0; shortIndex<textField.getText().length() && labelIndex<label.getValue().length(); labelIndex++) {
				
				if (label.getValue().charAt(labelIndex) == textField.getText().charAt(shortIndex)) {
					shortIndex++;
				}
			}
			
			if (shortIndex == textField.getText().length() && shortIndex != 0) {

				if (list != null) {	// if Service-ShortLabel
					
					int count = 0;
					for (Service serv: list) {
						
						if (serv.getShortLabel().getValue().contains(textField.getText())) count++;
						if (count > 1) break;
					}
					if (count < 2) textField.setStyle(ok);
	
				} 
				else textField.setStyle(ok);
			} 
		}
	}
}

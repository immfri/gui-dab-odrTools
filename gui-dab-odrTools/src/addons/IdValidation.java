package addons;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import model.*;


public class IdValidation extends Validation {

	protected final String hexRegEx = "^[0-9a-f]+$";
	
	
	public IdValidation(TextField textField, StringProperty id, int size, ObservableList<? extends Element> list) {
		
		// Text Formatter
		textField.setTextFormatter(new TextFormatter<> (c -> {  
			
			if (c.getControlNewText().isEmpty()) {
				checkId(textField, size, list);
				return c;
			}
			
			if (c.getControlNewText().length() <= size && c.getControlNewText().matches(hexRegEx)) {
				checkId(textField, size, list);
				return c;
			}
			
		    return null;
		}));
		
		// Binding
		textField.textProperty().bindBidirectional(id);
		
		checkId(textField, size, list);
	}

	
	private void checkId(TextField textField, int size, ObservableList<? extends Element> list) {
		textField.setStyle(nok);
			
			if ((list == null && textField.getText().length() == size) || (list != null && textField.getText().isEmpty())) {
				textField.setStyle(ok);
			}
			
			else if (list != null && textField.getText().length() == size) {	// Service-ID / Component-ID
				
				int count = 0;
				
				if (size == 4) {
					for (Element obj: list) {
						
						if (((Service)obj).getId().getValue().contains(textField.getText())) count++;
						if (count > 1) break;
					}
				} 
				else {
					for (Element obj: list) {
						
						if (((Component)obj).getId().getValue().contains(textField.getText())) count++;
						if (count > 1) break;
					}
				}
				
				if (count < 2) textField.setStyle(ok);
			}
		} 
}

package addons;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import model.*;


public class IdValidation extends Validation {
	
	public IdValidation(TextField textField, StringProperty id, int size, ObservableList<? extends Element> list) {
		
		// Text Formatter
		textField.setTextFormatter(new TextFormatter<> (c -> {  
			
			if (c.getControlNewText().isEmpty()) {
				checkId(textField, id, size, list);
				return c;
			}
			
			if (c.getControlNewText().length() == 1) {
				
				if (!c.getControlNewText().contentEquals("0x") && !c.getControlNewText().contentEquals("0")) {
					c.setText("0x");
					checkId(textField, id, size, list);
					return c;
				}
				
				else if (c.getControlNewText().contentEquals("0")) {
					checkId(textField, id, size, list);
					return c;
				}
				else return null;
			}
			
			if (c.getControlNewText().length() == 2) {
				
				if (c.getControlNewText().contentEquals("0x")) {
					checkId(textField, id, size, list);
					return c;
				}
				else return null;
			}
			
			if (c.getControlNewText().length() <= (size+2) && c.getControlNewText().substring(2).matches("^[0-9a-fA-F]*$")) {
				checkId(textField, id, size, list);
				return c;
			}
			
		    return null;
		}));
		
		
		// Binding
		textField.textProperty().unbindBidirectional(id);
		textField.textProperty().bindBidirectional(id);
		
		checkId(textField, id, size, list);
	}

	
	private void checkId(TextField textField, StringProperty id, int size, ObservableList<? extends Element> list) {
		textField.setStyle(nok);
		
		// Hex Value -> Upper Case
		if (textField.getText().length() > 2) {
			id.setValue("0x"+textField.getText().substring(2).toUpperCase());
		}
		
		
		if ((list == null && textField.getText().length() > 2) || (list != null && textField.getText().isEmpty())) {
			textField.setStyle(ok);
		}

		else if (list != null && textField.getText().length() > 2) {	// Service-/Component-ID
			
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

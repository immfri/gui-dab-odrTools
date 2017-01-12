package addons;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import model.*;


public class SubchannelIdValidation extends Validation {
	

	public SubchannelIdValidation(TextField textField, StringProperty id, ObservableList<Subchannel> list) {
		
		// Text Formatter
		textField.setTextFormatter(new TextFormatter<> (c -> {  

			if (c.getControlNewText().isEmpty()) {
				checkSubChId(textField, list);
				return c;
			}

			if (c.getControlNewText().matches("\\d*")) {
				if (Integer.parseInt(c.getControlNewText()) < 64) {
					checkSubChId(textField, list);
					return c;
				}
			}

			return null;
		}));

		// Binding
		textField.textProperty().unbindBidirectional(id);
		textField.textProperty().bindBidirectional(id);

		checkSubChId(textField, list);
	}
	
	

	private void checkSubChId(TextField textField, ObservableList<Subchannel> list) {
		textField.setStyle(nok);
		
		if (textField.getText().isEmpty()) {
			textField.setStyle(ok);
		} 
		else {
			int count = 0;
			
			for (Subchannel sub: list) {
				if (sub.getId().getValue().contentEquals(textField.getText())) count++;
				if (count > 1) break;
			} 
			if (count < 2) textField.setStyle(ok);
		}
	}
}

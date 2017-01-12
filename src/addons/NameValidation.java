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

			if (c.getControlNewText().length() > 25) return null;
			if (c.getControlNewText().contains(" ")) return null;
			
			checkName(textField, list);
			return c;
		}));

		// Binding
		textField.textProperty().unbindBidirectional(name);
		textField.textProperty().bindBidirectional(name);
		
		checkName(textField, list);
	}

	
	
	private void checkName(TextField textField, ObservableList<? extends Element> list) {
		textField.setStyle(nok);

		if (textField.getText().length() > 0) {
			int count = 0;
			
			if (list != null) {
				for (Object obj: Multiplex.getInstance().getServiceList()) {

					if (((Element)obj).getName().getValue().contentEquals(textField.getText())) count++;
					if (count > 1) break;
				}
				if (count < 2) {
					for (Object obj: Multiplex.getInstance().getSubchannelList()) {

						if (((Element)obj).getName().getValue().contentEquals(textField.getText())) count++;
						if (count > 1) break;
					}
				}
				if (count < 2) {
					for (Object obj: Multiplex.getInstance().getComponentList()) {

						if (((Element)obj).getName().getValue().contentEquals(textField.getText())) count++;
						if (count > 1) break;
					}
				}
				if (count < 2) {
					for (Object obj: Multiplex.getInstance().getOutputList()) {

						if (((Element)obj).getName().getValue().contentEquals(textField.getText())) count++;
						if (count > 1) break;
					}
				}
			}

			if (count < 2) textField.setStyle(ok);
			
		}
	}
}

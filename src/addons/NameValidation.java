package addons;

import java.util.ArrayList;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import model.*;
import model.output.*;


public class NameValidation extends Validation {
	
	
	public NameValidation(TextField textField, StringProperty name, ObservableList<? extends Element> list) {

		// Text Formatter
		textField.setTextFormatter(new TextFormatter<String>(c -> {

			if (c.getControlNewText().length() > 25) return null;
			if (c.getControlNewText().matches(".*[^-_A-Za-z0-9].*")) return null;
			
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
			
			// All Names in Project
			if (list != null) {	
				ArrayList<String> nameList = getAllNameList();
				for (String s: nameList) {
					if (s.contains(textField.getText())) count++;
					if (count > 1) break;
				}
				nameList.clear();
			}		
			if (count < 2) textField.setStyle(ok);
		}
	}
	
	
	private ArrayList<String> getAllNameList() {
		ArrayList<String> allNameList = new ArrayList<>();
		
		// Services
		for (Service serv: Multiplex.getInstance().getServiceList()) {
			allNameList.add(serv.getName().getValue());
		}
		
		// Components
		for (Component comp: Multiplex.getInstance().getComponentList()) {
			allNameList.add(comp.getName().getValue());
		}
		
		// Subchannel
		for (Subchannel subch: Multiplex.getInstance().getSubchannelList()) {
			allNameList.add(subch.getName().getValue());
		}
			
		// Outputs
		for (Output out: Multiplex.getInstance().getOutputList()) {
			allNameList.add(out.getName().getValue());
		}
				
		return allNameList;
	}
}

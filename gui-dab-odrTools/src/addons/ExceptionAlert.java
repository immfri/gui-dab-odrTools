package addons;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.control.Alert;

public class ExceptionAlert extends Alert {

	public ExceptionAlert(String location, Exception e) {
		super(AlertType.ERROR);
		
		final StringWriter sw = new StringWriter();
	    final PrintWriter pw =  new PrintWriter(sw, true);
	    
	    e.printStackTrace(pw);
		
	    this.setTitle(e.getClass().getName());
	    this.setHeaderText(e.getClass().getName() +" in "+ location);
	    this.setContentText(sw.toString());
	}

}

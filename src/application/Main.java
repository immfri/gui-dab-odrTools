package application;
	
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		
		stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/configUI/ConfigPane.fxml")), 740, 700));
		stage.getIcons().add(new Image("icons/odr-icon.png"));
		stage.setTitle("GUI4ODR");
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

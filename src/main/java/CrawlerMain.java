import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CrawlerMain extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
    	URL fxmlUrl = this.getClass()
    			.getClassLoader()
    			.getResource("CrawlerView.fxml");
        Pane mainPane = FXMLLoader.<Pane>load(fxmlUrl);
        primaryStage.setTitle("Crawler FXML");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
	}

	public static void main(String[] args) {
        Application.launch(args);
	}
}

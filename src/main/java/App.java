import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/app.fxml"));
        BorderPane rootElement = loader.load();
        Scene scene = new Scene(rootElement, 800, 600);

        primaryStage.setTitle("Java and OpenCV");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

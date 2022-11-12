import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        URL resource = Objects.requireNonNull(getClass().getResource("ChessEngine.fxml"));
        FXMLLoader loader = new FXMLLoader(resource);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");

        Controller controller = loader.getController();
        controller.setStage(stage);

        stage.setScene(scene);
        initializeStage(stage);


    }

    private void initializeStage(Stage stage) {

        stage.setResizable(false);
        stage.setTitle("Chess");

        String stageIconURL = Objects.requireNonNull(
                getClass().getResource("Images/stage icon.png")).toExternalForm();
        stage.getIcons().add(new Image(stageIconURL));

        stage.show();
    }
}
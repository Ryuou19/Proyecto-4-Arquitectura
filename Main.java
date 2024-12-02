import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Interfaz hack = new Interfaz();
        hack.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}


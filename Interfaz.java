import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Interfaz extends Application {
    private MaquinaVirtual vm;

    @Override
    public void start(Stage stage) {
        vm = new MaquinaVirtual();

        // Layout principal
        BorderPane root = new BorderPane();
        
        // Panel superior: Controles
        HBox controls = new HBox(10);
        Button loadButton = new Button("Cargar .hack");
        Button runButton = new Button("Ejecutar");
        Button stepButton = new Button("Paso a Paso");
        Button resetButton = new Button("Resetear");
        controls.getChildren().addAll(loadButton, runButton, stepButton, resetButton);

        // Panel central: Memoria e Instrucciones
        TextArea instructionsArea = new TextArea();
        instructionsArea.setEditable(false);
        TableView<String[]> memoryTable = new TableView<>();

        // Panel inferior: Consola
        TextArea console = new TextArea();
        console.setEditable(false);

        root.setTop(controls);
        root.setCenter(new VBox(instructionsArea, memoryTable));
        root.setBottom(console);

        // Acciones de botones
        loadButton.setOnAction(e -> loadProgram(stage, instructionsArea, console));
        stepButton.setOnAction(e -> {
            vm.step();
            updateUI(instructionsArea, memoryTable, console);
        });

        // Configuración de escena
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Hack Emulator");
        stage.show();
    }


    private void loadProgram(Stage stage, TextArea instructionsArea, TextArea console) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar Archivo .hack");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos .hack", "*.hack"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                vm.loadProgram(lines.toArray(new String[0]));
                instructionsArea.setText(String.join("\n", lines));
                console.appendText("Programa cargado: " + file.getName() + "\n");
            } catch (IOException ex) {
                console.appendText("Error al cargar el archivo: " + ex.getMessage() + "\n");
            }
        }
    }

    private void updateUI(TextArea instructionsArea, TableView<String[]> memoryTable, TextArea console) {
        // Aquí actualizar la memoria y mostrar en la tabla
        console.appendText("Ejecución paso a paso completada.\n");
    }
}

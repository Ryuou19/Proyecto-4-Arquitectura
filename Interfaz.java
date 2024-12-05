import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Interfaz extends Application {
    private MaquinaVirtual maquina;
    private Timeline timeline;

    @Override
    public void start(Stage stage) {
        maquina = new MaquinaVirtual();

        // Configuración de la interfaz
        BorderPane panel = new BorderPane();

        // Botones
        HBox botones = new HBox(10);
        Button botonCargar = new Button("Cargar .hack");
        Button botonEjecutar = new Button("Ejecutar");
        Button botonPorPaso = new Button("Paso a Paso");
        Button botonResetear = new Button("Resetear");
        botones.getChildren().addAll(botonCargar, botonEjecutar, botonPorPaso, botonResetear);

        // Tabla para las instrucciones
        TableView<Tablas> tablaInstrucciones = new TableView<>();
        TableColumn<Tablas, String> columnaCodigo = new TableColumn<>("Código Máquina");
        columnaCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        columnaCodigo.setPrefWidth(150);

        TableColumn<Tablas, String> columnaTraduccion = new TableColumn<>("Traducción");
        columnaTraduccion.setCellValueFactory(cellData -> cellData.getValue().traduccionProperty());
        columnaTraduccion.setPrefWidth(150);

        tablaInstrucciones.getColumns().addAll(columnaCodigo, columnaTraduccion);

        // Reducir el tamaño de fuente de la tabla
        tablaInstrucciones.setStyle("-fx-font-size: 10px;");

        // Área para mostrar los cambios en la memoria
        TextArea areaMemoria = new TextArea();
        areaMemoria.setEditable(false);
        areaMemoria.setPrefHeight(200);
        areaMemoria.setStyle("-fx-font-size: 10px;");

        VBox centro = new VBox(10);
        centro.getChildren().addAll(tablaInstrucciones, new Label("Cambios en la Memoria:"), areaMemoria);

        // Panel inferior: Consola
        TextArea consola = new TextArea();
        consola.setEditable(false);

        // Diseño general
        panel.setTop(botones);
        panel.setCenter(centro);
        panel.setBottom(consola);

        // Acciones de botones
        botonCargar.setOnAction(e -> cargarPrograma(stage, tablaInstrucciones, consola));

        botonPorPaso.setOnAction(e -> {
            maquina.step();
            actualizar(tablaInstrucciones, areaMemoria, consola);
        });

        botonEjecutar.setOnAction(e -> {
            if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
                timeline.stop();
                consola.appendText("Ejecución detenida.\n");
                botonEjecutar.setText("Ejecutar");
                return;
            }

            consola.appendText("Iniciando ejecución continua.\n");
            botonEjecutar.setText("Detener");

            timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
                if (maquina.getPC() < tablaInstrucciones.getItems().size()) {
                    maquina.step();
                    actualizar(tablaInstrucciones, areaMemoria, consola);
                } else {
                    consola.appendText("Ejecución completa.\n");
                    int resultado = maquina.getMemoryValue(16); // Cambia 16 por la dirección deseada
                    consola.appendText("El valor guardado en la memoria final es: " + resultado + "\n");
                    timeline.stop();
                    botonEjecutar.setText("Ejecutar");
                }
            }));

            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        });

        botonResetear.setOnAction(e -> {
            maquina.reset();
            tablaInstrucciones.getItems().clear();
            areaMemoria.clear();
            consola.clear();
        });

        // Configuración de la escena
        Scene scene = new Scene(panel, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Máquina Virtual");
        stage.show();
    }

    private void cargarPrograma(Stage stage, TableView<Tablas> tablaInstrucciones, TextArea consola) {
        FileChooser archivoCargado = new FileChooser();
        archivoCargado.setTitle("Cargar Archivo.hack");
        archivoCargado.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos .hack", "*.hack"));

        File archivo = archivoCargado.showOpenDialog(stage);
        if (archivo != null) {
            try {
                List<String> lineasMaquina = Files.readAllLines(archivo.toPath());
                maquina.loadProgram(lineasMaquina.toArray(new String[0]));

                // Inicializar valores dinámicos en memoria basados en las instrucciones
                inicializarMemoria(lineasMaquina);

                // Llena la tabla con las instrucciones
                tablaInstrucciones.getItems().clear();
                for (String codigo : lineasMaquina) {
                    String traduccion = Decodificador.decodeInstruction(codigo);
                    tablaInstrucciones.getItems().add(new Tablas(codigo, traduccion));
                }

                consola.appendText("Programa cargado: " + archivo.getName() + "\n");
            } catch (IOException ex) {
                consola.appendText("Error al cargar el archivo: " + ex.getMessage() + "\n");
            }
        }
    }


    private void inicializarMemoria(List<String> instrucciones) {
        // Recorrer las instrucciones y buscar valores para inicializar la memoria
        for (String instruccion : instrucciones) {
            if (instruccion.startsWith("000000000")) { // Verifica si es una instrucción tipo A (@value)
<<<<<<< HEAD
                try {
                    int direccion = Integer.parseInt(instruccion.substring(1), 2);
                    if (direccion >= 0 && direccion < 32768) { // Verifica que la dirección esté dentro de la memoria
                        maquina.setMemoryValue(direccion, direccion + 10); // Inicializa la memoria con valores basados en la dirección
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error al interpretar la instrucción: " + instruccion);
=======
                int direccion = Integer.parseInt(instruccion.substring(1), 2);
                if (direccion >= 0 && direccion < 32768) { // Verifica que la dirección esté dentro de la memoria
                    maquina.setMemoryValue(direccion, direccion + 10); // Inicializa la memoria con valores basados en la dirección
>>>>>>> origin/main
                }
            }
        }
    }


    private void actualizar(TableView<Tablas> tablaInstrucciones, TextArea areaMemoria, TextArea consola) {
        int pc = maquina.getPC();
        if (pc < tablaInstrucciones.getItems().size()) {
            Tablas instruccionActual = tablaInstrucciones.getItems().get(pc);
            consola.appendText("Ejecutando: " + instruccionActual.getTraduccion() + "\n");

            // Resalta la fila actual
            tablaInstrucciones.getSelectionModel().clearAndSelect(pc);
            tablaInstrucciones.scrollTo(pc);

            // Mostrar cambios en la memoria
            String cambiosMemoria = maquina.getMemoryState();
            areaMemoria.appendText(cambiosMemoria); // Agrega los cambios a la vista
        } else {
            consola.appendText("No hay más instrucciones para ejecutar.\n");
        }
    }
}

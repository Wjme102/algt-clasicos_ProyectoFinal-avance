package org.proyectofinal.avanceproyectofinal.visual;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.proyectofinal.avanceproyectofinal.logico.GestorRedes;
import org.proyectofinal.avanceproyectofinal.logico.Grafo;
import org.proyectofinal.avanceproyectofinal.logico.Parada;
import org.proyectofinal.avanceproyectofinal.logico.Ruta;
import javafx.animation.PauseTransition;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.application.Platform;

public class DialogHelper {

    public static void mostrarDialogoEdicionParada(Parada p, Stage owner, Grafo grafo, Runnable onRefresh) {
        if (p == null) {
            new Alert(Alert.AlertType.ERROR, "La parada a editar es nula.").showAndWait();
            return;
        }

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Parada");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #1A1A2E;");

        Label labelNombre = new Label("Nombre de la Parada:");
        labelNombre.setTextFill(Color.WHITE);
        TextField tfNombre = new TextField(p.getNombre());
        tfNombre.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        Label labelConexiones = new Label("Conectar a:");
        labelConexiones.setTextFill(Color.WHITE);
        ListView<CheckBox> listaCheckboxes = new ListView<>();
        for (Parada other : grafo.getParadas()) {
            if (other.equals(p)) continue;
            CheckBox cb = new CheckBox(other.getNombre());
            cb.setUserData(other);
            cb.setSelected(grafo.estaConectado(p, other));
            listaCheckboxes.getItems().add(cb);
        }
        listaCheckboxes.setStyle("-fx-control-inner-background: #333333; -fx-text-fill: white;");

        Button btnEliminar = new Button("Eliminar parada");
        btnEliminar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnEliminar.setOnAction(e -> {
            grafo.eliminarParada(p);
            dialog.close();
            if (onRefresh != null) onRefresh.run();
        });

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnAceptar.setOnAction(e -> {
            String nuevoNombre = tfNombre.getText();
            if (nuevoNombre.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "El nombre no puede estar vacío").showAndWait();
                return;
            }
            p.setNombre(nuevoNombre);

            for (CheckBox cb : listaCheckboxes.getItems()) {
                Parada destino = (Parada) cb.getUserData();
                if (cb.isSelected() && !grafo.estaConectado(p, destino)) {
                    grafo.agregarConexion(p, destino);
                } else if (!cb.isSelected() && grafo.estaConectado(p, destino)) {
                    grafo.eliminarConexion(p, destino);
                }
            }

            dialog.close();
            if (onRefresh != null) onRefresh.run();
        });

        HBox hboxBtns = new HBox(10);
        hboxBtns.getChildren().addAll(btnEliminar, btnAceptar);

        vbox.getChildren().addAll(labelNombre, tfNombre, labelConexiones, listaCheckboxes, hboxBtns);

        Scene scene = new Scene(vbox, 300, 400);
        dialog.setScene(scene);

        PauseTransition pause = new PauseTransition(Duration.millis(50));
        pause.setOnFinished(ev -> Platform.runLater(dialog::showAndWait));
        pause.play();
    }

    public static void mostrarDialogoEdicionRuta(Ruta ruta, Stage owner, Grafo grafo, Runnable onRefresh) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Ruta");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #1A1A2E;");

        Label lblInfo = new Label("Editar atributos de la ruta:");
        lblInfo.setTextFill(Color.WHITE);

        Label lblTiempo = new Label("Tiempo (segundos):");
        lblTiempo.setTextFill(Color.WHITE);
        TextField tfTiempo = new TextField(String.valueOf(ruta.getTiempoSegundos()));
        tfTiempo.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        Label lblDistancia = new Label("Distancia (metros):");
        lblDistancia.setTextFill(Color.WHITE);
        TextField tfDistancia = new TextField(String.valueOf(ruta.getDistanciaMetros()));
        tfDistancia.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        Label lblTransbordos = new Label("Cantidad de Transbordos:");
        lblTransbordos.setTextFill(Color.WHITE);
        TextField tfTransbordos = new TextField(String.valueOf(ruta.getCantidadTransbordos()));
        tfTransbordos.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        Label lblCosto = new Label("Costo ($):");
        lblCosto.setTextFill(Color.WHITE);
        TextField tfCosto = new TextField(String.valueOf(ruta.getCostoMonetario()));
        tfCosto.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        Button btnEliminar = new Button("Eliminar ruta");
        btnEliminar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnEliminar.setOnAction(e -> {
            Parada origen = ruta.getOrigen();
            Parada destino = ruta.getDestino();
            grafo.eliminarConexion(origen, destino);
            dialog.close();
            if (onRefresh != null) onRefresh.run();
        });

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnAceptar.setOnAction(e -> {
            try {
                long tiempo = Long.parseLong(tfTiempo.getText());
                double distancia = Double.parseDouble(tfDistancia.getText());
                double costo = Double.parseDouble(tfCosto.getText());
                int transbordos = Integer.parseInt(tfTransbordos.getText());

                // Actualizar la ruta seleccionada
                ruta.setTiempoSegundos(tiempo);
                ruta.setDistanciaMetros(distancia);
                ruta.setCostoMonetario(costo);
                ruta.setCantidadTransbordos(transbordos);

                Ruta rutaInversa = grafo.obtenerRutaInversa(ruta);
                if (rutaInversa != null) {
                    rutaInversa.setTiempoSegundos(tiempo);
                    rutaInversa.setDistanciaMetros(distancia);
                    rutaInversa.setCostoMonetario(costo);
                    rutaInversa.setCantidadTransbordos(transbordos);
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Ingrese valores numéricos válidos (tiempo como entero, distancia y costo como decimales).");
                alert.showAndWait();
                return;
            } catch (IllegalArgumentException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                alert.showAndWait();
                return;
            }
            dialog.close();
            if (onRefresh != null) onRefresh.run();
        });

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(btnEliminar, btnAceptar);

        vbox.getChildren().addAll(lblInfo, lblTiempo, tfTiempo, lblDistancia, tfDistancia, lblCosto, tfCosto, lblTransbordos, tfTransbordos, hbox);

        Scene scene = new Scene(vbox, 300, 360);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public static void mostrarDialogoDijkstra(Stage owner, Grafo grafo, Consumer<List<Parada>> onResult) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Ruta más corta");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #1A1A2E;");

        Label lblInicio = new Label("Parada de inicio:");
        lblInicio.setTextFill(Color.WHITE);
        ComboBox<Parada> cbInicio = new ComboBox<>();
        cbInicio.getItems().addAll(grafo.getParadas());

        Label lblDestino = new Label("Parada de destino:");
        lblDestino.setTextFill(Color.WHITE);
        ComboBox<Parada> cbDestino = new ComboBox<>();
        cbDestino.getItems().addAll(grafo.getParadas());

        Label lblCriterio = new Label("Criterio:");
        lblCriterio.setTextFill(Color.WHITE);
        ComboBox<String> cbCriterio = new ComboBox<>();
        cbCriterio.getItems().addAll("tiempo", "distancia", "costo", "transbordos");
        cbCriterio.setValue("tiempo");

        Button btnCalcular = new Button("Calcular");
        btnCalcular.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnCalcular.setOnAction(e -> {
            GrafoVisual.getInstance().getGraphAnimator().reset();
            GrafoVisual.getInstance().dibujar();

            Parada inicio = cbInicio.getValue();
            Parada destino = cbDestino.getValue();
            String criterio = cbCriterio.getValue();
            if (inicio == null || destino == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Seleccione ambas paradas.");
                alert.showAndWait();
            } else {
                List<Parada> path = grafo.dijkstra(inicio, destino, criterio);
                if (onResult != null) {
                    onResult.accept(path);
                }
                dialog.close();
            }
        });

        vbox.getChildren().addAll(lblInicio, cbInicio, lblDestino, cbDestino, lblCriterio, cbCriterio, btnCalcular);

        Scene scene = new Scene(vbox, 300, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public static void mostrarDialogoBellmanFord(Stage owner, Grafo grafo, GraphAnimator animator, Runnable onFrame) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Bellman-Ford");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #1A1A2E;");

        Label lblOrigen = new Label("Seleccione la Parada Origen:");
        lblOrigen.setTextFill(Color.WHITE);
        ComboBox<Parada> cbOrigen = new ComboBox<>();
        cbOrigen.getItems().addAll(grafo.getParadas());

        Label lblCriterio = new Label("Seleccione Criterio:");
        lblCriterio.setTextFill(Color.WHITE);
        ComboBox<String> cbCriterio = new ComboBox<>();
        cbCriterio.getItems().addAll("tiempo", "distancia", "costo", "transbordos");
        cbCriterio.setValue("tiempo");

        Button btnCalcular = new Button("Calcular Bellman-Ford");
        btnCalcular.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");

        btnCalcular.setOnAction(e -> {
            Parada origen = cbOrigen.getValue();
            String criterio = cbCriterio.getValue();
            if (origen == null) {
                new Alert(Alert.AlertType.ERROR, "Debe seleccionar una parada origen.").showAndWait();
                return;
            }
            dialog.close();

            Stage mainStage = owner;
            Pane root = (Pane) mainStage.getScene().getRoot();

            VBox bfBox = new VBox();
            bfBox.setPrefWidth(220);
            bfBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-border-color: #333333; -fx-border-width: 1;");
            bfBox.setLayoutX(220);
            bfBox.setLayoutY(10);

            Label header = new Label("Bellman-Ford");
            header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            header.setTextFill(Color.WHITE);
            header.setMaxWidth(Double.MAX_VALUE);
            header.setAlignment(Pos.CENTER);
            header.setStyle("-fx-background-color: #333333; -fx-cursor: move; -fx-padding: 5;");

            final double[] offsetX = {0};
            final double[] offsetY = {0};
            header.setOnMousePressed(ev -> {
                offsetX[0] = ev.getSceneX() - bfBox.getLayoutX();
                offsetY[0] = ev.getSceneY() - bfBox.getLayoutY();
            });
            header.setOnMouseDragged(ev -> {
                bfBox.setLayoutX(ev.getSceneX() - offsetX[0]);
                bfBox.setLayoutY(ev.getSceneY() - offsetY[0]);
            });

            Button btnDetener = new Button("Detener");
            btnDetener.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
            btnDetener.setOnAction(ev -> {
                animator.reset();
                root.getChildren().remove(bfBox);
                onFrame.run();
            });

            VBox content = new VBox(10);
            content.setPadding(new Insets(10));
            content.setAlignment(Pos.CENTER);
            content.getChildren().add(btnDetener);

            bfBox.getChildren().addAll(header, content);
            root.getChildren().add(bfBox);

            // Ejecutar el algoritmo Bellman-Ford y animar
            Grafo.BellmanFordResult resultado = grafo.bellmanFord(origen, criterio);
            animator.animarBellmanFord(resultado, onFrame, () -> {
                if (animator.tieneCicloNegativo()) {
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Se detectó un ciclo negativo.").showAndWait());
                }
            });
        });

        vbox.getChildren().addAll(lblOrigen, cbOrigen, lblCriterio, cbCriterio, btnCalcular);
        Scene scene = new Scene(vbox, 300, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public static void mostrarDialogoRutaFloyd(Stage owner, Grafo grafo, GraphAnimator animator, Grafo.FloydWarshallResult resultado, Runnable onFrame) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Consultar Ruta Floyd-Warshall");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #1A1A2E;");

        Label lblInicio = new Label("Parada de inicio:");
        lblInicio.setTextFill(Color.WHITE);
        ComboBox<Parada> cbInicio = new ComboBox<>();
        cbInicio.getItems().addAll(grafo.getParadas());

        Label lblDestino = new Label("Parada de destino:");
        lblDestino.setTextFill(Color.WHITE);
        ComboBox<Parada> cbDestino = new ComboBox<>();
        cbDestino.getItems().addAll(grafo.getParadas());

        Button btnConsultar = new Button("Mostrar Ruta");
        btnConsultar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnConsultar.setOnAction(e -> {
            GrafoVisual.getInstance().getGraphAnimator().reset();
            GrafoVisual.getInstance().dibujar();

            Parada origen = cbInicio.getValue();
            Parada destino = cbDestino.getValue();

            if (origen == null || destino == null) {
                new Alert(Alert.AlertType.ERROR, "Seleccione ambas paradas.").showAndWait();
                return;
            }

            List<Parada> camino = grafo.reconstruirCaminoFW(resultado, origen, destino);

            if (camino.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "No existe ruta entre las paradas seleccionadas.").showAndWait();
            } else {
                animator.animatePath(camino, onFrame, () -> DialogHelper.mostrarRutaRecorrida(camino));
                dialog.close();
            }
        });

        vbox.getChildren().addAll(lblInicio, cbInicio, lblDestino, cbDestino, btnConsultar);
        Scene scene = new Scene(vbox, 300, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public static void mostrarDialogoFloydWarshall(Stage owner, Grafo grafo, GraphAnimator animator, Runnable onFrame) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Matriz de Floyd-Warshall");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #1A1A2E;");

        Label lblCriterio = new Label("Seleccione Criterio:");
        lblCriterio.setTextFill(Color.WHITE);
        ComboBox<String> cbCriterio = new ComboBox<>();
        cbCriterio.getItems().addAll("tiempo", "distancia", "costo", "transbordos");
        cbCriterio.setValue("tiempo");

        Button btnMostrar = new Button("Optimizar Red");
        btnMostrar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnMostrar.setOnAction(e -> {
            String criterio = cbCriterio.getValue();
            dialog.close();

            Alert cargando = new Alert(Alert.AlertType.INFORMATION);
            cargando.setTitle("Calculando");
            cargando.setHeaderText(null);
            cargando.setContentText("Calculando matriz de distancias...");
            cargando.setGraphic(null);
            cargando.initOwner(owner);
            cargando.show();

            PauseTransition delay = new PauseTransition(Duration.millis(500));
            delay.setOnFinished(ev -> {
                cargando.close();

                var resultado = grafo.floydWarshall(criterio);
                List<Parada> paradas = grafo.getParadas();
                double[][] dist = resultado.getDistancias();

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(10));

                for (int j = 0; j < paradas.size(); j++) {
                    Label header = new Label(paradas.get(j).getNombre());
                    header.setTextFill(Color.YELLOW);
                    header.setStyle("-fx-font-weight: bold;");
                    grid.add(header, j + 1, 0);
                }

                for (int i = 0; i < paradas.size(); i++) {
                    Label rowHeader = new Label(paradas.get(i).getNombre());
                    rowHeader.setTextFill(Color.YELLOW);
                    rowHeader.setStyle("-fx-font-weight: bold;");
                    grid.add(rowHeader, 0, i + 1);

                    for (int j = 0; j < paradas.size(); j++) {
                        String value = dist[i][j] == Double.POSITIVE_INFINITY ? "∞" : String.format("%.1f", dist[i][j]);
                        Label cell = new Label(value);
                        cell.setTextFill(Color.WHITE);
                        cell.setStyle("-fx-background-color: transparent;");
                        grid.add(cell, j + 1, i + 1);
                    }
                }

                Button btnConsultar = new Button("Consultar ruta más corta");
                btnConsultar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
                btnConsultar.setOnAction(ev2 -> {
                    mostrarDialogoRutaFloyd(owner, grafo, animator, resultado, onFrame);
                });

                VBox contenedor = new VBox(10, grid, btnConsultar);
                contenedor.setPadding(new Insets(10));
                contenedor.setAlignment(Pos.CENTER_LEFT); // mejor para ver toda la tabla
                contenedor.setStyle("-fx-background-color: #1A1A2E;");

                ScrollPane scrollPane = new ScrollPane(contenedor);
                scrollPane.setFitToWidth(false);
                scrollPane.setFitToHeight(false);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setId("infoScrollPane"); // opcional: si quieres usar el mismo estilo exacto
                scrollPane.getStyleClass().add("my-scroll-pane");
                scrollPane.getStylesheets().add(
                        Objects.requireNonNull(
                                DialogHelper.class.getResource("/org/proyectofinal/avanceproyectofinal/logico/visual/darkscroll.css")
                        ).toExternalForm()
                );


                Scene matrizScene = new Scene(scrollPane, 700, 500);

                Stage matrizStage = new Stage();
                matrizStage.initOwner(owner);
                matrizStage.initModality(Modality.APPLICATION_MODAL);
                matrizStage.setTitle("Matriz de distancias Floyd-Warshall");
                matrizStage.setScene(matrizScene);
                matrizStage.show();

            });

            delay.play();
        });

        vbox.getChildren().addAll(lblCriterio, cbCriterio, btnMostrar);
        Scene scene = new Scene(vbox, 300, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }



    public static void mostrarDialogoGuardar(Stage stage, Grafo grafo) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Especificaciones de la Red");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label label = new Label("Especifique el nombre de la red:");
        TextField tfNombre = new TextField();

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(vbox);
        vbox.getChildren().addAll(label, tfNombre);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                String nombre = tfNombre.getText().trim();
                if (nombre.isEmpty()) {
                    Alert alerta = new Alert(Alert.AlertType.ERROR, "El nombre de la red no puede estar vacío.");
                    alerta.showAndWait();
                    return null;
                }

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Guardar Red de Transporte");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Red", "*.red"));

                fileChooser.setInitialFileName(nombre + ".red");

                String projectPath = System.getProperty("user.dir");
                System.out.println("Project root directory: " + projectPath);
                File grafosDir = new File(projectPath, "src/main/resources/Grafos");

                if (!grafosDir.exists()) {
                    System.out.println("Directory does not exist, attempting to create: " + grafosDir.getAbsolutePath());
                    boolean created = grafosDir.mkdirs();
                    if (!created) {
                        System.out.println("Failed to create directory: " + grafosDir.getAbsolutePath());
                        Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo crear el directorio para guardar el archivo.");
                        alerta.showAndWait();
                        return null;
                    } else {
                        System.out.println("Directory created successfully: " + grafosDir.getAbsolutePath());
                    }
                } else {
                    System.out.println("Directory already exists: " + grafosDir.getAbsolutePath());
                }

                if (!grafosDir.isDirectory()) {
                    System.out.println("Path is not a directory: " + grafosDir.getAbsolutePath());
                    Alert alerta = new Alert(Alert.AlertType.ERROR, "La ruta especificada no es un directorio válido: " + grafosDir.getAbsolutePath());
                    alerta.showAndWait();
                    return null;
                }

                System.out.println("Setting initial directory to: " + grafosDir.getAbsolutePath());
                fileChooser.setInitialDirectory(grafosDir);

                File archivo = fileChooser.showSaveDialog(stage);
                if (archivo != null) {
                    System.out.println("Saving file to: " + archivo.getAbsolutePath());
                    GestorRedes.guardarRed(grafo, nombre, archivo);
                } else {
                    System.out.println("No file selected (operation cancelled).");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
    public static Grafo mostrarDialogoCargar(Stage stage, Runnable onCargar) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar Red de Transporte");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Red", "*.red"));

        String projectPath = System.getProperty("user.dir");
        System.out.println("Project root directory: " + projectPath);

        File grafosDir = new File(projectPath, "src/main/resources/Grafos");

        if (!grafosDir.exists()) {
            System.out.println("Directory does not exist, attempting to create: " + grafosDir.getAbsolutePath());
            boolean created = grafosDir.mkdirs();
            if (!created) {
                System.out.println("Failed to create directory: " + grafosDir.getAbsolutePath());
                Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo crear el directorio para cargar el archivo.");
                alerta.showAndWait();
                return null;
            } else {
                System.out.println("Directory created successfully: " + grafosDir.getAbsolutePath());
            }
        } else {
            System.out.println("Directory already exists: " + grafosDir.getAbsolutePath());
        }

        if (!grafosDir.isDirectory()) {
            System.out.println("Path is not a directory: " + grafosDir.getAbsolutePath());
            Alert alerta = new Alert(Alert.AlertType.ERROR, "La ruta especificada no es un directorio válido: " + grafosDir.getAbsolutePath());
            alerta.showAndWait();
            return null;
        }

        System.out.println("Setting initial directory to: " + grafosDir.getAbsolutePath());
        fileChooser.setInitialDirectory(grafosDir);

        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo != null) {
            System.out.println("File selected: " + archivo.getAbsolutePath());

            Grafo nuevoGrafo = GestorRedes.cargarRed(archivo);
            if (nuevoGrafo != null) {
                if (nuevoGrafo.getParadas().size() < 2) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error al cargar");
                    alert.setHeaderText(null);
                    alert.setContentText("El grafo cargado debe tener al menos 2 paradas.");
                    alert.showAndWait();
                    return null;
                } else {

                    if (onCargar != null) {
                        onCargar.run();
                    }
                    return nuevoGrafo;
                }
            } else {
                // Mostrar una alerta si no se pudo cargar el grafo
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al cargar");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo cargar la red desde el archivo seleccionado.");
                alert.showAndWait();
                return null;
            }
        } else {
            System.out.println("No file selected (operation cancelled)."); // Debugging
            // Si no se selecciona un archivo, mostrar mensaje de cancelación
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Operación Cancelada");
            alert.setHeaderText(null);
            alert.setContentText("La operación de carga fue cancelada.");
            alert.showAndWait();
            return null;
        }
    }
    public static void mostrarDialogoPrim(Stage owner, Grafo grafo, GraphAnimator animator, Runnable onFrame) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Algoritmo Prim");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #1A1A2E;");

        Label lblCriterio = new Label("Seleccione Criterio:");
        lblCriterio.setTextFill(Color.WHITE);
        ComboBox<String> cbCriterio = new ComboBox<>();
        cbCriterio.getItems().addAll("tiempo", "distancia", "costo", "transbordos");
        cbCriterio.setValue("costo");

        Button btnCalcular = new Button("Calcular MST con Prim");
        btnCalcular.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnCalcular.setOnAction(e -> {
            String criterio = cbCriterio.getValue();
            dialog.close();
            GrafoVisual.getInstance().getGraphAnimator().reset();
            GrafoVisual.getInstance().dibujar();
            Grafo.PrimResult resultado = grafo.prim(criterio);
            animator.animarPrim(resultado, onFrame, () -> {
                Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "Prim completado.").showAndWait());
            });
        });

        vbox.getChildren().addAll(lblCriterio, cbCriterio, btnCalcular);
        Scene scene = new Scene(vbox, 300, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public static void mostrarDialogoKruskal(Stage owner, Grafo grafo, GraphAnimator animator, Runnable onFrame) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Algoritmo Kruskal");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #1A1A2E;");

        Label lblCriterio = new Label("Seleccione Criterio");
        lblCriterio.setTextFill(Color.WHITE);
        ComboBox<String> cbCriterio = new ComboBox<>();
        cbCriterio.getItems().addAll("tiempo", "distancia", "costo", "transbordos");
        cbCriterio.setValue("costo");

        Button btnCalcular = new Button("Calcular MST con Kruskal");
        btnCalcular.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnCalcular.setOnAction(e -> {

            String criterio = cbCriterio.getValue();
            dialog.close();
            GrafoVisual.getInstance().getGraphAnimator().reset();
            GrafoVisual.getInstance().dibujar();
            Grafo.KruskalResult resultado = grafo.kruskal(criterio);
            animator.animarKruskal(resultado, onFrame, () -> {
                Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "Kruskal completado.").showAndWait());
            });
        });

        vbox.getChildren().addAll(lblCriterio, cbCriterio, btnCalcular);
        Scene scene = new Scene(vbox, 300, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public static void mostrarRutaRecorrida(List<Parada> path) {
        StringBuilder sb = new StringBuilder();
        for (Parada p : path) {
            sb.append(p.toString()).append(" -> ");
        }
        if (sb.length() >= 4) {
            sb.setLength(sb.length() - 4);
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ruta recorrida: " + sb.toString());
        alert.showAndWait();
    }
}

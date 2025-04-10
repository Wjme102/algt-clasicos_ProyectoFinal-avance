package org.proyectofinal.avanceproyectofinal.visual;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.proyectofinal.avanceproyectofinal.logico.GestorRedes;
import org.proyectofinal.avanceproyectofinal.logico.Grafo;
import org.proyectofinal.avanceproyectofinal.logico.Parada;
import org.proyectofinal.avanceproyectofinal.logico.Ruta;

import java.io.File;
import java.util.*;

public class GrafoVisual extends Application {

    public Grafo grafoLogico;
    private Canvas canvas;
    private GraphRenderer graphRenderer;
    private GraphAnimator graphAnimator;
    private static GrafoVisual instancia;

    private double mouseX, mouseY;
    private boolean mostrarPlaceholder;
    private final double RADIO = 15;

    private Parada paradaArrastrada = null;
    private double offsetX, offsetY;
    private boolean dragging = false;
    private Parada paradaPresionada = null;

    private VBox movableInfoBox;
    private VBox movableAlgoritmosBox;
    private VBox infoBox;
    private VBox configuracionRedBox;
    private ScrollPane infoScrollPane;
    private VBox movableInfoBoxRutas = new VBox();

    private double infoBoxDragOffsetX, infoBoxDragOffsetY;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        instancia = this;
        this.grafoLogico = new Grafo();
        this.graphAnimator = new GraphAnimator();
        this.mostrarPlaceholder = false;

        Pane root = new Pane();

        this.canvas = new Canvas(2560, 1600);
        root.getChildren().add(canvas);

        setupMovableInfoBox(root);
        setupMovableAlgoritmosBox(root);
        setupMovableInfoBoxRutas(root);
        setupConfiguracionRedBox(root);

        root.setStyle("-fx-background-color: #1A1A2E;");

        this.graphRenderer = new GraphRenderer(canvas, grafoLogico);

        Scene scene = new Scene(root, 2560, 1600);
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        infoScrollPane.maxHeightProperty().bind(scene.heightProperty().multiply(0.4));

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::onMouseMoved);
        canvas.addEventHandler(MouseEvent.MOUSE_ENTERED, this::onMouseEntered);
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, this::onMouseExited);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateBoxesPosition();
            dibujar();
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateBoxesPosition();
            dibujar();
        });

        stage.setScene(scene);
        stage.setTitle("Grafo Visual - Red de Transporte");
        stage.setMaximized(true);
        stage.show();

        dibujar();
        Platform.runLater(this::updateBoxesPosition);

    }

    public static GrafoVisual getInstance() {
        return instancia;
    }



    private void setupMovableInfoBox(Pane root) {
        movableInfoBox = new VBox();
        movableInfoBox.setLayoutX(10);
        movableInfoBox.setLayoutY(10);
        movableInfoBox.setPrefWidth(200);
        movableInfoBox.setAlignment(Pos.TOP_CENTER);

        Label header = new Label("Información de paradas");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        header.setTextFill(Color.WHITE);
        header.setStyle("-fx-background-color: #333333; -fx-cursor: move; -fx-padding: 5;");
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setOnMousePressed(e -> {
            infoBoxDragOffsetX = e.getSceneX() - movableInfoBox.getLayoutX();
            infoBoxDragOffsetY = e.getSceneY() - movableInfoBox.getLayoutY();
            updateBoxesPosition();
        });
        header.setOnMouseDragged(e -> {
            movableInfoBox.setLayoutX(e.getSceneX() - infoBoxDragOffsetX);
            movableInfoBox.setLayoutY(e.getSceneY() - infoBoxDragOffsetY);
            updateBoxesPosition();
        });

        infoBox = new VBox(5);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-font-size: 13;");
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.prefWidthProperty().bind(movableInfoBox.widthProperty());

        infoScrollPane = new ScrollPane(infoBox);
        infoScrollPane.setFitToWidth(true);
        infoScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        infoScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        infoScrollPane.getStyleClass().add("my-scroll-pane");
        infoScrollPane.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/org/proyectofinal/avanceproyectofinal/logico/visual/darkscroll.css")
                ).toExternalForm()
        );
        infoScrollPane.setId("infoScrollPane");

        movableInfoBox.getChildren().addAll(header, infoScrollPane);
        root.getChildren().add(movableInfoBox);
    }

    private void setupMovableAlgoritmosBox(Pane root) {
        movableAlgoritmosBox = new VBox();
        movableAlgoritmosBox.setLayoutX(10);
        movableAlgoritmosBox.setLayoutY(300);
        movableAlgoritmosBox.setPrefWidth(200);
        movableAlgoritmosBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-border-color: #333333; -fx-border-width: 1;");

        Label header = new Label("Algoritmos Clásicos");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        header.setTextFill(Color.WHITE);
        header.setStyle("-fx-background-color: #333333; -fx-padding: 5;");
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(10));
        contentBox.setAlignment(Pos.CENTER);

        Label lblBusqueda = new Label("Búsqueda de Rutas");
        lblBusqueda.setTextFill(Color.WHITE);
        lblBusqueda.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblBusqueda.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 5;");
        lblBusqueda.setMaxWidth(Double.MAX_VALUE);
        lblBusqueda.setAlignment(Pos.CENTER);

        VBox busquedaBox = new VBox(10);
        busquedaBox.setAlignment(Pos.CENTER);

        Button btnDijkstra = new Button("Dijsktra");
        btnDijkstra.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnDijkstra.setOnAction(e -> {
            DialogHelper.mostrarDialogoDijkstra(
                    (Stage) canvas.getScene().getWindow(),
                    grafoLogico,
                    path -> {
                        if (path == null || path.isEmpty()) {
                            new Alert(Alert.AlertType.INFORMATION, "No se encontró ruta.").showAndWait();
                        } else {
                            Pane paneroot = (Pane) canvas.getScene().getRoot();

                            VBox dijkstraBox = new VBox();
                            dijkstraBox.setPrefWidth(220);
                            dijkstraBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-border-color: #333333; -fx-border-width: 1;");
                            dijkstraBox.setLayoutX(220);
                            dijkstraBox.setLayoutY(10);

                            Label headerpane = new Label("Dijkstra");
                            headerpane.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                            headerpane.setTextFill(Color.WHITE);
                            headerpane.setStyle("-fx-background-color: #333333; -fx-cursor: move; -fx-padding: 5;");
                            headerpane.setMaxWidth(Double.MAX_VALUE);
                            headerpane.setAlignment(Pos.CENTER);

                            final double[] offsetX = {0};
                            final double[] offsetY = {0};
                            headerpane.setOnMousePressed(ev -> {
                                offsetX[0] = ev.getSceneX() - dijkstraBox.getLayoutX();
                                offsetY[0] = ev.getSceneY() - dijkstraBox.getLayoutY();
                            });
                            headerpane.setOnMouseDragged(ev -> {
                                dijkstraBox.setLayoutX(ev.getSceneX() - offsetX[0]);
                                dijkstraBox.setLayoutY(ev.getSceneY() - offsetY[0]);
                            });

                            Button btnDetener = new Button("Detener");
                            btnDetener.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
                            btnDetener.setOnAction(ev -> {
                                graphAnimator.detenerAnimacionDijkstra(() -> {
                                    paneroot.getChildren().remove(dijkstraBox);
                                    graphAnimator.reset();
                                    dibujar();
                                });
                            });

                            VBox content = new VBox(10);
                            content.setPadding(new Insets(10));
                            content.setAlignment(Pos.CENTER);
                            content.getChildren().add(btnDetener);

                            dijkstraBox.getChildren().addAll(headerpane, content);
                            paneroot.getChildren().add(dijkstraBox);

                            graphAnimator.animatePath(path, this::dibujar, () -> {

                            });
                        }
                    }
            );
        });

        Button btnBellmanFord = new Button("Bellman-Ford");
        btnBellmanFord.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnBellmanFord.setOnAction(e -> {
            DialogHelper.mostrarDialogoBellmanFord(
                    (Stage) canvas.getScene().getWindow(),
                    grafoLogico,
                    graphAnimator,
                    this::dibujar
            );
        });

        Button btnFloydWarshall = new Button("Floyd-Warshall");
        btnFloydWarshall.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnFloydWarshall.setOnAction(e -> {
            graphAnimator.reset();
            dibujar();
            Alert cargando = new Alert(Alert.AlertType.INFORMATION);
            cargando.setTitle("Calculando");
            cargando.setHeaderText(null);
            cargando.setContentText("Cargando matriz de distancias...");
            cargando.setGraphic(null);
            cargando.initOwner(canvas.getScene().getWindow());
            cargando.show();

            // Llamamos el diálogo después de una pequeña pausa para que se vea el mensaje
            PauseTransition delay = new PauseTransition(Duration.millis(500));
            delay.setOnFinished(ev -> {
                cargando.close();
                Platform.runLater(() -> {
                    DialogHelper.mostrarDialogoFloydWarshall(
                            (Stage) canvas.getScene().getWindow(),
                            grafoLogico,
                            graphAnimator,
                            this::dibujar
                    );
                });
            });
            delay.play();
        });

        busquedaBox.getChildren().addAll(btnDijkstra, btnBellmanFord, btnFloydWarshall);

        Label lblMST = new Label("Optimización MST");
        lblMST.setTextFill(Color.WHITE);
        lblMST.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblMST.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 5;");
        lblMST.setMaxWidth(Double.MAX_VALUE);
        lblMST.setAlignment(Pos.CENTER);

        VBox mstBox = new VBox(10);
        mstBox.setAlignment(Pos.CENTER);

        Button btnPrim = new Button("Prim MST");
        btnPrim.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnPrim.setOnAction(e -> {
            DialogHelper.mostrarDialogoPrim(
                    (Stage) canvas.getScene().getWindow(),
                    grafoLogico,
                    graphAnimator,
                    this::dibujar
            );
        });

        Button btnKruskal = new Button("Kruskal MST");
        btnKruskal.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnKruskal.setOnAction(e -> {
            DialogHelper.mostrarDialogoKruskal(
                    (Stage) canvas.getScene().getWindow(),
                    grafoLogico,
                    graphAnimator,
                    this::dibujar
            );
        });

        mstBox.getChildren().addAll(btnPrim, btnKruskal);

        contentBox.getChildren().addAll(lblBusqueda, busquedaBox, lblMST, mstBox);
        movableAlgoritmosBox.getChildren().addAll(header, contentBox);
        root.getChildren().add(movableAlgoritmosBox);
    }

    private void setupMovableInfoBoxRutas(Pane root) {
        movableInfoBoxRutas.setLayoutX(10);
        movableInfoBoxRutas.setLayoutY(10);
        movableInfoBoxRutas.setPrefWidth(200);
        movableInfoBoxRutas.setAlignment(Pos.TOP_CENTER);
        movableInfoBoxRutas.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-border-color: #333333; -fx-border-width: 1;");

        Label headerRutas = new Label("Información sobre las rutas");
        headerRutas.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        headerRutas.setTextFill(Color.WHITE);
        headerRutas.setStyle("-fx-background-color: #333333; -fx-padding: 5;");
        headerRutas.setAlignment(Pos.CENTER);
        headerRutas.setMaxWidth(Double.MAX_VALUE);

        VBox infoBoxRutas = new VBox(5);
        infoBoxRutas.setPadding(new Insets(10));
        infoBoxRutas.setStyle("-fx-background-color: rgba(255,255,255,0.0);");
        infoBoxRutas.setAlignment(Pos.TOP_LEFT);
        infoBoxRutas.prefWidthProperty().bind(movableInfoBoxRutas.widthProperty());

        Label mensajeRutas = new Label("Para cambiar atributos de una \nruta, haga clic sobre ella.");
        mensajeRutas.setTextFill(Color.WHITE);
        infoBoxRutas.getChildren().add(mensajeRutas);

        movableInfoBoxRutas.getChildren().addAll(headerRutas, infoBoxRutas);
        root.getChildren().add(movableInfoBoxRutas);
    }

    private void setupConfiguracionRedBox(Pane root) {
        // Crear el contenedor para los botones
        configuracionRedBox = new VBox(10);
        configuracionRedBox.setPrefWidth(260); // Aumentar el ancho para que el texto del label quepa completamente
        configuracionRedBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-border-color: #333333; -fx-border-width: 1;");

        // Header
        Label header = new Label("Configuración de Red de Transportes");
        header.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 5; -fx-font-weight: bold;");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);
        header.setWrapText(true); // Permitir que el texto se envuelva si es necesario

        // Crear un HBox para los botones (para que estén uno al lado del otro)
        Button btnGuardar = new Button("Guardar Red");
        btnGuardar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnGuardar.setOnAction(e -> {
            // Validar que el grafo tenga al menos 2 paradas
            if (grafoLogico.getParadas().size() < 2) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al guardar");
                alert.setHeaderText(null);
                alert.setContentText("El grafo debe tener al menos 2 paradas para poder guardarlo.");
                alert.showAndWait();
            } else {
                // Si la validación pasa, mostrar el diálogo para guardar
                DialogHelper.mostrarDialogoGuardar((Stage) canvas.getScene().getWindow(), grafoLogico);
            }
        });

        Button btnCargar = new Button("Cargar Red");
        btnCargar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnCargar.setOnAction(e -> {
            // Llamar a mostrarDialogoCargar para que maneje la selección del archivo y la carga del grafo
            Grafo nuevoGrafo = DialogHelper.mostrarDialogoCargar((Stage) canvas.getScene().getWindow(), () -> {
                // Este callback se ejecutará dentro de mostrarDialogoCargar si el grafo es válido
                dibujar();
            });
            // Si se cargó un grafo válido, actualizar grafoLogico y graphRenderer
            if (nuevoGrafo != null) {
                grafoLogico = nuevoGrafo;
                graphRenderer = new GraphRenderer(canvas, grafoLogico); // Update the GraphRenderer
            }
        });

        // Usar el constructor de HBox para agregar los botones directamente
        HBox buttonBox = new HBox(10, btnGuardar, btnCargar); // Espaciado de 10 píxeles entre los botones
        buttonBox.setAlignment(Pos.CENTER); // Alineación centrada de los botones
        buttonBox.setPadding(new Insets(0, 0, 10, 0)); // Agregar padding inferior para despegar los botones del fondo

        // Agregar el HBox a configuracionRedBox
        configuracionRedBox.getChildren().addAll(header, buttonBox);

        // Agregar al root de la ventana
        root.getChildren().add(configuracionRedBox);

        // Posicionar la caja en la parte inferior izquierda
        configuracionRedBox.setLayoutX(10); // Fijo en la izquierda con un margen de 10 píxeles

        // Asegurarse de que la escena esté disponible antes de bindear
        Platform.runLater(() -> {
            Scene scene = root.getScene();
            if (scene != null) {
                // Bindear la posición Y para que siempre esté en la parte inferior
                configuracionRedBox.layoutYProperty().bind(
                        scene.heightProperty()
                                .subtract(configuracionRedBox.heightProperty())
                                .subtract(10)
                );
            }
        });
    }

    private void onMouseMoved(MouseEvent event) {


        mouseX = event.getX();
        mouseY = event.getY();

        boolean sobreParada = (obtenerParadaCercaDe(mouseX, mouseY) != null);
        boolean sobreRuta = false;
        for (List<Ruta> list : grafoLogico.getAdjList().values()) {
            for (Ruta r : list) {
                double d = graphRenderer.distancePointToSegment(
                        mouseX, mouseY,
                        r.getOrigen().getX(), r.getOrigen().getY(),
                        r.getDestino().getX(), r.getDestino().getY()
                );
                if (d < 7) {
                    sobreRuta = true;
                    break;
                }
            }
            if (sobreRuta) break;
        }
        mostrarPlaceholder = !(sobreParada || sobreRuta);
        dibujar();
    }

    private void onMouseEntered(MouseEvent event) {
        //if (graphAnimator.isAnimatingBellmanFord()) return;
        if (obtenerParadaCercaDe(event.getX(), event.getY()) == null) {
            boolean sobreRuta = false;
            for (List<Ruta> list : grafoLogico.getAdjList().values()) {
                for (Ruta r : list) {
                    double d = graphRenderer.distancePointToSegment(
                            event.getX(), event.getY(),
                            r.getOrigen().getX(), r.getOrigen().getY(),
                            r.getDestino().getX(), r.getDestino().getY()
                    );
                    if (d < 7) {
                        sobreRuta = true;
                        break;
                    }
                }
                if (sobreRuta) break;
            }
            mostrarPlaceholder = !sobreRuta;
        }
        dibujar();
    }

    private void onMouseExited(MouseEvent event) {
        //if (graphAnimator.isAnimatingBellmanFord()) return;
        mostrarPlaceholder = false;
        dibujar();
    }

    private void onMousePressed(MouseEvent event) {
        //if (graphAnimator.isAnimatingBellmanFord()) return;
        Parada p = obtenerParadaCercaDe(event.getX(), event.getY());
        if (p != null) {
            paradaArrastrada = p;
            paradaPresionada = p;
            offsetX = event.getX() - p.getX();
            offsetY = event.getY() - p.getY();
            dragging = false;
        } else {
            paradaArrastrada = null;
        }
        dibujar();
    }

    private void onMouseDragged(MouseEvent event) {
        //if (graphAnimator.isAnimatingBellmanFord()) return;
        if (paradaArrastrada != null) {
            paradaArrastrada.setX(event.getX() - offsetX);
            paradaArrastrada.setY(event.getY() - offsetY);
            dragging = true;
            dibujar();
        }
    }

    private void onMouseReleased(MouseEvent event) {
        //if (graphAnimator.isAnimatingBellmanFord()) return;
        if (paradaArrastrada != null) {
            if (!dragging) {
                DialogHelper.mostrarDialogoEdicionParada(
                        paradaArrastrada,
                        (Stage) canvas.getScene().getWindow(),
                        grafoLogico,
                        this::dibujar
                );
            }
            paradaArrastrada = null;
            paradaPresionada = null;
            dragging = false;
        } else {
            Ruta rutaClickeada = null;
            for (List<Ruta> list : grafoLogico.getAdjList().values()) {
                for (Ruta r : list) {
                    double d = graphRenderer.distancePointToSegment(
                            mouseX, mouseY,
                            r.getOrigen().getX(), r.getOrigen().getY(),
                            r.getDestino().getX(), r.getDestino().getY()
                    );
                    if (d < 5) {
                        rutaClickeada = r;
                        break;
                    }
                }
                if (rutaClickeada != null) break;
            }
            if (rutaClickeada != null) {
                DialogHelper.mostrarDialogoEdicionRuta(
                        rutaClickeada,
                        (Stage) canvas.getScene().getWindow(),
                        grafoLogico,
                        this::dibujar
                );
            } else {
                Parada nueva = new Parada(mouseX, mouseY, "");
                grafoLogico.agregarParada(nueva);
                updateBoxesPosition();
                canvas.getScene().getRoot().applyCss();
                canvas.getScene().getRoot().layout();
                PauseTransition pause = new PauseTransition(Duration.millis(100));
                pause.setOnFinished(e -> {
                    DialogHelper.mostrarDialogoEdicionParada(
                            nueva,
                            (Stage) canvas.getScene().getWindow(),
                            grafoLogico,
                            this::dibujar
                    );
                });
                pause.play();
            }
            dibujar();
        }
    }

    public void dibujar() {


        graphRenderer.drawAll(mouseX, mouseY, mostrarPlaceholder,
                paradaArrastrada != null ? paradaArrastrada : paradaPresionada,
                RADIO, graphAnimator);
        updateInfoBox();
        updateBoxesPosition();
        if (canvas.getParent() != null) {
            canvas.getParent().requestLayout();
        }
    }

    private void updateInfoBox() {
        infoBox.getChildren().clear();
        if (grafoLogico.getParadas().isEmpty()) {
            Label msg = new Label("Para crear una parada,\nclickea en cualquier espacio\nde la ventana");
            msg.setTextFill(Color.WHITE);
            infoBox.getChildren().add(msg);
        } else {
            for (Parada p : grafoLogico.getParadas()) {
                int connCount = grafoLogico.getConnectionCount(p);
                String info = "Nombre: " + (p.getNombre().isEmpty() ? "(sin nombre)" : p.getNombre())
                        + "\nPosición: (" + (int) p.getX() + ", " + (int) p.getY() + ")"
                        + "\nRutas: " + connCount;
                Label infoLabel = new Label(info);
                infoLabel.setTextFill(Color.WHITE);
                infoLabel.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
                infoLabel.setPadding(new Insets(3));
                infoBox.getChildren().add(infoLabel);
            }
        }
    }

    private void updateBoxesPosition() {
        movableAlgoritmosBox.setLayoutX(movableInfoBox.getLayoutX());
        movableAlgoritmosBox.setLayoutY(movableInfoBox.getLayoutY() + movableInfoBox.getHeight() + 10);
        movableInfoBoxRutas.setLayoutX(movableAlgoritmosBox.getLayoutX());
        movableInfoBoxRutas.setLayoutY(movableAlgoritmosBox.getLayoutY() + movableAlgoritmosBox.getHeight() + 10);
    }

    private Parada obtenerParadaCercaDe(double x, double y) {
        for (Parada p : grafoLogico.getParadas()) {
            if (Math.hypot(p.getX() - x, p.getY() - y) <= RADIO) {
                return p;
            }
        }
        return null;
    }

    public GraphAnimator getGraphAnimator() {
        return graphAnimator;
    }
}

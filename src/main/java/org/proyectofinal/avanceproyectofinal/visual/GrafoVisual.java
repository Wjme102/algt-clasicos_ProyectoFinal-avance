package org.proyectofinal.avanceproyectofinal.visual;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.proyectofinal.avanceproyectofinal.logico.Grafo;
import org.proyectofinal.avanceproyectofinal.logico.Parada;
import org.proyectofinal.avanceproyectofinal.logico.Ruta;

import java.util.*;

public class GrafoVisual extends Application {

    private Grafo grafoLogico;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        grafoLogico = new Grafo();
        mostrarPlaceholder = false;
    
        Pane root = new Pane();
        Canvas canvas = new Canvas(800, 600);
        root.getChildren().add(canvas);
    
        // Contenedor movible para la infoBox
        movableInfoBox = new VBox();
        movableInfoBox.setLayoutX(10);
        movableInfoBox.setLayoutY(10);
        movableInfoBox.setPrefWidth(200);
        movableInfoBox.setAlignment(Pos.TOP_CENTER);
    
        // Header del contenedor
        Label header = new Label("Información de paradas");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        header.setTextFill(Color.WHITE);
        header.setStyle("-fx-background-color: #333333; -fx-cursor: move; -fx-padding: 5;");
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setOnMousePressed(e -> {
            infoBoxDragOffsetX = e.getSceneX() - movableInfoBox.getLayoutX();
            infoBoxDragOffsetY = e.getSceneY() - movableInfoBox.getLayoutY();
        });
        header.setOnMouseDragged(e -> {
            movableInfoBox.setLayoutX(e.getSceneX() - infoBoxDragOffsetX);
            movableInfoBox.setLayoutY(e.getSceneY() - infoBoxDragOffsetY);
        });
    
        // Configuración de la infoBox
        infoBox = new VBox(5);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.prefWidthProperty().bind(movableInfoBox.widthProperty());
    
        movableInfoBox.getChildren().addAll(header, infoBox);
        root.getChildren().add(movableInfoBox);

        // Se agrego el otro cuadro con informacion sobre las rutas
        VBox movableInfoBoxRutas = new VBox();
        movableInfoBoxRutas.setLayoutX(220); // Ubicación a la derecha del otro cuadro
        movableInfoBoxRutas.setLayoutY(10);
        movableInfoBoxRutas.setPrefWidth(200);
        movableInfoBoxRutas.setAlignment(Pos.TOP_CENTER);

        // Crear header del contenedor de rutas
        Label headerRutas = new Label("Información sobre las rutas");
        headerRutas.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        headerRutas.setTextFill(Color.WHITE);
        headerRutas.setStyle("-fx-background-color: #333333; -fx-cursor: move; -fx-padding: 5;");
        headerRutas.setAlignment(Pos.CENTER);
        headerRutas.setMaxWidth(Double.MAX_VALUE);
        headerRutas.setOnMousePressed(e -> {
            infoBoxDragOffsetX = e.getSceneX() - movableInfoBoxRutas.getLayoutX();
            infoBoxDragOffsetY = e.getSceneY() - movableInfoBoxRutas.getLayoutY();
        });
        headerRutas.setOnMouseDragged(e -> {
            movableInfoBoxRutas.setLayoutX(e.getSceneX() - infoBoxDragOffsetX);
            movableInfoBoxRutas.setLayoutY(e.getSceneY() - infoBoxDragOffsetY);
        });

        // Crear infoBox de rutas con mensaje
        VBox infoBoxRutas = new VBox(5);
        infoBoxRutas.setPadding(new Insets(10));
        infoBoxRutas.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
        infoBoxRutas.setAlignment(Pos.TOP_LEFT);
        infoBoxRutas.prefWidthProperty().bind(movableInfoBoxRutas.widthProperty());

        Label mensajeRutas = new Label("Para cambiar atributos de una \nruta, haga clic sobre ella.");
        mensajeRutas.setTextFill(Color.WHITE);
        mensajeRutas.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        infoBoxRutas.getChildren().add(mensajeRutas);

        // Agregar header e infoBoxRutas a movableInfoBoxRutas
        movableInfoBoxRutas.getChildren().addAll(headerRutas, infoBoxRutas);
        root.getChildren().add(movableInfoBoxRutas);
        
        root.setStyle("-fx-background-color: #1A1A2E;");
    
        gc = canvas.getGraphicsContext2D();
        gc.setFont(new Font("Arial", 12));
    
        Scene scene = new Scene(root, 800, 600);
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());
    
        // Registro de eventos de mouse en el canvas
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::onMouseMoved);
        canvas.addEventHandler(MouseEvent.MOUSE_ENTERED, this::onMouseEntered);
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, this::onMouseExited);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);
    
        stage.setScene(scene);
        stage.setTitle("Grafo Visual - Red de Transporte");
        stage.setMaximized(true);
        stage.show();
    
        dibujar();
    }

    // Actualiza la posición del mouse y controla el placeholder
    private void onMouseMoved(MouseEvent event) {
    mouseX = event.getX();
    mouseY = event.getY();
    boolean sobreParada = (obtenerParadaCercaDe(mouseX, mouseY) != null);
    boolean sobreRuta = false;
    for (List<Ruta> list : grafoLogico.getAdjList().values()) {
        for (Ruta r : list) {
            double d = distancePointToSegment(mouseX, mouseY,
                    r.getOrigen().getX(), r.getOrigen().getY(),
                    r.getDestino().getX(), r.getDestino().getY());
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

    
    // Actualiza el placeholder al entrar el mouse según si está sobre una parada o ruta
    private void onMouseEntered(MouseEvent event) {
        if (obtenerParadaCercaDe(event.getX(), event.getY()) == null) {
            boolean sobreRuta = false;
            for (List<Ruta> list : grafoLogico.getAdjList().values()) {
                for (Ruta r : list) {
                    double d = distancePointToSegment(event.getX(), event.getY(),
                            r.getOrigen().getX(), r.getOrigen().getY(),
                            r.getDestino().getX(), r.getDestino().getY());
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

    // Oculta el placeholder al salir el mouse
    private void onMouseExited(MouseEvent event) {
        mostrarPlaceholder = false;
        dibujar();
    }

    // Selecciona la parada para arrastrar o editar al presionar el mouse
    private void onMousePressed(MouseEvent event) {
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

    // Mueve la parada mientras se arrastra el mouse
    private void onMouseDragged(MouseEvent event) {
        if (paradaArrastrada != null) {
            paradaArrastrada.setX(event.getX() - offsetX);
            paradaArrastrada.setY(event.getY() - offsetY);
            dragging = true;
            dibujar();
        }
    }

    // Al soltar el mouse, edita la parada o ruta seleccionada, o crea una nueva parada
    private void onMouseReleased(MouseEvent event) {
        if (paradaArrastrada != null) {
            if (!dragging) {
                mostrarDialogoEdicion(paradaArrastrada, primaryStage);
            }
            paradaArrastrada = null;
            paradaPresionada = null;
            dragging = false;
        } else {
            Ruta rutaClickeada = null;
            for (List<Ruta> list : grafoLogico.getAdjList().values()) {
                for (Ruta r : list) {
                    double d = distancePointToSegment(mouseX, mouseY,
                            r.getOrigen().getX(), r.getOrigen().getY(),
                            r.getDestino().getX(), r.getDestino().getY());
                    if (d < 5) {
                        rutaClickeada = r;
                        break;
                    }
                }
                if (rutaClickeada != null) break;
            }
            if (rutaClickeada != null) {
                mostrarDialogoEdicionRuta(rutaClickeada, primaryStage);
            } else {
                Parada nueva = new Parada(mouseX, mouseY, "");
                grafoLogico.agregarParada(nueva);
            }
            dibujar();
        }
    }


    private void dibujar() {
        gc.setFill(Color.web("#1A1A2E"));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    
        // Dibujar rutas
        for (Map.Entry<Parada, List<Ruta>> entry : grafoLogico.getAdjList().entrySet()) {
            for (Ruta r : entry.getValue()) {
                double x1 = r.getOrigen().getX();
                double y1 = r.getOrigen().getY();
                double x2 = r.getDestino().getX();
                double y2 = r.getDestino().getY();
    
                double d = distancePointToSegment(mouseX, mouseY, x1, y1, x2, y2);
                boolean hovered = d < 7;
                if (hovered) {
                    gc.setStroke(Color.LIGHTBLUE);
                    gc.setLineWidth(4);
                } else {
                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(2);
                }
                gc.strokeLine(x1, y1, x2, y2);
                drawArrow(x1, y1, x2, y2);
    
                String label = String.format("T: %.0fm\nD: %.0fM\nC: $%.0f", r.getTiempo(), r.getDistancia(), r.getCosto());
                String[] lines = label.split("\n");
                double midX = (x1 + x2) / 2;
                double midY = (y1 + y2) / 2;
                Font routeFont = Font.font("Arial", FontWeight.NORMAL, 14);
                gc.setFont(routeFont);
                gc.setFill(Color.YELLOW);
                double lineHeight = 14;
                double totalHeight = lines.length * lineHeight;
                for (int i = 0; i < lines.length; i++) {
                    Text text = new Text(lines[i]);
                    text.setFont(routeFont);
                    double tw = text.getLayoutBounds().getWidth();
                    gc.fillText(lines[i], midX - tw / 2, midY - totalHeight / 2 + i * lineHeight);
                }
            }
        }

        // Dibujar paradas
        for (Parada p : grafoLogico.getParadas()) {
            gc.setFill(Color.web("#FF7F7F"));
            gc.fillOval(p.getX() - RADIO, p.getY() - RADIO, RADIO * 2, RADIO * 2);
            if (!p.getNombre().isEmpty()) {
                Font boldFont = Font.font("Arial", FontWeight.BOLD, 16);
                gc.setFont(boldFont);
                gc.setFill(Color.WHITE);
                Text text = new Text(p.getNombre());
                text.setFont(boldFont);
                double textWidth = text.getLayoutBounds().getWidth();
                double textHeight = text.getLayoutBounds().getHeight();
                gc.fillText(p.getNombre(), p.getX() - textWidth / 2, p.getY() + textHeight / 4);
                gc.setFont(Font.font("Arial", 12));
            }
            boolean hovered = Math.hypot(p.getX() - mouseX, p.getY() - mouseY) <= RADIO;
            if (hovered) {
                gc.setLineWidth(2);
                gc.setStroke((p == paradaPresionada) ? Color.DARKGRAY : Color.WHITE);
                gc.strokeOval(p.getX() - RADIO, p.getY() - RADIO, RADIO * 2, RADIO * 2);
            }
        }

        if (mostrarPlaceholder) {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.setLineDashes(5);
            gc.strokeOval(mouseX - RADIO, mouseY - RADIO, RADIO * 2, RADIO * 2);
            gc.setLineDashes(0);
        }

        if (animating && animatedPath != null) {
            for (int i = 0; i < currentStep && i < animatedPath.size() - 1; i++) {
                Parada pa = animatedPath.get(i);
                Parada pb = animatedPath.get(i + 1);
                gc.setStroke(Color.ORANGE);
                gc.setLineWidth(6);
                gc.strokeLine(pa.getX(), pa.getY(), pb.getX(), pb.getY());
            }
            for (int i = 0; i <= currentStep && i < animatedPath.size(); i++) {
                Parada p = animatedPath.get(i);
                gc.setFill(Color.LIGHTGREEN);
                gc.fillOval(p.getX() - RADIO, p.getY() - RADIO, RADIO * 2, RADIO * 2);
                Font boldFont = Font.font("Arial", FontWeight.BOLD, 16);
                gc.setFont(boldFont);
                gc.setFill(Color.BLACK);
                Text text = new Text(p.getNombre());
                text.setFont(boldFont);
                double tw = text.getLayoutBounds().getWidth();
                double th = text.getLayoutBounds().getHeight();
                gc.fillText(p.getNombre(), p.getX() - tw / 2, p.getY() + th / 4);
                gc.setFont(Font.font("Arial", 12));
            }
        }

        updateInfoBox();
    }
    
    private void drawArrow(double x1, double y1, double x2, double y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double arrowLength = 10;
        double arrowAngle = Math.toRadians(20);
        double xArrow1 = x2 - arrowLength * Math.cos(angle - arrowAngle);
        double yArrow1 = y2 - arrowLength * Math.sin(angle - arrowAngle);
        double xArrow2 = x2 - arrowLength * Math.cos(angle + arrowAngle);
        double yArrow2 = y2 - arrowLength * Math.sin(angle + arrowAngle);
        gc.strokeLine(x2, y2, xArrow1, yArrow1);
        gc.strokeLine(x2, y2, xArrow2, yArrow2);
    }
    
    private double distancePointToSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return Math.hypot(px - x1, py - y1);
        }
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        double projX = x1 + t * dx;
        double projY = y1 + t * dy;
        return Math.hypot(px - projX, py - projY);
    }


    private void updateInfoBox() {
        infoBox.getChildren().clear();
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
    
        Button btnRuta = new Button("Calcular ruta");
        btnRuta.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnRuta.setOnAction(e -> showDijkstraDialog());
        infoBox.getChildren().add(btnRuta);
    }
    
    private Parada obtenerParadaCercaDe(double x, double y) {
        for (Parada p : grafoLogico.getParadas()) {
            if (Math.hypot(p.getX() - x, p.getY() - y) <= RADIO) {
                return p;
            }
        }
        return null;
    }

    private void mostrarDialogoEdicion(Parada p, Stage owner) {
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
        ListView<Parada> lvConexiones = new ListView<>();
        for (Parada other : grafoLogico.getParadas()) {
            if (!other.equals(p)) {
                lvConexiones.getItems().add(other);
            }
        }
        lvConexiones.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<Ruta> conexiones = grafoLogico.getAdjList().get(p);
        if (conexiones != null) {
            for (Parada other : lvConexiones.getItems()) {
                for (Ruta r : conexiones) {
                    if (r.getDestino().equals(other)) {
                        lvConexiones.getSelectionModel().select(other);
                        break;
                    }
                }
            }
        }
        lvConexiones.setStyle("-fx-control-inner-background: #333333; -fx-text-fill: white;");
    
        Button btnEliminar = new Button("Eliminar parada");
        btnEliminar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnEliminar.setOnAction(e -> {
            grafoLogico.eliminarParada(p);
            dialog.close();
            dibujar();
        });
    
        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnAceptar.setOnAction(e -> {
            p.setNombre(tfNombre.getText());
            // Nombre de la parada no vacio
            if(tfNombre.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "El nombre de la parada no puede estar vacío.");
                alert.showAndWait();
            return;
            }
            
            grafoLogico.getAdjList().remove(p);
            for (Parada destino : lvConexiones.getSelectionModel().getSelectedItems()) {
                grafoLogico.addRoute(p, destino, 1.0, 1.0, 1.0);
                grafoLogico.addRoute(destino, p, 1.0, 1.0, 1.0);
            }
            dialog.close();
            dibujar();
        });
    
        HBox hboxBtns = new HBox(10);
        hboxBtns.setAlignment(Pos.CENTER);
        hboxBtns.getChildren().addAll(btnEliminar, btnAceptar);
    
        vbox.getChildren().addAll(labelNombre, tfNombre, labelConexiones, lvConexiones, hboxBtns);
    
        Scene scene = new Scene(vbox, 300, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void mostrarDialogoEdicionRuta(Ruta ruta, Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Ruta");
    
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #1A1A2E;");
    
        Label lblInfo = new Label("Editar atributos de la ruta:");
        lblInfo.setTextFill(Color.WHITE);
    
        Label lblTiempo = new Label("Tiempo (m):");
        lblTiempo.setTextFill(Color.WHITE);
        TextField tfTiempo = new TextField(String.valueOf(ruta.getTiempo()));
        tfTiempo.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
    
        Label lblDistancia = new Label("Distancia (M):");
        lblDistancia.setTextFill(Color.WHITE);
        TextField tfDistancia = new TextField(String.valueOf(ruta.getDistancia()));
        tfDistancia.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
    
        Label lblCosto = new Label("Costo ($):");
        lblCosto.setTextFill(Color.WHITE);
        TextField tfCosto = new TextField(String.valueOf(ruta.getCosto()));
        tfCosto.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
    
        Button btnEliminar = new Button("Eliminar ruta");
        btnEliminar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnEliminar.setOnAction(e -> {
            grafoLogico.removeRoute(ruta.getOrigen(), ruta.getDestino());
            grafoLogico.removeRoute(ruta.getDestino(), ruta.getOrigen());
            dialog.close();
            dibujar();
        });
    
        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnAceptar.setOnAction(e -> {
            try {
                double tiempo = Double.parseDouble(tfTiempo.getText());
                double distancia = Double.parseDouble(tfDistancia.getText());
                double costo = Double.parseDouble(tfCosto.getText());

                 // VALIDACION ATRIBUTOS TIEMPO, DISTANCIA Y COSTO NO NEGATIVOS
                if (tiempo <= 0 || distancia <= 0 || costo < 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Los valores deben ser positivos.");
                    alert.showAndWait();
                    return;
                }
                
                ruta.tiempo = tiempo;
                ruta.distancia = distancia;
                ruta.costo = costo;
                List<Ruta> inversa = grafoLogico.getAdjList().get(ruta.getDestino());
                if (inversa != null) {
                    for (Ruta r : inversa) {
                        if (r.getDestino().equals(ruta.getOrigen())) {
                            r.tiempo = tiempo;
                            r.distancia = distancia;
                            r.costo = costo;
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ingrese valores numéricos válidos.");
                alert.showAndWait();
            }
            dialog.close();
            dibujar();
        });
    
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(btnEliminar, btnAceptar);
    
        vbox.getChildren().addAll(lblInfo, lblTiempo, tfTiempo, lblDistancia, tfDistancia, lblCosto, tfCosto, hbox);
    
        Scene scene = new Scene(vbox, 300, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showDijkstraDialog() {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Ruta más corta");
    
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #1A1A2E;");
    
        Label lblInicio = new Label("Parada de inicio:");
        lblInicio.setTextFill(Color.WHITE);
        ComboBox<Parada> cbInicio = new ComboBox<>();
        cbInicio.getItems().addAll(grafoLogico.getParadas());
    
        Label lblDestino = new Label("Parada de destino:");
        lblDestino.setTextFill(Color.WHITE);
        ComboBox<Parada> cbDestino = new ComboBox<>();
        cbDestino.getItems().addAll(grafoLogico.getParadas());
    
        Label lblCriterio = new Label("Criterio:");
        lblCriterio.setTextFill(Color.WHITE);
        ComboBox<String> cbCriterio = new ComboBox<>();
        cbCriterio.getItems().addAll("tiempo", "distancia", "costo");
        cbCriterio.setValue("tiempo");
    
        Button btnCalcular = new Button("Calcular");
        btnCalcular.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        btnCalcular.setOnAction(e -> {
            Parada inicio = cbInicio.getValue();
            Parada destino = cbDestino.getValue();
            String criterio = cbCriterio.getValue();
            if (inicio == null || destino == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Seleccione ambas paradas.");
                alert.showAndWait();
            } else {
                List<Parada> path = grafoLogico.dijkstra(inicio, destino, criterio);
                if (path == null || path.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "No se encontró ruta.");
                    alert.showAndWait();
                } else {
                    dialog.close();
                    animatePath(path);
                }
            }
        });
    
        vbox.getChildren().addAll(lblInicio, cbInicio, lblDestino, cbDestino, lblCriterio, cbCriterio, btnCalcular);
    
        Scene scene = new Scene(vbox, 300, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void animatePath(List<Parada> path) {
        animatedPath = path;
        currentStep = 0;
        animating = true;
        Timeline timeline = new Timeline();
        timeline.setCycleCount(path.size());
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(2), event -> {
            currentStep++;
            dibujar();
        }));
        timeline.setOnFinished(e -> {
            animating = false;
            Platform.runLater(() -> {
                StringBuilder sb = new StringBuilder();
                for (Parada p : path) {
                    sb.append(p.toString()).append(" -> ");
                }
                sb.setLength(sb.length() - 4);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ruta recorrida: " + sb.toString());
                alert.showAndWait();
                animatedPath = null;
                dibujar();
            });
        });
        timeline.play();
    }


}

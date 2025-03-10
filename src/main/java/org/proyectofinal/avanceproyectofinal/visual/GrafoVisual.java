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

}

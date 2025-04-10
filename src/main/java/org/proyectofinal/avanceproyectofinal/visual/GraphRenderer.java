package org.proyectofinal.avanceproyectofinal.visual;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.proyectofinal.avanceproyectofinal.logico.Grafo;
import org.proyectofinal.avanceproyectofinal.logico.Parada;
import org.proyectofinal.avanceproyectofinal.logico.Ruta;

import java.util.*;

public class GraphRenderer {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Grafo grafo;

    public GraphRenderer(Canvas canvas, Grafo grafo) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.grafo = grafo;
    }

    public void drawAll(double mouseX, double mouseY, boolean mostrarPlaceholder, Parada paradaPresionada, double radio, GraphAnimator animator) {
        gc.setFill(Color.web("#1A1A2E"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Parada> path = animator.getAnimatedPath();
        int currentStep = animator.getCurrentStep();

        Map<Parada, Parada> predecesoresBF = animator.getPredecesoresActuales();
        Set<Ruta> cicloNegativoBF = animator.tieneCicloNegativo() ? new HashSet<>(animator.getCicloNegativo()) : Collections.emptySet();

        List<Ruta> rutasKruskal = animator.getRutasActualesKruskal();


        for (Map.Entry<Parada, List<Ruta>> entry : grafo.getAdjList().entrySet()) {
            for (Ruta r : entry.getValue()) {
                drawLineWithArrow(
                        r.getOrigen().getX(), r.getOrigen().getY(),
                        r.getDestino().getX(), r.getDestino().getY(),
                        radio, Color.WHITE, 2
                );
            }
        }

        // Dibuja las rutas de Bellman-Ford en ROJO
        for (Ruta r : cicloNegativoBF) {
            drawLineWithArrow(
                    r.getOrigen().getX(), r.getOrigen().getY(),
                    r.getDestino().getX(), r.getDestino().getY(),
                    radio, Color.RED, 4
            );
        }

        // Dibuja las rutas de Bellman-Ford
        for (Map.Entry<Parada, Parada> entry : predecesoresBF.entrySet()) {
            Parada destino = entry.getKey();
            Parada origen = entry.getValue();

            if (origen != null) {
                Ruta mejorRuta = grafo.obtenerRuta(origen, destino);
                if (mejorRuta != null && !cicloNegativoBF.contains(mejorRuta)) {
                    drawLineWithArrow(
                            origen.getX(), origen.getY(),
                            destino.getX(), destino.getY(),
                            radio, Color.YELLOW, 4
                    );
                }
            }
        }

        // Dibuja las rutas de Kruskal
        for (Ruta r : rutasKruskal) {
            drawLineWithArrow(
                    r.getOrigen().getX(), r.getOrigen().getY(),
                    r.getDestino().getX(), r.getDestino().getY(),
                    radio, Color.ORANGE, 4
            );
        }

        // Dibuja las rutas de Prim
        List<Ruta> rutasPrim = animator.getRutasActualesPrim();
        for (Ruta r : rutasPrim) {
            drawLineWithArrow(
                    r.getOrigen().getX(), r.getOrigen().getY(),
                    r.getDestino().getX(), r.getDestino().getY(),
                    radio, Color.ORANGE, 4
            );
        }

        for (Parada p : grafo.getParadas()) {
            gc.setFill(Color.web("#FF7F7F"));
            gc.fillOval(p.getX() - radio, p.getY() - radio, radio * 2, radio * 2);

            asignarNombreParada(p);

            boolean hovered = Math.hypot(p.getX() - mouseX, p.getY() - mouseY) <= radio;
            if (hovered) {
                gc.setLineWidth(2);
                gc.setStroke((p == paradaPresionada) ? Color.DARKGRAY : Color.WHITE);
                gc.strokeOval(p.getX() - radio, p.getY() - radio, radio * 2, radio * 2);
                canvas.getScene().setCursor(javafx.scene.Cursor.HAND);
            }
        }

        if (mostrarPlaceholder) {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.setLineDashes(5);
            gc.strokeOval(mouseX - radio, mouseY - radio, radio * 2, radio * 2);
            gc.setLineDashes(0);
        }


            for (int i = 0; i < currentStep && i < path.size() - 1; i++) {
                drawLineWithArrow(
                        path.get(i).getX(), path.get(i).getY(),
                        path.get(i + 1).getX(), path.get(i + 1).getY(),
                        radio, Color.ORANGE, 5);
            }

            for (int i = 0; i <= currentStep && i < path.size(); i++) {
                Parada p = path.get(i);
                gc.setFill(Color.LIGHTGREEN);
                gc.fillOval(p.getX() - radio, p.getY() - radio, radio * 2, radio * 2);
                Font boldFont = Font.font("Arial", FontWeight.BOLD, 16);
                gc.setFont(boldFont);
                gc.setFill(Color.BLACK);
                Text text = new Text(p.getNombre());
                text.setFont(boldFont);
                double tw = text.getLayoutBounds().getWidth();
                double th = text.getLayoutBounds().getHeight();
                gc.fillText(p.getNombre(), p.getX() - tw / 2, p.getY() + th / 4);
            }



        for (Map.Entry<Parada, List<Ruta>> entry : grafo.getAdjList().entrySet()) {
            for (Ruta r : entry.getValue()) {
                double x1 = r.getOrigen().getX(), y1 = r.getOrigen().getY();
                double x2 = r.getDestino().getX(), y2 = r.getDestino().getY();

                String label = String.format(
                        "T: %ds\nD: %.0fm\nC: $%.0f\nCT: %d",
                        r.getTiempoSegundos(),
                        r.getDistanciaMetros(),
                        r.getCostoMonetario(),
                        r.getCantidadTransbordos()
                );

                drawMultiLineText(label, (x1 + x2) / 2, (y1 + y2) / 2);
            }
        }

    }

    private void asignarNombreParada(Parada p) {
        if (!p.getNombre().isEmpty()) {
            Font boldFont = Font.font("Arial", FontWeight.BOLD, 16);
            gc.setFont(boldFont);
            gc.setFill(Color.WHITE);
            Text text = new Text(p.getNombre());
            text.setFont(boldFont);
            double textWidth = text.getLayoutBounds().getWidth();
            double textHeight = text.getLayoutBounds().getHeight();
            gc.fillText(p.getNombre(), p.getX() - textWidth / 2, p.getY() + textHeight / 4);
        }
    }

    private void drawLineWithArrow(double x1, double y1, double x2, double y2, double radius, Color color, double width) {
        double dx = x2 - x1, dy = y2 - y1;
        double dist = Math.hypot(dx, dy);
        if (dist < 1e-9) return;
        double ratio = Math.max((dist - 2 * radius) / dist, 0);
        double x1p = x1 + (radius / dist) * dx;
        double y1p = y1 + (radius / dist) * dy;
        double x2p = x1 + (radius + ratio * dist) / dist * dx;
        double y2p = y1 + (radius + ratio * dist) / dist * dy;

        gc.setStroke(color);
        gc.setLineWidth(width);
        gc.strokeLine(x1p, y1p, x2p, y2p);


    }


    public double distancePointToSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1, dy = y2 - y1;
        if (dx == 0 && dy == 0) return Math.hypot(px - x1, py - y1);
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        double projX = x1 + t * dx, projY = y1 + t * dy;
        return Math.hypot(px - projX, py - projY);
    }

    private void drawMultiLineText(String text, double x, double y) {
        String[] lines = text.split("\n");
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.setFill(Color.YELLOW);
        double lineHeight = 16;
        double totalHeight = lines.length * lineHeight;
        double maxWidth = 0;
        for (String line : lines) {
            Text t = new Text(line);
            t.setFont(gc.getFont());
            maxWidth = Math.max(maxWidth, t.getLayoutBounds().getWidth());
        }
        double padding = 5;
        double rectX = x - maxWidth / 2 - padding;
        double rectY = y - totalHeight / 2 - padding;
        double rectW = maxWidth + 2 * padding;
        double rectH = totalHeight + 2 * padding;

        gc.setFill(Color.web("#1A1A2E"));
        gc.fillRect(rectX, rectY, rectW, rectH);
        gc.setFill(Color.YELLOW);

        for (int i = 0; i < lines.length; i++) {
            Text t = new Text(lines[i]);
            t.setFont(gc.getFont());
            double tw = t.getLayoutBounds().getWidth();
            gc.fillText(lines[i], x - tw / 2, y - totalHeight / 2 + (i + 1) * lineHeight - 4);
        }
    }
}

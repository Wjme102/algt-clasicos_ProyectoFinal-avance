package org.proyectofinal.avanceproyectofinal.visual;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.proyectofinal.avanceproyectofinal.logico.Grafo;
import org.proyectofinal.avanceproyectofinal.logico.Parada;
import org.proyectofinal.avanceproyectofinal.logico.Ruta;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GraphAnimator {

    // Animación de caminos (Dijkstra)
    private List<Parada> animatedPath;
    private int currentStep;
    private boolean animating;
    private boolean animationCompleted = false;

    // Animación de Bellman-Ford
    private List<Map<Parada, Parada>> pasosBF;
    private List<Ruta> cicloNegativo;
    private int pasoActual;
    Timeline timelineBF;

    // kruskal
    private List<List<Ruta>> pasosKruskal;
    private int pasoActualKruskal;
    Timeline timelineKruskal;

    // prim
    private List<List<Ruta>> pasosPrim;
    private int pasoActualPrim;
    Timeline timelinePrim;

    public GraphAnimator() {
        animatedPath = null;
        currentStep = 0;
        animating = false;
    }

    public void reset() {
        this.animatedPath = null;
        this.currentStep = 0;
        this.animating = false;
        this.pasosBF = null;
        this.cicloNegativo = null;
        this.pasoActual = 0;
        this.pasosKruskal = null;
        this.pasoActualKruskal = 0;
        this.pasosPrim = null;
        this.pasoActualPrim = 0;
        this.timelineKruskal = null;
        this.timelinePrim = null;
        this.timelineBF = null;
        this.animationCompleted = false;
        if (timelineBF != null) {
            timelineBF.stop();
        }
    }
    public void detenerAnimacionDijkstra(Runnable onDetener) {
        animating = false;
        animationCompleted = false;
        if (onDetener != null) {
            onDetener.run();
        }
    }
    // Animación de caminos (Dijkstra)
    public void animatePath(List<Parada> path, Runnable onFrame, Runnable onFinished) {
        this.animatedPath = path;
        this.currentStep = 0;
        this.animating = true;
        this.animationCompleted = false;

        Timeline timeline = new Timeline();
        timeline.setCycleCount(path.size());
        timeline.getKeyFrames().add(new javafx.animation.KeyFrame(Duration.seconds(0.4), event -> {
            currentStep++;
            if (onFrame != null) onFrame.run();
        }));

        timeline.setOnFinished(e -> {
            animating = false;
            if (onFinished != null) onFinished.run();
        });

        timeline.play();
    }


    // Animación de Bellman-Ford
    public void animarBellmanFord(Grafo.BellmanFordResult resultado, Runnable onFrame, Runnable onFinished) {
        this.pasosBF = resultado.pasosAnimacion();
        this.cicloNegativo = resultado.cicloNegativo();
        this.pasoActual = 0;
        this.animating = true;

        timelineBF = new Timeline();
        timelineBF.setCycleCount(pasosBF.size());
        timelineBF.getKeyFrames().add(new javafx.animation.KeyFrame(Duration.seconds(1), event -> {
            pasoActual++;
            if (onFrame != null) onFrame.run();
        }));

        timelineBF.setOnFinished(e -> {
            animating = false;
            if (onFinished != null) onFinished.run();
        });

        timelineBF.play();
    }

    public boolean tieneCicloNegativo() {
        return cicloNegativo != null && !cicloNegativo.isEmpty();
    }

    public List<Ruta> getCicloNegativo() {
        return cicloNegativo;
    }

    public Map<Parada, Parada> getPredecesoresActuales() {
        if (pasoActual > 0 && pasoActual <= pasosBF.size()) {
            return pasosBF.get(pasoActual - 1);
        }
        return Collections.emptyMap();
    }


    public void animarKruskal(Grafo.KruskalResult resultado, Runnable onFrame, Runnable onFinished) {
        this.pasosKruskal = resultado.pasosAnimacion();
        this.pasoActualKruskal = 0;
        this.animating = true;

        timelineKruskal = new Timeline();
        timelineKruskal.setCycleCount(pasosKruskal.size());
        timelineKruskal.getKeyFrames().add(new KeyFrame(Duration.seconds(0.7), e -> {
            pasoActualKruskal++;
            if (onFrame != null) onFrame.run();
        }));

        timelineKruskal.setOnFinished(e -> {
            animating = false;
            if (onFinished != null) onFinished.run();
        });

        timelineKruskal.play();
    }

    public List<Ruta> getRutasActualesKruskal() {
        if (pasosKruskal != null && pasoActualKruskal > 0 && pasoActualKruskal <= pasosKruskal.size()) {
            return pasosKruskal.get(pasoActualKruskal - 1);
        }
        return Collections.emptyList();
    }

    public void animarPrim(Grafo.PrimResult resultado, Runnable onFrame, Runnable onFinished) {
        this.pasosPrim = resultado.pasosAnimacion();
        this.pasoActualPrim = 0;
        this.animating = true;

        timelinePrim = new Timeline();
        timelinePrim.setCycleCount(pasosPrim.size());
        timelinePrim.getKeyFrames().add(new KeyFrame(Duration.seconds(0.7), e -> {
            pasoActualPrim++;
            if (onFrame != null) onFrame.run();
        }));

        timelinePrim.setOnFinished(e -> {
            animating = false;
            if (onFinished != null) onFinished.run();
        });

        timelinePrim.play();
    }

    public List<Ruta> getRutasActualesPrim() {
        if (pasosPrim != null && pasoActualPrim > 0 && pasoActualPrim <= pasosPrim.size()) {
            return pasosPrim.get(pasoActualPrim - 1);
        }
        return Collections.emptyList();
    }

    // Getters
    public List<Parada> getAnimatedPath() {
    if (animatedPath != null && currentStep > 0 && currentStep <= animatedPath.size()) {
            return animatedPath.subList(0, currentStep);
        }
        return Collections.emptyList();
    }
    public int getCurrentStep() { if (animatedPath != null && currentStep > 0 && currentStep <= animatedPath.size()) {
            return currentStep;
        }
        return -1;
    }
    public boolean isAnimating() { if (animatedPath != null && currentStep > 0 && currentStep <= animatedPath.size()) {
            return animating;
        }
        return false;
    }
    public boolean isAnimationCompleted() {if (animatedPath != null && currentStep > 0 && currentStep <= animatedPath.size()) {
            return animationCompleted;
        }
        return false;
    }

}
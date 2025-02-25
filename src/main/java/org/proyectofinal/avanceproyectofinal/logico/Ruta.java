package org.proyectofinal.avanceproyectofinal.logico;

import java.util.*;

public class Ruta {

    private Parada origen, destino;
    public double tiempo;
    public double distancia;
    public double costo;

    // constructor
    public Ruta(Parada origen, Parada destino, double tiempo, double distancia, double costo) {
        this.origen = origen;
        this.destino = destino;
        this.tiempo = tiempo;
        this.distancia = distancia;
        this.costo = costo;
    }

    // Getter
    public Parada getOrigen() {
        return origen;
    }

    public Parada getDestino() {
        return destino;
    }

    public double getTiempo() {
        return tiempo;
    }

    public double getDistancia() {
        return distancia;
    }

    public double getCosto() {
        return costo;
    }

    // Metodo para imprimir ruta en string
    @Override
    public String toString() {
        return (destino.getNombre() != null && !destino.getNombre().isEmpty())
                ? "-> " + destino.getNombre()
                : "-> (" + destino.getX() + ", " + destino.getY() + ")";
    }
}

package org.proyectofinal.avanceproyectofinal.logico;

import java.util.*;

public class Grafo {
  
    private List<Parada> paradas;
    private Map<Parada, List<Ruta>> adjList;

    public Grafo() {
        paradas = new ArrayList<>();
        adjList = new HashMap<>();
    }

    // Getters
    public List<Parada> getParadas() {
        return paradas;
    }

    public Map<Parada, List<Ruta>> getAdjList() {
        return adjList;
    }
  
}

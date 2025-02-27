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

      // Metodos

      // Agregar parada
      public void agregarParada(Parada p) {
        paradas.add(p);
    }

    // Eliminar parada
    public void eliminarParada(Parada p) {
        paradas.remove(p);
        adjList.remove(p);
        // Eliminar rutas donde el parametro p es destino
        for (List<Ruta> rutas : adjList.values()) {
            rutas.removeIf(r -> r.getDestino().equals(p));
        }
    }

    // Agregar ruta
    public void addRoute(Parada origen, Parada destino, double tiempo, double distancia, double costo) {
        Ruta ruta = new Ruta(origen, destino, tiempo, distancia, costo);
        adjList.computeIfAbsent(origen, k -> new ArrayList<>()).add(ruta);
    }

    // Eliminar ruta
    public void removeRoute(Parada origen, Parada destino) {
        List<Ruta> rutas = adjList.get(origen);
        if (rutas != null) {
            rutas.removeIf(r -> r.getDestino().equals(destino));
        }
    }

    // Contabilizar cuantas rutas tiene una parada
    public int getConnectionCount(Parada p) {
        List<Ruta> rutas = adjList.get(p);
        return (rutas != null) ? rutas.size() : 0;
    }

}

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

    // Camino más corto con Dijkstra
    public List<Parada> dijkstra(Parada inicio, Parada fin, String criterio) {
    Map<Parada, Double> dist = new HashMap<>();
    Map<Parada, Parada> prev = new HashMap<>();
    for (Parada p : paradas) {
        dist.put(p, Double.MAX_VALUE);
        prev.put(p, null);
    }
    dist.put(inicio, 0.0);

    // Crear cola de prioridad y añadir el inicio
    PriorityQueue<Parada> queue = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
    queue.add(inicio);

    // Procesar nodos con Dijkstra
    while (!queue.isEmpty()) {
        Parada u = queue.poll();
        if (u.equals(fin)) break;
        List<Ruta> out = adjList.get(u);
        if (out != null) {
            for (Ruta r : out) {
                Parada v = r.getDestino();
                double peso = 0;
                switch (criterio) {
                    case "tiempo": 
                        peso = r.getTiempo(); 
                        break;
                    case "distancia": 
                        peso = r.getDistancia(); 
                        break;
                    case "costo": 
                        peso = r.getCosto(); 
                        break;
                }
                double alt = dist.get(u) + peso;
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    queue.remove(v);
                    queue.add(v);
                }
            }
        }
    }
    
    // Reconstruir camino desde el destino hasta el inicio
    List<Parada> path = new ArrayList<>();
    for (Parada at = fin; at != null; at = prev.get(at)) {
        path.add(0, at);
    }
    
    // Verificar que el camino sea válido
    if (!path.isEmpty() && !path.get(0).equals(inicio)) {
        return null;
    }
    return path;
}



  

}

package org.proyectofinal.avanceproyectofinal.logico;

import java.util.*;
import java.io.Serializable;


public class Grafo implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Parada> paradas;
    private Map<Parada, List<Ruta>> adjList;

    public Grafo() {
        paradas = new ArrayList<>();
        adjList = new HashMap<>();
    }

    public List<Parada> getParadas() {
        return paradas;
    }

    public Map<Parada, List<Ruta>> getAdjList() {
        return adjList;
    }

    public void agregarParada(Parada p) {
        paradas.add(p);
    }

    public void eliminarParada(Parada p) {
        paradas.remove(p);
        adjList.remove(p);
        for (List<Ruta> rutas : adjList.values()) {
            rutas.removeIf(r -> r.getDestino().equals(p));
        }
    }

    public boolean estaConectado(Parada origen, Parada destino) {
        List<Ruta> rutas = adjList.get(origen);
        if (rutas != null) {
            for (Ruta r : rutas) {
                if (r.getDestino().equals(destino)) return true;
            }
        }
        return false;
    }



    public void agregarConexion(Parada origen, Parada destino) {
        Ruta rutaDirecta = new Ruta(origen, destino, 1, 1.0, 1.0, 1);
        adjList.computeIfAbsent(origen, k -> new ArrayList<>()).add(rutaDirecta);

        Ruta rutaInversa = new Ruta(destino, origen, 1, 1.0, 1.0, 1);
        adjList.computeIfAbsent(destino, k -> new ArrayList<>()).add(rutaInversa);
    }


    public void eliminarConexion(Parada origen, Parada destino) {
        removeRoute(origen, destino);
        removeRoute(destino, origen);
    }

    public void removeRoute(Parada origen, Parada destino) {
        List<Ruta> rutas = adjList.get(origen);
        if (rutas != null) {
            rutas.removeIf(r -> r.getDestino().equals(destino));
        }
    }

    public Ruta obtenerRuta(Parada origen, Parada destino) {
        List<Ruta> rutas = adjList.get(origen);
        if (rutas != null) {
            for (Ruta r : rutas) {
                if (r.getDestino().equals(destino)) {
                    return r;
                }
            }
        }
        return null;
    }

    public Ruta obtenerRutaInversa(Ruta ruta) {
        List<Ruta> rutaInversa = adjList.get(ruta.getDestino());
        if (rutaInversa != null) {
            for (Ruta r : rutaInversa) {
                if (r.getDestino().equals(ruta.getOrigen())) {
                    return r;
                }
            }
        }
        return null;
    }


    private double obtenerPeso(Ruta r, String criterio) {
        return switch (criterio) {
            case "tiempo" -> r.getTiempoSegundos();
            case "distancia" -> r.getDistanciaMetros();
            case "costo" -> r.getCostoMonetario();
            case "transbordos" -> r.getCantidadTransbordos();
            default -> r.getDistanciaMetros();
        };
    }

    public int getConnectionCount(Parada p) {
        List<Ruta> rutas = adjList.get(p);
        return (rutas != null) ? rutas.size() : 0;
    }

    public List<Parada> dijkstra(Parada inicio, Parada fin, String criterio) {
        Map<Parada, Double> dist = new HashMap<>();
        Map<Parada, Parada> prev = new HashMap<>();
        for (Parada p : paradas) {
            dist.put(p, Double.MAX_VALUE);
            prev.put(p, null);
        }
        dist.put(inicio, 0.0);

        PriorityQueue<Parada> queue = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        queue.add(inicio);

        while (!queue.isEmpty()) {
            Parada u = queue.poll();
            if (u.equals(fin)) break;
            List<Ruta> out = adjList.get(u);
            if (out != null) {
                for (Ruta r : out) {
                    Parada v = r.getDestino();
                    double peso = switch (criterio) {
                        case "tiempo" -> (double) r.getTiempoSegundos();
                        case "distancia" -> r.getDistanciaMetros();
                        case "transbordos" -> (double) r.getCantidadTransbordos();
                        default -> 0;
                    };
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
        List<Parada> path = new ArrayList<>();
        for (Parada at = fin; at != null; at = prev.get(at)) {
            path.add(0, at);
        }
        if (!path.isEmpty() && !path.get(0).equals(inicio)) {
            return null;
        }
        return path;
    }

    public record BellmanFordResult(
            List<Map<Parada, Parada>> pasosAnimacion,
            List<Ruta> cicloNegativo
    ) {}

    public BellmanFordResult bellmanFord(Parada inicio, String criterio) {
        Map<Parada, Double> dist = new HashMap<>();
        Map<Parada, Parada> prev = new HashMap<>();
        List<Map<Parada, Parada>> pasosAnimacion = new ArrayList<>();
        List<Ruta> cicloNegativo = new ArrayList<>();

        for (Parada p : paradas) {
            dist.put(p, Double.POSITIVE_INFINITY);
            prev.put(p, null);
        }
        dist.put(inicio, 0.0);

        // Relajaciones y captura de cada iteración para pasarlo
        for (int i = 0; i < paradas.size() - 1; i++) {
            boolean cambio = false;

            for (List<Ruta> rutas : adjList.values()) {
                for (Ruta ruta : rutas) {
                    Parada u = ruta.getOrigen();
                    Parada v = ruta.getDestino();
                    double peso = obtenerPeso(ruta, criterio);

                    if (dist.get(u) + peso < dist.get(v)) {
                        dist.put(v, dist.get(u) + peso);
                        prev.put(v, u);
                        cambio = true;
                    }
                }
            }

            pasosAnimacion.add(new HashMap<>(prev));
            if (!cambio) break;
        }

        // Detección del primer ciclo negativo
        for (List<Ruta> rutas : adjList.values()) {
            for (Ruta ruta : rutas) {
                Parada u = ruta.getOrigen();
                Parada v = ruta.getDestino();
                double peso = obtenerPeso(ruta, criterio);

                if (dist.get(u) + peso < dist.get(v)) {
                    cicloNegativo = recuperarCicloNegativo(prev, v, u);
                    return new BellmanFordResult(pasosAnimacion, cicloNegativo);
                }
            }
        }

        return new BellmanFordResult(pasosAnimacion, cicloNegativo);
    }

    private List<Ruta> recuperarCicloNegativo(Map<Parada, Parada> prev, Parada inicio, Parada fin) {
        Set<Parada> visitado = new HashSet<>();
        LinkedList<Ruta> ciclo = new LinkedList<>();
        Parada actual = fin;

        while (!visitado.contains(actual)) {
            visitado.add(actual);
            Parada anterior = prev.get(actual);
            if (anterior == null) break;

            Ruta r = obtenerRuta(anterior, actual);
            if (r != null) ciclo.addFirst(r);
            actual = anterior;
        }

        Ruta cierre = obtenerRuta(actual, fin);
        if (cierre != null) ciclo.addFirst(cierre);
        return ciclo;
    }

    public record FloydWarshallResult(
             double[][] distancias,
            Parada[][] predecesores
    ) {
       public double[][] getDistancias() {
            return distancias;
        }

        public Parada[][] getPredecesores() {
            return predecesores;
        }
    }

    public FloydWarshallResult floydWarshall(String criterio) {
        int n = paradas.size();
        double[][] dist = new double[n][n];
        Parada[][] next = new Parada[n][n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Double.POSITIVE_INFINITY);
            dist[i][i] = 0;
        }

        for (int i = 0; i < n; i++) {
            Parada u = paradas.get(i);
            List<Ruta> rutas = adjList.get(u);
            if (rutas != null) {
                for (Ruta r : rutas) {
                    int v = paradas.indexOf(r.getDestino());
                    dist[i][v] = obtenerPeso(r, criterio);
                    next[i][v] = r.getDestino();
                }
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        return new FloydWarshallResult(dist, next);
    }

    public List<Parada> reconstruirCaminoFW(FloydWarshallResult resultado, Parada origen, Parada destino) {
        int idxOrigen = paradas.indexOf(origen);
        int idxDestino = paradas.indexOf(destino);

        if (resultado.predecesores[idxOrigen][idxDestino] == null) return Collections.emptyList();

        List<Parada> camino = new ArrayList<>();
        camino.add(origen);
        while (!origen.equals(destino)) {
            origen = resultado.predecesores[paradas.indexOf(origen)][idxDestino];
            camino.add(origen);
        }
        return camino;
    }

    public record KruskalResult(List<List<Ruta>> pasosAnimacion) {}

    public KruskalResult kruskal(String criterio) {
        List<Ruta> todasRutas = new ArrayList<>();
        for (List<Ruta> lista : adjList.values()) {
            for (Ruta r : lista) {
                // Solo agregamos una dirección para evitar duplicados
                if (paradas.indexOf(r.getOrigen()) < paradas.indexOf(r.getDestino())) {
                    todasRutas.add(r);
                }
            }
        }

        todasRutas.sort(Comparator.comparingDouble(r -> obtenerPeso(r, criterio)));

        Map<Parada, Parada> parent = new HashMap<>();
        for (Parada p : paradas) {
            parent.put(p, p);
        }

        List<List<Ruta>> pasos = new ArrayList<>();
        List<Ruta> mst = new ArrayList<>();

        for (Ruta r : todasRutas) {
            Parada root1 = find(parent, r.getOrigen());
            Parada root2 = find(parent, r.getDestino());

            if (!root1.equals(root2)) {
                mst.add(r);
                parent.put(root1, root2);

                pasos.add(new ArrayList<>(mst));
            }
        }

        return new KruskalResult(pasos);
    }

    private Parada find(Map<Parada, Parada> parent, Parada p) {
        if (!parent.get(p).equals(p)) {
            parent.put(p, find(parent, parent.get(p)));
        }
        return parent.get(p);
    }

    public record PrimResult(List<List<Ruta>> pasosAnimacion) {}

    public PrimResult prim(String criterio) {
        if (paradas.isEmpty()) return new PrimResult(Collections.emptyList());

        Set<Parada> visitadas = new HashSet<>();
        PriorityQueue<Ruta> cola = new PriorityQueue<>(Comparator.comparingDouble(r -> obtenerPeso(r, criterio)));
        List<List<Ruta>> pasos = new ArrayList<>();
        List<Ruta> mst = new ArrayList<>();

        Parada inicio = paradas.get(0);
        visitadas.add(inicio);

        List<Ruta> conexiones = adjList.getOrDefault(inicio, new ArrayList<>());
        cola.addAll(conexiones);

        while (!cola.isEmpty() && mst.size() < paradas.size() - 1) {
            Ruta ruta = cola.poll();
            Parada destino = ruta.getDestino();

            if (visitadas.contains(destino)) continue;

            visitadas.add(destino);
            mst.add(ruta);
            pasos.add(new ArrayList<>(mst));

            for (Ruta r : adjList.getOrDefault(destino, new ArrayList<>())) {
                if (!visitadas.contains(r.getDestino())) {
                    cola.add(r);
                }
            }
        }

        return new PrimResult(pasos);
    }



}

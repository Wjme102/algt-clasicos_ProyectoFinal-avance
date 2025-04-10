package org.proyectofinal.avanceproyectofinal.logico;
import java.io.Serializable;

public class Ruta implements Serializable {
    private static final long serialVersionUID = 1L;
    private Parada origen;
    private Parada destino;
    private long tiempoSegundos;
    private double distanciaMetros;
    private double costoMonetario;
    private int cantidadTransbordos;

    public static final long TIEMPO_MAXIMO_SEGUNDOS = 86_400;
    public static final double DISTANCIA_MAXIMA_METROS = 1_000_000;
    public static final double COSTO_MAXIMO_MONETARIO = 10_000;

    public Ruta(Parada origen, Parada destino, long tiempoSegundos, double distanciaMetros, double costoMonetario, int cantidadTransbordos) {
        this.origen = origen;
        this.destino = destino;
        setTiempoSegundos(tiempoSegundos);
        setDistanciaMetros(distanciaMetros);
        setCostoMonetario(costoMonetario);
        this.cantidadTransbordos = cantidadTransbordos;
    }

    public Parada getOrigen() { return origen; }
    public Parada getDestino() { return destino; }
    public long getTiempoSegundos() { return tiempoSegundos; }
    public double getDistanciaMetros() { return distanciaMetros; }
    public double getCostoMonetario() { return costoMonetario; }
    public int getCantidadTransbordos() { return cantidadTransbordos; }

    public void setTiempoSegundos(long tiempoSegundos) {
        if (tiempoSegundos < 0)
            throw new IllegalArgumentException("El tiempo en segundos no puede ser negativo.");
        if (tiempoSegundos > TIEMPO_MAXIMO_SEGUNDOS)
            throw new IllegalArgumentException("El tiempo en segundos no puede exceder de " + TIEMPO_MAXIMO_SEGUNDOS + " segundos.");
        this.tiempoSegundos = tiempoSegundos;
    }

   public void setDistanciaMetros(double distanciaMetros) {
        if (distanciaMetros < 0)
            throw new IllegalArgumentException("La distancia en metros no puede ser negativa.");
        if (distanciaMetros > DISTANCIA_MAXIMA_METROS)
            throw new IllegalArgumentException("La distancia en metros no puede exceder de " + DISTANCIA_MAXIMA_METROS + " metros.");
        this.distanciaMetros = distanciaMetros;
    }

    public void setCostoMonetario(double costoMonetario) {
        if (costoMonetario > COSTO_MAXIMO_MONETARIO)
            throw new IllegalArgumentException("El costo monetario no puede exceder de " + COSTO_MAXIMO_MONETARIO + " $DOP");
        this.costoMonetario = costoMonetario;
    }

    public void setCantidadTransbordos(int cantidadTransbordos) {
        if (cantidadTransbordos < 0)
            throw new IllegalArgumentException("La cantidad de transbordos no puede ser negativa.");
        this.cantidadTransbordos = cantidadTransbordos;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "origen=" + (origen != null ? origen.getNombre() : "null") +
                ", destino=" + (destino != null ? destino.getNombre() : "null") +
                ", tiempo=" + tiempoSegundos + "s" +
                ", distancia=" + distanciaMetros + "m" +
                ", costo=$" + costoMonetario +
                '}';
    }
}

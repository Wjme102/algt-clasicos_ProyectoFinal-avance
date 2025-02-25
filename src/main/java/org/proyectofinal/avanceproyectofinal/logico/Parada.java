package org.proyectofinal.avanceproyectofinal.logico;

import java.util.*;

public class Parada {

    private final UUID id;
    private double x,y;
    private String nombre;

    // Constructor
    public Parada(double x, double y, String nombre){
        this.id=UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.nombre = nombre;
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getNombre() {
        return nombre;
    }

    // Setters
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Metodo para imprimir el objeto parada en string
    @Override
    public String toString() {
        return (nombre != null && !nombre.isEmpty()) ? nombre : String.format("(%.0f, %.0f)", x, y);
    }

    /* Compara si las paradas son iguales
    *  Recibe un objeto (en este caso una parada)
    * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parada)) return false;
        Parada p = (Parada) o;
        return id.equals(p.id);
    }

    // Genera un codigo hash unico para cada objeto parada
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

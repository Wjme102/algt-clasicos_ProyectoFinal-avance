package org.proyectofinal.avanceproyectofinal.logico;

import java.util.Objects;
import java.util.UUID;
import java.io.Serializable;

public class Parada implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private double x, y;
    private String nombre;

    public Parada(double x, double y, String nombre) {
        this.id = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.nombre = nombre;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public String getNombre() { return nombre; }
    public UUID getId() {
        return id;
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return (nombre != null && !nombre.isEmpty()) ? nombre : String.format("(%.0f, %.0f)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parada)) return false;
        Parada p = (Parada) o;
        return id.equals(p.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

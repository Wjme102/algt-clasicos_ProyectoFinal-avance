// Clase que maneja guardar y cargar redes
package org.proyectofinal.avanceproyectofinal.logico;

import javafx.scene.control.Alert;
import java.io.*;
import java.util.UUID;

public class GestorRedes {

    public static void guardarRed(Grafo grafo, String nombreRed, File archivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(nombreRed);
            oos.writeObject(UUID.randomUUID().toString());
            oos.writeObject(grafo);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Red guardada");
            alert.setHeaderText(null);
            alert.setContentText("La red \"" + nombreRed + "\" se guardó correctamente.");
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al guardar");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo guardar la red. Detalles: " + e.getMessage());
            alert.showAndWait();

        }
    }
    public static Grafo cargarRed(File archivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            String nombre = (String) ois.readObject();
            String id = (String) ois.readObject();
            Grafo grafo = (Grafo) ois.readObject();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Red cargada");
            alert.setHeaderText(null);
            alert.setContentText("La red \"" + nombre + "\" se ha cargado correctamente y reemplazó a la actual.");
            alert.showAndWait();

            return grafo;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al cargar");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar la red.");
            alert.showAndWait();
            return null;
        }
    }
}

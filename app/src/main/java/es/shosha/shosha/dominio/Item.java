package es.shosha.shosha.dominio;

import java.io.Serializable;

/**
 * Created by Jesús Iráizoz on 06/03/2017.
 */

public class Item  implements Serializable{
    private int id;
    private String nombre;
    private double precio;
    private int idLista;

    public Item() {
    }

    @Deprecated
    public Item(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public Item(int id, String nombre, double precio,int idLista) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.idLista=idLista;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                '}';
    }

    public int getIdLista() {
        return idLista;
    }

    public void setIdLista(int idLista) {
        this.idLista = idLista;
    }
}

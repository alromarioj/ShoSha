package es.shosha.shosha.dominio;

import java.io.Serializable;

/**
 * Created by Jesús Iráizoz on 02/03/2017.
 */

public class Usuario implements Serializable{
    private int id;
    private String nombre;
    private String email;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String email) {

        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    public int getId() {
        return id;
    }
    public String getStringId(){
        return String.valueOf(id);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
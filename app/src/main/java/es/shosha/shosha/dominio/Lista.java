package es.shosha.shosha.dominio;

/**
 * Created by Jesús Iráizoz on 28/02/2017.
 */

public class Lista {
    private String id;
    private String nombre;
    private String propietario;
    private boolean estado;

    public Lista(String id, String nombre, String propietario, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.propietario = propietario;
        this.estado = estado;
    }

    public Lista() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}

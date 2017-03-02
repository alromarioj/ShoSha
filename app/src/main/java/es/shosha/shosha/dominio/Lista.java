package es.shosha.shosha.dominio;

/**
 * Created by Jesús Iráizoz on 28/02/2017.
 */

public class Lista {
    private String id;
    private String nombre;
    private Usuario propietario;
    private boolean estado;

    public Lista(String id, String nombre, Usuario propietario, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.propietario = propietario;
        this.estado = estado;
    }

    public Lista() {
    }

    @Override
    public String toString() {
        return "[id = " + this.getId() + ", nombre = " + this.getNombre() + ", propietario = " + this.getPropietario() + ", estado = " + (this.isEstado() ? "activa" : "inactiva") + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
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

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}

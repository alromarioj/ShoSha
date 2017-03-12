package es.shosha.shosha.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jesús Iráizoz on 28/02/2017.
 */

public class Lista {
    private String id;
    private String nombre;
    private Usuario propietario;
    private boolean estado;
    private List<Item> items;
    private List<Usuario> participantes;

    public Lista(String id, String nombre, Usuario propietario, boolean estado, List<Item> items, List<Usuario> participantes) {
        this.id = id;
        this.nombre = nombre;
        this.propietario = propietario;
        this.estado = estado;
        this.items = items;
        this.participantes = participantes;
    }

    public Lista() {
        this.items = new ArrayList<Item>();
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

    public void addItem(Item i) {
        this.items.add(i);
    }

    public Item getItem(int index) {
        return this.items.get(index);
    }

    /**
     * Método para obtener un ítem de la lista actual.
     * Si el valor de <code>id</code> es <code>true</code>, entonces se buscará en la lista por identificador, de lo contrario será por nombre
     *
     * @param valor Código identificador o nombre.
     * @param id    Booleano para buscar por id (<code>true</code>) o por nombre (<code>false</code>).
     * @return Si existe, el ítem solicitado, de lo contrario <code>null</code>.
     */
    public Item getItem(String valor, boolean id) {
        for (Item i : this.items) {
            if (id && i.getId().equals(valor))
                return i;
            else if (!id && i.getNombre().equals(valor))
                return i;
        }
        return null;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }

    public void setListaItems(List<Item> items){
        this.items = items;
    }
}

package es.shosha.shosha.dominio;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jesús Iráizoz on 28/02/2017.
 */
@SuppressWarnings("serial")
public class Lista implements Serializable {
    private String id;
    private String nombre;
    private Usuario propietario;
    private boolean estado;
    private List<Usuario> participantes;
    private Drawable imagen = null;
    private List<Item> items;

    /**
     * @param id
     * @param nombre
     * @param propietario
     * @param estado
     */
    public Lista(String id, String nombre, Usuario propietario, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.propietario = propietario;
        this.estado = estado;
        this.items = new ArrayList<Item>();
    }

    /**
     * @param id
     * @param nombre
     * @param propietario
     * @param estado
     * @param items
     * @param participantes
     */
    public Lista(String id, String nombre, Usuario propietario, boolean estado, List<Item> items, List<Usuario> participantes) {
        this.id = id;
        this.nombre = nombre;
        this.propietario = propietario;
        this.estado = estado;
        this.items = items;
        this.participantes = participantes;
    }

    /**
     * @param id
     * @param nombre
     * @param propietario
     * @param estado
     * @param participantes
     * @param imagen
     */
    public Lista(String id, String nombre, Usuario propietario, boolean estado, List<Usuario> participantes, Drawable imagen) {
        this.id = id;
        this.nombre = nombre;
        this.propietario = propietario;
        this.estado = estado;
        this.items = new ArrayList<Item>();
        this.participantes = participantes;
        this.imagen = imagen;
    }

    /**
     *
     */
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

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }

    public void setImagen(Drawable imagen) {
        this.imagen = imagen;
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

    /**
     * Obtiene el objeto <code>Item</code> contenido en la posición <code>index</code> de la lista de items.
     *
     * @param index Posición del objeto en la lista
     * @return Objeto <code>Item</code> con posición <code>index</code>.
     */
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

    public void setListaItems(List<Item> lItem) {
        this.items = lItem;
    }

    public List<Item> getListaItems() {
        return this.items;
    }

    public int getNumParticipantes() {
        return this.participantes.size();
    }

    public Drawable getImagen() {
        return imagen;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}

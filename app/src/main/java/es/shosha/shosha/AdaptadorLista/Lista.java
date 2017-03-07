package es.shosha.shosha.AdaptadorLista;

import android.graphics.drawable.Drawable;

public class Lista {
    private String titulo;
    private String numPartic;
    private Drawable imagen=null;

    public Lista(String titulo, String numPartic, Drawable imagen) {
        super();
        this.titulo = titulo;
        this.numPartic = numPartic;
        this.imagen = imagen;
    }
    public Lista(String titulo, String numPartic) {
        super();
        this.titulo = titulo;
        this.numPartic = numPartic;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNumPartic() {
        return numPartic;
    }

    public void setNumPartic(String numPartic) {
        this.numPartic = numPartic;
    }

    public Drawable getImagen() {
        return imagen;
    }

    public void setImagen(Drawable imagen) {
        this.imagen = imagen;
    }
}

package es.shosha.shosha.Adaptadores.Productos;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.dominio.Item;
import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.ItemFB;
import es.shosha.shosha.persistencia.ListaFB;
import es.shosha.shosha.persistencia.Notificacion;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by inhernan on 23/03/2017.
 */

public class ProductosAdapter extends RecyclerView.Adapter {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private RecyclerViewOnItemClickListener oicl;
    List<Item> items;
    List<Item> itemsPendingRemoval;
    int lastInsertedIndex = 0; // so we can add some more items for testing purposes
    boolean undoOn = true; // is undo on, you can turn it on from the toolbar menu

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Item, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    Context contexto;
    int idLista;

    public ProductosAdapter(List<Item> productos, @NonNull RecyclerViewOnItemClickListener oicl, Context contexto, int idLista) {
        items = productos;
        itemsPendingRemoval = new ArrayList<>();
        this.oicl = oicl;
        this.contexto = contexto;
        this.idLista = idLista;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductosViewHolder(parent, oicl, idLista);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ProductosViewHolder viewHolder = (ProductosViewHolder) holder;
        final Item item = items.get(position);
        viewHolder.comprado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ITEM","entra");
                    /*new ItemPers(MyApplication.getAppContext()).execute("buy",
                            String.valueOf(idLista),
                            String.valueOf(item.getId()),//Id del producto seleccionado
                            String.valueOf(MyApplication.getUser().getId()));*/
                if(viewHolder.comprado.isChecked()){
                    item.setComprado(true);
                    List<String> params=new ArrayList<String>();
                    int idUsuario=MyApplication.getUser().getId();
                    //Obtener usuarios que participen en la lista o propietarios de la misma
                    AdaptadorBD abd=new AdaptadorBD(contexto);
                    abd.open();
                    List<Usuario> l=abd.getParticipantes(idLista);
                    Lista lista=abd.obtenerLista(idLista,idUsuario);
                    l.add(lista.getPropietario());
                    abd.close();
                    params.add(lista.getNombre());
                    //Escoger usuarios que no sean el usuario actual
                    for (Usuario u:l) {
                        if(idUsuario!=u.getId()){
                            params.add(String.valueOf(u.getId()));
                        }
                    }

                    new Notificacion().execute(params);//Enviar notificación

                }
                else{
                    item.setComprado(false);
                }
                ItemFB.insertaItemFB(item, false);


            }
        });

        if (itemsPendingRemoval.contains(item)) {
            // we need to show the "undo" state of the row
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            viewHolder.nombre.setVisibility(View.GONE);
            viewHolder.precio.setVisibility(View.GONE);
            viewHolder.comprado.setVisibility(View.GONE);

            viewHolder.cantidad.setVisibility(View.GONE);
            viewHolder.undoButton.setVisibility(View.VISIBLE);
            viewHolder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(item);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(items.indexOf(item));
                }
            });
        } else {
            // we need to show the "normal" state
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            viewHolder.nombre.setVisibility(View.VISIBLE);
            viewHolder.precio.setVisibility(View.VISIBLE);
            viewHolder.comprado.setVisibility(View.VISIBLE);
            viewHolder.cantidad.setVisibility(View.VISIBLE);
            viewHolder.nombre.setText(item.getNombre());
            viewHolder.precio.setText(item.getPrecio() + " €");
            viewHolder.cantidad.setText(" | " + String.valueOf(item.getCantidad()));
            viewHolder.undoButton.setVisibility(View.GONE);
            viewHolder.undoButton.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<Item> category) {
        for (int i = 0; i < category.size(); i++) {
            items.add(category.get(i));
        }
    }

    public Item getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        final Item item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(items.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        Item item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
            /*AdaptadorBD abd = new AdaptadorBD(contexto);
            abd.open();
            //Eliminar producto de BD local
            abd.eliminarItem(idLista, item.getId());
            //Eliminar de BD remota
            new ItemPers(MyApplication.getAppContext()).execute("delete", String.valueOf(idLista), String.valueOf(item.getId()));
            abd.close();*/

            ItemFB.borrarItemFB(item.getId());

            //Toast.makeText(ListaProductos.this, "Eliminando producto ", Toast.LENGTH_SHORT).show();
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        Item item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }
}


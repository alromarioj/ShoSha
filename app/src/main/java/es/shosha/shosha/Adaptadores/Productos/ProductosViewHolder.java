package es.shosha.shosha.Adaptadores.Productos;

/**
 * Created by inhernan on 23/03/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.R;
import es.shosha.shosha.persistencia.ItemPers;

/**
 * ViewHolder capable of presenting two states: "normal" and "undo" state.
 */
public class ProductosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView precio;
    TextView nombre;
    TextView cantidad;
    Button undoButton;
    CheckBox comprado;
    private RecyclerViewOnItemClickListener oicl;

    public ProductosViewHolder(ViewGroup parent, RecyclerViewOnItemClickListener oicl, int idLista) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false));
        final int lista=idLista;
        nombre = (TextView) itemView.findViewById(R.id.nombreP);
        precio=(TextView)itemView.findViewById(R.id.precioP);
        cantidad=(TextView)itemView.findViewById(R.id.cantidadP);
        undoButton = (Button) itemView.findViewById(R.id.undo_button);
        comprado=(CheckBox) itemView.findViewById(R.id.comprado);
        comprado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ItemPers(MyApplication.getAppContext()).execute("buy",
                        String.valueOf(lista),
                        String.valueOf(itemView.getId()),//"1",//Id del producto seleccionado
                        String.valueOf(MyApplication.getUser().getId()));

            }
        });
        itemView.setOnClickListener(this);
        this.oicl=oicl;
    }

    @Override
    public void onClick(View v) {
        System.out.println("NULO?"+(oicl == null));
        oicl.onClick(v,getAdapterPosition());
    }
}
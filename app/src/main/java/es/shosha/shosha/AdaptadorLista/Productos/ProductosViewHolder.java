package es.shosha.shosha.AdaptadorLista.Productos;

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
import es.shosha.shosha.R;

/**
 * ViewHolder capable of presenting two states: "normal" and "undo" state.
 */
public class ProductosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView precio;
    TextView nombre;
    Button undoButton;
    CheckBox comprado;
    private RecyclerViewOnItemClickListener oicl;

    public ProductosViewHolder(ViewGroup parent, RecyclerViewOnItemClickListener oicl) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false));
        nombre = (TextView) itemView.findViewById(R.id.nombreP);
        precio=(TextView)itemView.findViewById(R.id.precioP);
        undoButton = (Button) itemView.findViewById(R.id.undo_button);
        comprado=(CheckBox) itemView.findViewById(R.id.comprado);
        itemView.setOnClickListener(this);
        this.oicl=oicl;
    }

    @Override
    public void onClick(View v) {
        oicl.onClick(v,getAdapterPosition());
    }
}
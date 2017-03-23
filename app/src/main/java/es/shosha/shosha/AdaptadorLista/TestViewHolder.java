package es.shosha.shosha.AdaptadorLista;

/**
 * Created by inhernan on 23/03/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import es.shosha.shosha.R;

/**
 * ViewHolder capable of presenting two states: "normal" and "undo" state.
 */
public class TestViewHolder extends RecyclerView.ViewHolder {

    TextView precio;
    TextView nombre;
    Button undoButton;

    public TestViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false));
        nombre = (TextView) itemView.findViewById(R.id.nombreP);
        precio=(TextView)itemView.findViewById(R.id.precioP);
        undoButton = (Button) itemView.findViewById(R.id.undo_button);
    }

}
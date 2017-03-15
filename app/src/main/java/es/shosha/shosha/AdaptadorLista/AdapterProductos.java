package es.shosha.shosha.AdaptadorLista;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.shosha.shosha.R;
import es.shosha.shosha.dominio.Item;
import es.shosha.shosha.dominio.Lista;


public class AdapterProductos extends BaseAdapter {

    protected Activity activity;
    protected List<Item> items;

    public AdapterProductos(Activity activity, List<Item> productos) {
        this.activity = activity;
        this.items = productos;
    }

    @Override
    public int getCount() {
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

    @Override
    public Item getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_producto, null);
        }

        Item dir = items.get(position);

        TextView nombre = (TextView) v.findViewById(R.id.nombreP);
        nombre.setText(dir.getNombre());

        TextView precio = (TextView) v.findViewById(R.id.precioP);
        double p=dir.getPrecio();
        precio.setText(dir.getPrecio()==0?"":p+" €");

        return v;
    }
}

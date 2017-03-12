package es.shosha.shosha.AdaptadorLista;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import es.shosha.shosha.dominio.Lista;
import java.util.ArrayList;
import java.util.List;

import es.shosha.shosha.R;


public class AdapterLista extends BaseAdapter {

    protected Activity activity;
    protected List<Lista> items;

    public AdapterLista(Activity activity, List<Lista> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<Lista> category) {
        for (int i = 0; i < category.size(); i++) {
            items.add(category.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
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
            v = inf.inflate(R.layout.item_lista, null);
        }

        Lista dir = items.get(position);

        TextView titulo = (TextView) v.findViewById(R.id.tituloLista);
        titulo.setText(dir.getNombre());

        TextView participantes = (TextView) v.findViewById(R.id.numParticipantes);
        participantes.setText(Integer.toString(dir.getParticipantes().size()));
        Drawable im=null;
        if(im!=null){
            ImageView imagen = (ImageView) v.findViewById(R.id.iconoLista);
            imagen.setImageDrawable(im);
        }
        return v;
    }
}

package es.shosha.shosha.AdaptadorLista;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

import es.shosha.shosha.R;
import es.shosha.shosha.dominio.Item;

public class AdaptadorProductosL extends BaseSwipeAdapter {

    private Context mContext;
    protected List<Item> items;
    protected Activity activity;

    public AdaptadorProductosL(Activity a, List<Item> productos) {

        this.items=productos;
        activity=a;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_producto;
    }
    @Override
    public View generateView(int position, ViewGroup parent) {
        View v;
        LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inf.inflate(R.layout.swipe_producto, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(R.id.swipe_producto);
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                //YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    @Override
    public void fillValues(int position, View v) {
        Item dir = items.get(position);
        TextView nombre = (TextView) v.findViewById(R.id.nombreP);
        nombre.setText(dir.getNombre());

        TextView precio = (TextView) v.findViewById(R.id.precioP);
        double p=dir.getPrecio();
        precio.setText(dir.getPrecio()==0?"":p+" €");
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public Item getItem(int arg0) {
        return items.get(arg0);
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
}

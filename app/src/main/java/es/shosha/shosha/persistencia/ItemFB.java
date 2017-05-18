package es.shosha.shosha.persistencia;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.dominio.Item;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 17/05/2017.
 */

public class ItemFB {

    private static final String ITEM = "item";
    public static final String LOG_MSG = "ItemFB";
    public static long cuenta = 0;

    public ItemFB() {
        DatabaseReference sbUsuario =
                FirebaseDatabase.getInstance().getReference()
                        .child(ITEM);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(LOG_MSG, "onChildAdded: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                Item i = parser(dataSnapshot);
                lastChild();
                AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
                abd.open();
                abd.insertarItem(i);
                abd.close();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(LOG_MSG, "onChildChanged: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                Item i = parser(dataSnapshot);
                AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
                abd.open();
                abd.updateItem(i);
                abd.close();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(LOG_MSG, "onChildRemoved: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                Item i = parser(dataSnapshot);
                lastChild();
                AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
                abd.open();
                abd.eliminarItem(i.getIdLista(), i.getId());
                abd.close();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(LOG_MSG, "onChildMoved: " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_MSG, "Error!", databaseError.toException());
            }
        };

        sbUsuario.addChildEventListener(childEventListener);
    }

    private Item parser(DataSnapshot data) {
        Item itm = null;
        itm = data.getValue(Item.class);
        itm.setId(Integer.parseInt(data.getKey()));
        return itm;
    }

    /**
     * Inserta un item en la base de datos remota.
     * @param it Item a añadir
     * @param nuevo Indica si se añade un nuevo item o uno ya existente
     */
    public static void insertaItemFB(Item it, boolean nuevo) {
        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference()
                        .child(ITEM);

        long i = cuenta + 1;
        if (nuevo)
            dbRef.child(String.valueOf(i)).setValue(it);
        else
            dbRef.child(String.valueOf(it.getId())).setValue(it);
    }

    public static void borrarItemFB(int idItem) {
        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference()
                        .child(ITEM)
                        .child(String.valueOf(idItem));
        dbRef.removeValue();
    }

    public static void lastChild() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference messages = database
                .getReference()
                .child(ITEM); // change this to your databae ref

        messages.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> it = dataSnapshot.getChildren();
                    for (DataSnapshot snapshot : it) {
                        String key = snapshot.getKey();
                        cuenta = Long.parseLong(key);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

package es.shosha.shosha.persistencia;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 17/05/2017.
 */

public class ParticipaFB {
    private static final String PARTICIPA = "participa";
    public static final String LOG_MSG = "ParticipaFB";

    public ParticipaFB() {
        DatabaseReference sbParticipa =
                FirebaseDatabase.getInstance().getReference()
                        .child(PARTICIPA);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(LOG_MSG, "onChildAdded: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                parser(dataSnapshot, true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(LOG_MSG, "onChildChanged: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                parser(dataSnapshot, true);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(LOG_MSG, "onChildRemoved: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                parser(dataSnapshot, false);
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

        sbParticipa.addChildEventListener(childEventListener);
    }

    private void parser(DataSnapshot data, boolean participa) {
        int idLista = Integer.valueOf(data.getKey());
        int idUsuario;

        Iterable<DataSnapshot> it = data.getChildren();

        AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
        abd.open();

        for (DataSnapshot ds : it) {
            idUsuario = Integer.parseInt(ds.getKey());
            if (ds.getValue(boolean.class))
                abd.insertarParticipa(idUsuario, idLista, participa);
        }

        abd.close();
    }

    public static void insertaParticipaFB(int idUsr, int idLista) {
        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference()
                        .child(PARTICIPA);

        dbRef.child(String.valueOf(String.valueOf(idLista))).child(String.valueOf(idUsr)).setValue(true);
    }

    public static void borrarParticipaFB(int idLista) {
        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference()
                        .child(PARTICIPA)
                        .child(String.valueOf(idLista))
                        .child(String.valueOf(MyApplication.getUser().getId()));
        dbRef.removeValue();
    }
}
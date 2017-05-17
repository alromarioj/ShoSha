package es.shosha.shosha.persistencia;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 15/05/2017.
 */

public class UsuarioFB {
    private static final String USUARIO = "usuario";
    public static final String LOG_MSG = "UsuarioFB";

    public UsuarioFB() {
        DatabaseReference sbUsuario =
                FirebaseDatabase.getInstance().getReference()
                        .child(USUARIO);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(LOG_MSG, "onChildAdded: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                Usuario u = parser(dataSnapshot);
                AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
                abd.open();
                abd.insertarUsuario(u);
                abd.close();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(LOG_MSG, "onChildChanged: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                Usuario u = parser(dataSnapshot);
                AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
                abd.open();
                abd.updateUsuario(u);
                abd.close();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(LOG_MSG, "onChildRemoved: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                Usuario u = parser(dataSnapshot);
                AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
                abd.open();
                abd.eliminarUsuario(u);
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

    private Usuario parser(DataSnapshot data) {
        Usuario usr = null;
        usr = data.getValue(Usuario.class);
        usr.setId(Integer.parseInt(data.getKey()));
        return usr;
    }

    public static void insertaUsuarioFB(Usuario u) {
        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference()
                        .child(USUARIO);

        dbRef.child(String.valueOf(u.getId())).setValue(u);
    }

    public static Usuario obtenerUsuario(int id) {
        DatabaseReference sbUsuario =
                FirebaseDatabase.getInstance().getReference()
                        .child(USUARIO)
                        .child(String.valueOf(id));
        final Usuario[] u = {null};

        sbUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                u[0] = dataSnapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_MSG, "Error!", databaseError.toException());
            }
        });
        return u[0];
    }
}

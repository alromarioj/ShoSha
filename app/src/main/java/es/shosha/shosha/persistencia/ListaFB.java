package es.shosha.shosha.persistencia;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 15/05/2017.
 */

public class ListaFB {

    private static final String LISTA = "lista";
    public static final String LOG_MSG = "ListaFB";
    public static long cuenta = 0;

    public ListaFB() {
        DatabaseReference sbUsuario =
                FirebaseDatabase.getInstance().getReference()
                        .child(LISTA);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(LOG_MSG, "onChildAdded: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                Lista l = parser(dataSnapshot);
                cuenta++;
                AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
                abd.open();
                abd.insertarLista(l);
                abd.close();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(LOG_MSG, "onChildChanged: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                Lista l = parser(dataSnapshot);
                AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
                abd.open();
                abd.updateLista(l, l.getNombre());
                abd.close();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(LOG_MSG, "onChildRemoved: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
                Lista l = parser(dataSnapshot);
                cuenta--;
                AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
                abd.open();
                abd.eliminarLista(l);
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

    private Lista parser(DataSnapshot data) {
        Lista lst = null;
        lst = data.getValue(Lista.class);
        lst.setId(Integer.valueOf(data.getKey()));
        int idUsr = data.child("propietario").getValue(int.class);
        lst.setPropietario(new Usuario(idUsr, "", ""));

        return lst;
    }

    public static long insertaListaFB(Lista l, boolean nuevo) {
        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference()
                        .child(LISTA);

        long i = cuenta + 1;

        class aux {
            String nombre;
            boolean estado;
            int propietario;
            String codigoQR;

            public aux(String nombre, boolean estado, int propietario, String codigoQR) {
                this.nombre = nombre;
                this.estado = estado;
                this.propietario = propietario;
                this.codigoQR = codigoQR;
            }

            public String getNombre() {
                return nombre;
            }

            public void setNombre(String nombre) {
                this.nombre = nombre;
            }

            public boolean isEstado() {
                return estado;
            }

            public void setEstado(boolean estado) {
                this.estado = estado;
            }

            public int getPropietario() {
                return propietario;
            }

            public void setPropietario(int propietario) {
                this.propietario = propietario;
            }

            public String getCodigoQR() {
                return codigoQR;
            }

            public void setCodigoQR(String codigoQR) {
                this.codigoQR = codigoQR;
            }
        }

        if (nuevo)
            dbRef.child(String.valueOf(i)).setValue(new aux(l.getNombre(), l.isEstado(), l.getPropietario().getId(), l.getCodigoQR()));
        else
            dbRef.child(String.valueOf(l.getId())).setValue(new aux(l.getNombre(), l.isEstado(), l.getPropietario().getId(), l.getCodigoQR()));

        return i;

    }

}

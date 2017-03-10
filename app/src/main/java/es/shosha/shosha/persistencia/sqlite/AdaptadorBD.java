package es.shosha.shosha.persistencia.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.shosha.shosha.dominio.Item;
import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.dominio.Usuario;

/**
 * Created by Jesús Iráizoz on 06/03/2017.
 */

public class AdaptadorBD {
    private static final String NOMBRE_BD = "ShoSha";
    private static final int VERSION_BD = 1;

    private static final String TB_USUARIO = "usuario";
    private static final String TB_LISTA = "lista";
    private static final String TB_PARTICIPA = "participa";
    private static final String TB_ITEM = "item";
    private static final String ID = "id";
    private static final String NOMBRE = "nombre";
    private static final String USR_EMAIL = "email";
    private static final String LST_PROP = "propietario";
    private static final String LST_ESTADO = "estado";
    private static final String ITM_PRECIO = "precio";
    private static final String IDLISTA = "idLista";
    private static final String PPA_IDUSR = "idUsuario";
    private static final String PPA_ACTIVO = "activo";

    private static final String ID_LOG = "USO DE BD";

    private final Context contexto;

    private SQLiteDatabase bdatos;

    private AuxiliarBD auxBD;


    public AdaptadorBD(Context contexto) {
        this.contexto = contexto;
        auxBD = new AuxiliarBD(contexto);
    }

    private static class AuxiliarBD extends SQLiteOpenHelper {

        private Context cntx;

        public AuxiliarBD(Context context) {
            super(context, NOMBRE_BD, null, VERSION_BD);
            this.cntx = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                InputStream is = cntx.getAssets().open("ShoSha.sql");

                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String leido = "", sql = "";
                while ((leido = br.readLine()) != null) {
                    //      System.out.println(" > " + leido);
                    if (!leido.endsWith(";")) {
                        sql += leido;
                    } else {
                        sql += leido;
                        System.out.println("     > " + sql);
                        db.execSQL(sql);
                        sql = "";
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(ID_LOG, "Acualiza la versión: " + oldVersion + " a la versión: " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TB_USUARIO);
            onCreate(db);
        }
    }

    public AdaptadorBD open() throws SQLException {
        bdatos = auxBD.getWritableDatabase();
        return this;
    }

    public void close() {
        auxBD.close();
    }

    public long insertarUsuario(String id, String nombre, String email) {
        bdatos.beginTransaction();
        long res;
        try {
            ContentValues valores = new ContentValues();
            valores.put(ID, id);
            valores.put(NOMBRE, nombre);
            valores.put(USR_EMAIL, email);
            res = bdatos.insert(TB_USUARIO, null, valores);

            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }

        return res;
    }

    public long insertarLista(String id, String nombre, String propietario, String estado) {
        bdatos.beginTransaction();
        long res;
        try {
            ContentValues valores = new ContentValues();
            valores.put(ID, id);
            valores.put(NOMBRE, nombre);
            valores.put(LST_PROP, propietario);
            valores.put(LST_ESTADO, estado);
            res = bdatos.insert(TB_LISTA, null, valores);

            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }

        return res;
    }

    public long insertarItem(String id, String nombre, double precio, String idLista) {
        bdatos.beginTransaction();
        long res;
        try {
            ContentValues valores = new ContentValues();
            valores.put(ID, id);
            valores.put(NOMBRE, nombre);
            valores.put(ITM_PRECIO, precio);
            valores.put(IDLISTA, idLista);
            res = bdatos.insert(TB_ITEM, null, valores);

            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }

        return res;
    }

    public long insertarParticipa(String idUsr, String idLista, boolean activo) {
        bdatos.beginTransaction();
        long res;
        try {
            ContentValues valores = new ContentValues();

            valores.put(IDLISTA, idLista);
            valores.put(PPA_IDUSR, idUsr);
            valores.put(PPA_ACTIVO, activo ? 1 : 0);
            res = bdatos.insert(TB_ITEM, null, valores);

            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }

        return res;
    }

/*    public Cursor leerTodos() {
        //return bdatos.query(true,TB_USUARIO,null,null,null,null,null,null,"100");
        return bdatos.rawQuery("SELECT * FROM usuario", null);
    }*/


    public List<Lista> obtenerListas(String idUsuario) {
        /*List<Lista> x = new ArrayList<Lista>();
        List<Usuario> y = new ArrayList<Usuario>();
        y.add(new Usuario("1","1","1"));
        y.add(new Usuario("2","2","2"));
        x.add(new Lista("1","1",new Usuario("1","1","1"),true,y,null));
        return x;*/
        Cursor c = bdatos.query(false, TB_LISTA, null, "propietario='" + idUsuario + "'", null, null, null, null, null);
        Cursor c2 = null;
        Lista l = null;
        List<Lista> aux = new ArrayList<Lista>();
        List<Usuario> participantes;
        while (c.moveToNext()) {
            c2 = bdatos.query(false, TB_PARTICIPA, null, "idLista='" + c.getString(0) + "'", null, null, null, null, null);
            participantes = new ArrayList<Usuario>();
            while(c2.moveToNext()){
                participantes.add(this.obtenerUsuario(c.getString(1)));
            }
            c2.close();
            l = new Lista(c.getString(0), c.getString(2), this.obtenerUsuario(idUsuario), c.getString(4).equals("1"), participantes,null);
            l.setListaItems(this.obtenerItems(l.getId()));
            System.out.println("          > "+l.toString());
            aux.add(l);
        }
        c.close();
        return aux;
    }

    public List<Lista> obtenerListas(Usuario u) {
        Cursor c = bdatos.query(false, TB_LISTA, null, "propietario='" + u.getId() + "'", null, null, null, null, null);
        Lista l = null;
        List<Lista> aux = new ArrayList<Lista>();
        while (c.moveToNext()) {
            l = new Lista(c.getString(0), c.getString(2), u, c.getString(4).equals("1"));
            l.setListaItems(this.obtenerItems(l.getId()));
            aux.add(l);
        }
        return aux;
    }

    public Usuario obtenerUsuario(String id) {
        Cursor c = bdatos.query(false, TB_USUARIO, null, "id='" + id + "'", null, null, null, null, null);
        Usuario u = null;

        while (c.moveToNext()) {
            u = new Usuario(c.getString(0), c.getString(1), c.getString(3));
        }
        return u;
    }

    public List<Item> obtenerItems(String idLista) {
        Cursor c = bdatos.query(false, TB_ITEM, null, "idLista='" + idLista + "'", null, null, null, null, null);
        Item i = null;
        List<Item> aux = new ArrayList<Item>();

        while (c.moveToNext())  {
            i = new Item(c.getString(0), c.getString(1), c.getDouble(2));
            aux.add(i);
        }
        return aux;
    }
}

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
    private static final String TB_ITEM = "item";
    private static final String TB_PARTICIPA = "participa";
    private static final String TB_CONTEXTO = "contexto";
    private static final String ID = "id";
    private static final String NOMBRE = "nombre";
    private static final String USR_EMAIL = "email";
    private static final String USR_MODIF = "modificacion";
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
            Log.w(ID_LOG, "Actualiza la versión: " + oldVersion + " a la versión: " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TB_USUARIO + ";"
                    + "DROP TABLE IF EXISTS " + TB_PARTICIPA + ";"
                    + "DROP TABLE IF EXISTS " + TB_LISTA + ";"
                    + "DROP TABLE IF EXISTS " + TB_ITEM + ";");
            this.onCreate(db);
        }
    }

    public AdaptadorBD open() throws SQLException {
        bdatos = auxBD.getWritableDatabase();
        return this;
    }

    public void close() {
        auxBD.close();
    }

    public long getUltimaModificacion(String idUsr) {
        Cursor c = bdatos.rawQuery("SELECT modificacion FROM usuario WHERE id='" + idUsr + "'", null);
        long l = -1L;
        if (c.moveToFirst())
            l = c.getLong(0);
        c.close();
        return l;
    }

    public void modificarUltimaModificacion(long l, String idUsr) {
        String sql = "UPDATE " + TB_USUARIO + " SET " + USR_MODIF + " = " + l + " WHERE " + ID + " = '" + idUsr + "'";

        bdatos.beginTransaction();
        try {
            bdatos.rawQuery(sql, null);
            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }
    }

    public void insertarContextoUsuario(Usuario u) {
        bdatos.beginTransaction();
        try {

            ContentValues valores = new ContentValues();
            valores.put(ID, u.getId());
            bdatos.insert(TB_CONTEXTO, null, valores);

            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }
    }

    public void insertarUltimaModificacion(long l, String idUsr) {
        bdatos.beginTransaction();
        long res;
        try {
            bdatos.rawQuery("INSERT or replace INTO " + TB_USUARIO + " (modificacion) VALUES(" + l + ") WHERE id='" + idUsr + "'", null);

            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }
    }

    public long insertarUsuario(String id, String nombre, String email, long modif) {
        bdatos.beginTransaction();
        long res;
        try {

            ContentValues valores = new ContentValues();
            valores.put(ID, id);
            valores.put(NOMBRE, nombre);
            valores.put(USR_EMAIL, email);
            valores.put(USR_MODIF, modif);
            bdatos.delete(TB_USUARIO, ID + " = '" + id + "'", null);
            res = bdatos.insert(TB_USUARIO, null, valores);

            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }

        return res;
    }

    public long insertarLista(String id, String nombre, Usuario propietario, String estado) {
        bdatos.beginTransaction();
        long res;
        try {
            ContentValues valores = new ContentValues();
            valores.put(ID, id);
            valores.put(NOMBRE, nombre);
            valores.put(LST_PROP, propietario.getId());
            valores.put(LST_ESTADO, estado);
            bdatos.delete(TB_LISTA, ID + " = '" + id + "'", null);
            res = bdatos.insert(TB_LISTA, null, valores);

            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }

        return res;
    }

    public long insertarItem(String id, String nombre, double precio, String idLista) {
        //    bdatos.beginTransaction();

    /*    try {*/
        bdatos.beginTransaction();
        long res=0;
        try {
            ContentValues valores = new ContentValues();
            valores.put(ID, id);
            valores.put(NOMBRE, nombre);
            valores.put(ITM_PRECIO, precio);
            valores.put(IDLISTA, idLista);


            bdatos.delete(TB_ITEM, ID + " = '" + id + "'", null);
            res = bdatos.insertOrThrow(TB_ITEM, null, valores);
            bdatos.setTransactionSuccessful();
        }finally {
            bdatos.endTransaction();
        }
        //bdatos.rawQuery("INSERT INTO item VALUES ('"+id+"', '"+nombre+"', '"+precio+"', '"+idLista+"')",null);

  /*          bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }*/

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
            bdatos.delete(TB_PARTICIPA, IDLISTA + " = '" + idLista + "' AND " + PPA_IDUSR + " = '" + idUsr + "'", null);
            res = bdatos.insert(TB_PARTICIPA, null, valores);

            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }

        return res;
    }

    public List<Lista> obtenerListas(String idUsuario) {

        String sql = "SELECT l.* FROM lista l LEFT JOIN participa p ON l.id=p.idLista WHERE (l.propietario='" + idUsuario + "' AND l.estado=1) OR (p.idUsuario='" + idUsuario + "' AND p.activo=1)";

        //Cursor de las listas del usuario idUsuario
        //Cursor c = bdatos.query(false, TB_LISTA, null, "propietario='" + idUsuario + "'", null, null, null, null, null);
        Cursor c = bdatos.rawQuery(sql,null);

        System.out.println("asdfasdfasdf -> "+c.getCount());

        Usuario u = this.obtenerUsuario(idUsuario);

        Lista l = null;
        List<Lista> aux = new ArrayList<Lista>();

        if (c.moveToFirst()) {
            do {
                l = new Lista();
                l.setId(c.getString(0));
                l.setNombre(c.getString(1));
                l.setEstado(c.getString(3).equals("1"));
                String usrProp = c.getString(2);
                if (usrProp.equals(idUsuario) && u != null) {
                    l.setPropietario(u);
                } else if (!usrProp.equals(idUsuario)) {
                    l.setPropietario(this.obtenerUsuario(usrProp));
                }

                l.setListaItems(this.obtenerItems(l.getId()));

                l.setParticipantes(this.getParticipantes(l.getId()));

                aux.add(l);

            } while (c.moveToNext());
        }
        c.close();

        return aux;
    }

    public List<Lista> obtenerListas(Usuario u) {
        //Cursor de las listas del usuario idUsuario
        Cursor c = bdatos.query(false, TB_LISTA, null, "propietario='" + u.getId() + "'", null, null, null, null, null);

        Lista l = null;
        List<Lista> aux = new ArrayList<Lista>();

        if (c.moveToFirst()) {
            do {
                l = new Lista();
                l.setId(c.getString(0));
                l.setNombre(c.getString(1));
                l.setEstado(c.getString(3).equals("1"));
                String usrProp = c.getString(2);
                if (usrProp.equals(u.getId())) {
                    l.setPropietario(u);
                } else {
                    l.setPropietario(this.obtenerUsuario(usrProp));
                }

                l.setListaItems(this.obtenerItems(l.getId()));

                l.setParticipantes(this.getParticipantes(l.getId()));

                aux.add(l);

            } while (c.moveToNext());
        }
        c.close();
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
        if (c.moveToFirst()) {
            do {
                i = new Item(c.getString(0), c.getString(1), c.getDouble(2));
                aux.add(i);
            } while (c.moveToNext());
        }
        c.close();
        return aux;
    }

    public List<Usuario> getParticipantes(String idLista) {
        //Cursor cursor = bdatos.rawQuery("SELECT * FROM " + TB_PARTICIPA + " WHERE " + IDLISTA + "='" + idLista + "'", null);
        Cursor cursor = bdatos.query(false, TB_PARTICIPA, null, IDLISTA + "=?", new String[]{idLista}, null, null, null, null);

        List<Usuario> aux = new ArrayList<Usuario>();

        if (cursor.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                aux.add(this.obtenerUsuario(cursor.getString(cursor.getColumnIndex(PPA_IDUSR))));
            } while (cursor.moveToNext());
        }

        return aux;
    }

    public void updateUsuario(Usuario u) {
        bdatos.beginTransaction();
        try {

            ContentValues cv = new ContentValues();
            cv.put(NOMBRE, u.getNombre());
            cv.put(USR_EMAIL, u.getEmail());

            bdatos.update(TB_USUARIO, cv, "id='?'", new String[]{u.getId()});

            bdatos.setTransactionSuccessful();
        } finally {
            bdatos.endTransaction();
        }
    }

    public long eliminarLista(String id, Usuario usuario) {
        bdatos.beginTransaction();
        long res = -1;
        try {
            Cursor cursor = bdatos.rawQuery("SELECT * FROM " + TB_LISTA + " WHERE " + ID + "='" + id + "'", null);
            if (cursor.moveToFirst()) {
                if (cursor.getString(2).equals(usuario.getId())) { //Si el usuario es propietario
                    ContentValues valores = new ContentValues();
                    valores.put(LST_ESTADO, "0");
                    res = bdatos.update(TB_LISTA, valores, ID + "='" + id + "'", null);
                } else {
                    ContentValues valores = new ContentValues();
                    valores.put(PPA_ACTIVO, "0");
                    res = bdatos.update(TB_PARTICIPA, valores, IDLISTA + "='" + id + "' AND " + PPA_IDUSR + " = '" + usuario.getId() + "'", null);
                }
                bdatos.setTransactionSuccessful();
            }
        } finally {
            bdatos.endTransaction();
        }
        return res;
    }

    public long eliminarLista(String id, String idUsuario) {
        bdatos.beginTransaction();
        long res = -1;
        try {
            Cursor cursor = bdatos.rawQuery("SELECT * FROM " + TB_LISTA + " WHERE " + ID + "='" + id + "'", null);
            if (cursor.moveToFirst()) {
                if (cursor.getString(2).equals(idUsuario)) {
                    ContentValues valores = new ContentValues();
                    valores.put(LST_ESTADO, "0");
                    res = bdatos.update(TB_LISTA, valores, ID + "='" + id + "'", null);
                } else {
                    ContentValues valores = new ContentValues();
                    valores.put(PPA_ACTIVO, "0");
                    res = bdatos.update(TB_PARTICIPA, valores, IDLISTA + "='" + id + "' AND " + PPA_IDUSR + " = '" + idUsuario + "'", null);
                }
                bdatos.setTransactionSuccessful();
            }
        } finally {
            bdatos.endTransaction();
        }
        return res;
    }
}

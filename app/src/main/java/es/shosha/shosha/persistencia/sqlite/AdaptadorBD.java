package es.shosha.shosha.persistencia.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jesús Iráizoz on 06/03/2017.
 */

public class AdaptadorBD {
    private static final String NOMBRE_BD = "ShoSha";
    private static final int VERSION_BD = 1;

    private static final String TB_USUARIO = "usuario";
    private static final String USR_ID = "id";
    private static final String USR_NOMBRE = "nombre";
    private static final String USR_EMAIL = "email";

    private static final String ID_LOG = "USO DE BD";

    private final Context contexto;

    private SQLiteDatabase bdatos;

    private AuxiliarBD auxBD;


    public AdaptadorBD(Context contexto) {
        this.contexto = contexto;
        auxBD = new AuxiliarBD(contexto);
    }

    private static class AuxiliarBD extends SQLiteOpenHelper{

        private Context cntx;

        public AuxiliarBD(Context context) {
            super(context, NOMBRE_BD, null, VERSION_BD);
            this.cntx = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            new ArchivoBD(this.cntx);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(ID_LOG, "Acualiza la versión: "+oldVersion+" a la versión: "+newVersion);
            db.execSQL("DROP TABLE IF EXISTS "+ TB_USUARIO);
            onCreate(db);
        }
    }

    public AdaptadorBD open() throws SQLException{
        bdatos = auxBD.getWritableDatabase();
        return this;
    }

    public void close(){
        auxBD.close();
    }

    public long insertarFila(String id, String nombre, String email){
        ContentValues valores = new ContentValues();
        valores.put(USR_ID, id);
        valores.put(USR_NOMBRE, nombre);
        valores.put(USR_EMAIL,email);

        return bdatos.insert(TB_USUARIO,null,valores);
    }

    public Cursor leerTodos(){
        return bdatos.query(true,TB_USUARIO,null,null,null,null,null,null,"100");
    }
}

package es.shosha.shosha.persistencia.sqlite;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jesús Iráizoz on 05/03/2017.
 */

public class ArchivoBD {
    //private AdaptadorBD miBD;
    //private AdaptadorBD miBD2;

    private Cursor generalCursor;
    private Cursor updateCursor;
    private boolean bCurActivo = false;

    public ArchivoBD(Context c) {
        System.out.println("            > ");
        String fDestino = "/data/data/" + c.getPackageName() + "/databases/ShoSha";
        File f = new File(fDestino);
        if (!f.exists()) {
            InputStream is;
            try {
                is = c.getAssets().open("ShoSha");
                FileOutputStream fos = new FileOutputStream(fDestino);

                copiarBD(is, fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copiarBD(InputStream is, FileOutputStream fos) throws IOException {
        byte[] buffer = new byte[1024];
        int leido;

        while((leido = is.read(buffer))>0) {
            fos.write(buffer, 0, leido);
        }
        is.close();
        fos.close();
    }

}

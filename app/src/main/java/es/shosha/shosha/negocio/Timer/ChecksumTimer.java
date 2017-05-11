package es.shosha.shosha.negocio.Timer;

import android.util.Log;

import java.util.Date;
import java.util.TimerTask;

import es.shosha.shosha.negocio.NegocioChecksum;

/**
 * Created by Jesús Iráizoz on 11/05/2017.
 */

public class ChecksumTimer extends TimerTask {
    @Override
    public void run() {
        Log.i("ChecksumTimer ", "Ejecutado el " + new Date());
        NegocioChecksum.setChecksum(false);
    }
}

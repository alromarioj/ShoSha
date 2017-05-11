package es.shosha.shosha.negocio.Timer;

import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jesús Iráizoz on 11/05/2017.
 */

public class ChecksumCanceladorTimer extends TimerTask {
    private Timer timerACancelar;

    ChecksumCanceladorTimer(Timer timerACancelar) {
        this.timerACancelar = timerACancelar;
    }

    @Override
    public void run() {
        Log.i("ChecksumCancelador", "Timer cancelado el " + new Date());
        timerACancelar.cancel();
    }
}

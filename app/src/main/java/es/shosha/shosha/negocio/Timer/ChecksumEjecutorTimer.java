package es.shosha.shosha.negocio.Timer;

import java.util.Calendar;
import java.util.Timer;

/**
 * Created by Jesús Iráizoz on 11/05/2017.
 */

public class ChecksumEjecutorTimer {

    private final static int MILIS = 1000;

    public ChecksumEjecutorTimer() {
        Calendar init = Calendar.getInstance();
        init.add(Calendar.SECOND, 60);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new ChecksumTimer(), init.getTime(), 60 * MILIS);

        // Parar 10 segundos despues de comenzar
//        init.add(Calendar.SECOND, 10);
//        timer.schedule(new ChecksumCanceladorTimer(timer), init.getTime());
    }
}

package es.shosha.shosha.servicios;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import es.shosha.shosha.ListaProductos;
import es.shosha.shosha.ListasActivas;
import es.shosha.shosha.R;

/**
 * Created by inhernan on 11/05/2017.
 */

public class ServicioFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       super.onMessageReceived(remoteMessage);
        Map<String,String> datos=remoteMessage.getData();
        mostrarNotificacion(
                datos.get("message")
        );
    }
    private void mostrarNotificacion(String mensaje){
        int icono = R.mipmap.logo;
        Intent i = new Intent(this, ListaProductos.class);
        //Enviar id de lista

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,i, 0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext())
        .setContentIntent(pendingIntent)
        .setSmallIcon(icono)
        .setContentTitle("Compra")
        .setContentText(mensaje)
        .setVibrate(new long[]{100,250,100,500})
        .setAutoCancel(true);//Al hacer click la notificación desaparece

        NotificationManager nm =(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1,builder.build());
    }

}

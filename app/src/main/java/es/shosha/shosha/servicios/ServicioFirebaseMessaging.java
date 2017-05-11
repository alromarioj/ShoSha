package es.shosha.shosha.servicios;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by inhernan on 11/05/2017.
 */

public class ServicioFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("FIREBASE",remoteMessage.getNotification().getBody());
    }
}

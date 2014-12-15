package ut.handshake;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Set;

/**
 * Created by Aurelius on 12/13/14.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = GcmBroadcastReceiver.class.getSimpleName();

    Context mContext;
    SharedPreferences prefs;
    int unread_messages;
    public GcmBroadcastReceiver() {
        super();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        prefs = mContext.getSharedPreferences(Handshake.class.getSimpleName(), Context.MODE_PRIVATE);
        unread_messages = prefs.getInt(Handshake.PROPERTY_UNREAD_COUNT, 0);
        String action = intent.getAction();
        Set<String> categories = intent.getCategories();
        if (action != null) {
            Log.d(TAG, "action: " + action);
        }
        if (categories != null) {
            Log.d(TAG, "categories: " + categories.toString());
        }
        if (action.equals(Handshake.NOTIFICATION_CANCELLED)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Handshake.PROPERTY_UNREAD_COUNT, 0);
            editor.commit();
            return;
        }
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
        /*
         * Filter messages based on message type. Since it is likely that GCM
         * will be extended in the future with new message types, just ignore
         * any message types you're not interested in, or that you don't
         * recognize.
         */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " +
                //        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                /*for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());*/
                // Post notification of received message.
                sendNotification(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }

    }

    private void sendNotification(Bundle extras) {
        Log.d(TAG, "Received in sendNotification: "+extras.toString());
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        unread_messages++;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Handshake.PROPERTY_UNREAD_COUNT, unread_messages);
        editor.commit();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setNumber(unread_messages)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("New Handshake Message")
                                //.setStyle(new NotificationCompat.BigTextStyle()
                                //        .bigText(msg))
                        .setContentText(extras.toString())
                        .setTicker("New Handshake Message")
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        String[] events = new String[6];
        if (unread_messages == 1) {
            inboxStyle.setBigContentTitle(unread_messages+" new message");
        } else {
            inboxStyle.setBigContentTitle(unread_messages + " new messages");
        }
        //events[0] = "Tiger Bear @ Green Leaf";
        //events[1] = "Orange Cheetah @ Kobe Bryant";
        for (int i=0; i < events.length; i++) {

            inboxStyle.addLine(events[i]);
        }
        mBuilder.setStyle(inboxStyle);

        // Go to home page with navigation drawer open
        Intent openUnread = new Intent();
        openUnread.setClass(mContext, Handshake.class);
        //openUnread.putExtra("FromPush", true);
        //openUnread.putExtra("RouteId", routeId);
        //openUnread.putExtra("clientUserId", clientUserId);
        //TODO: add the message information here to pass to the activity
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                openUnread, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDeleteIntent(getDeleteIntent());
        mNotificationManager.notify(Handshake.notification_id, mBuilder.build());

    }

    private PendingIntent getDeleteIntent()
    {
        Log.d(TAG, "getDeleteIntent()");
        Intent intent = new Intent(mContext, GcmBroadcastReceiver.class);
        intent.setAction(Handshake.NOTIFICATION_CANCELLED);
        //intent.setCategories("ut.anhandshake");
        return PendingIntent.getBroadcast(mContext, 0, intent, 0);
    }
}

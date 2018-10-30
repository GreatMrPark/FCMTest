package com.greatmrpark.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.net.URL;

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Bitmap bigPicture;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Data: " + remoteMessage.getData());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("NotificationMessage", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);


        Log.d(TAG, "messageBody: " + messageBody);

        String title            = null;
        String content          = null;
        int smallIcon        = R.mipmap.ic_launcher;
        String contentTitle     = null;
        String contentText      = null;
        String bigContentTitle  = null;
        String bigText          = null;
        String bigPicturePath       = null;
        String summaryText      = null;
        String linkPath         = null;

        try {
            JSONObject json = new JSONObject(messageBody);
            title = json.getString("title");
            content = json.getString("content");
            contentTitle = json.getString("contentTitle");
            contentText = json.getString("contentText");
            bigContentTitle = json.getString("bigContentTitle");
            bigText = json.getString("bigText");
            bigPicturePath = json.getString("bigPicture");
            summaryText = json.getString("summaryText");
            linkPath = json.getString("linkPath");

        } catch(Exception e) {}

        //이미지 온라인 링크를 가져와 비트맵으로 바꾼다.
        try {
            URL url = new URL(bigPicturePath);
            bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)

                        //BigTextStyle
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .setBigContentTitle(bigContentTitle)
//                                .bigText(bigText))

                        //이미지를 보내는 스타일 사용하기
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(bigPicture)
                                .setBigContentTitle(bigContentTitle)
                                .setSummaryText(summaryText))

                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}



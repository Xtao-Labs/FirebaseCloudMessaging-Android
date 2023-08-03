package com.example.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
	public static final String FCM_PARAM = "picture";
	private static final String CHANNEL_NAME = "FCM";
	private static final String CHANNEL_DESC = "Firebase Cloud Messaging";
	private static final String TAG = "MyFirebaseMsgService";
	private int numMessages = 0;

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// [START_EXCLUDE]
		// There are two types of messages data messages and notification messages. Data messages
		// are handled
		// here in onMessageReceived whether the app is in the foreground or background. Data
		// messages are the type
		// traditionally used with GCM. Notification messages are only received here in
		// onMessageReceived when the app
		// is in the foreground. When the app is in the background an automatically generated
		// notification is displayed.
		// When the user taps on the notification they are returned to the app. Messages
		// containing both notification
		// and data payloads are treated as notification messages. The Firebase console always
		// sends notification
		// messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
		// [END_EXCLUDE]

		// TODO(developer): Handle FCM messages here.
		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "From: " + remoteMessage.getFrom());

		// Check if message contains a data payload.
		if (remoteMessage.getData().size() > 0) {
			Log.d(TAG, "Message data payload: " + remoteMessage.getData());
			handleNow();
		}

		// Check if message contains a notification payload.
		if (remoteMessage.getNotification() != null) {
			Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
			String notificationBody = remoteMessage.getNotification().getBody();
			if (remoteMessage.getNotification().getBody() != null) {
				sendNotification(remoteMessage.getNotification());
			}
		}

		// Also if you intend on generating your own notifications as a result of a received FCM
		// message, here is where that should be initiated. See sendNotification method below.
	}
	// [END receive_message]

	@Override
	public void onNewToken(String token) {
		Log.d(TAG, "Refreshed token: " + token);
	}

	private void handleNow() {
		Log.d(TAG, "Short lived task is done.");
	}

	private void sendNotification(RemoteMessage.Notification notification) {
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
				.setContentTitle(notification.getTitle())
				.setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getBody()))
				.setAutoCancel(true)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentInfo("Hello")
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
				.setColor(getColor(R.color.colorAccent))
				.setLights(Color.RED, 1000, 300)
				.setDefaults(Notification.DEFAULT_VIBRATE)
				.setNumber(++numMessages)
				.setSmallIcon(R.drawable.ic_notification);
		final Notification app_notification = notificationBuilder.build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					getString(R.string.notification_channel_id), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
			);
			channel.setDescription(CHANNEL_DESC);
			channel.setShowBadge(true);
			channel.canShowBadge();
			channel.enableLights(true);
			channel.setLightColor(Color.RED);
			channel.enableVibration(true);
			channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

			assert notificationManager != null;
			notificationManager.createNotificationChannel(channel);
		}
		assert notificationManager != null;
		notificationManager.notify(0, app_notification);
	}
}
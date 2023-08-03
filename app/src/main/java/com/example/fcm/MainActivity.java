package com.example.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


import static com.example.fcm.R.id.txt;

public class MainActivity extends AppCompatActivity {
	private TextView mTextView;
	private static final String TAG = "Main";
	private final ActivityResultLauncher<String> requestPermissionLauncher =
			registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
				@Override
				public void onActivityResult(Boolean isGranted) {
					if (isGranted) {
						Toast.makeText(MainActivity.this, "Notifications permission granted", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(MainActivity.this, "FCM can't post notifications without POST_NOTIFICATIONS permission",
								Toast.LENGTH_LONG).show();
					}
				}
			});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = findViewById(txt);

		askNotificationPermission();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// Create channel to show notifications.
			String channelId  = getString(R.string.notification_channel_id);
			String channelName = "FCM";
			NotificationManager notificationManager =
					getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(new NotificationChannel(channelId,
					channelName, NotificationManager.IMPORTANCE_LOW));
		}

		if (getIntent().getExtras() != null) {
			for (String key : getIntent().getExtras().keySet()) {
				Object value = getIntent().getExtras().get(key);
				Log.d(TAG, "Key: " + key + " Value: " + value);
			}
		}
	}

	public void showToken(View view) {
		// Get token
		// [START log_reg_token]
		FirebaseMessaging.getInstance().getToken()
				.addOnCompleteListener(new OnCompleteListener<String>() {
					@Override
					public void onComplete(@NonNull Task<String> task) {
						if (!task.isSuccessful()) {
							Log.w(TAG, "Fetching FCM registration token failed", task.getException());
							Toast.makeText(MainActivity.this, "GetToken Failed.", Toast.LENGTH_SHORT).show();
							return;
						}

						// Get new FCM registration token
						String token = task.getResult();

						// show token
						mTextView.setText(token);
						Intent sendIntent = new Intent();
						sendIntent.setAction(Intent.ACTION_SEND);
						sendIntent.putExtra(Intent.EXTRA_TEXT, token);//分享的文本内容
						sendIntent.setType("text/plain");
						startActivity(Intent.createChooser(sendIntent, "分享到"));
					}
				});
		// [END log_reg_token]
	}

	public void subscribe(View view) {
		FirebaseMessaging.getInstance().subscribeToTopic("news");
		mTextView.setText(R.string.subscribed);
	}

	public void unsubscribe(View view) {
		FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
		mTextView.setText(R.string.unsubscribed);
	}

	public void sendToken(View view) {

	}

	public void sendTokens(View view) {

	}

	public void sendTopic(View view) {

	}

	private void askNotificationPermission() {
		// This is only necessary for API Level > 33 (TIRAMISU)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") ==
					PackageManager.PERMISSION_GRANTED) {
				// FCM SDK (and your app) can post notifications.
			} else {
				// Directly ask for the permission
				requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS");
			}
		}
	}
}

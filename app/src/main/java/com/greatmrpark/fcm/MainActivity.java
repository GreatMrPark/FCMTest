package com.greatmrpark.fcm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView messageTextView = (TextView) findViewById(R.id.MessageTextView);
        final EditText tokenEditText = (EditText) findViewById(R.id.TokenEditText);

        FirebaseMessaging.getInstance().subscribeToTopic("ALL");

        if (FirebaseInstanceId.getInstance().getToken() != null) {
            String token = FirebaseInstanceId.getInstance().getToken();
            tokenEditText.setText(token);
        }

        Bundle extras = this.getIntent().getExtras();
        if(extras != null){
            if(extras.containsKey("NotificationMessage")) {
                // extract the extra-data in the Notification
                String msg = extras.getString("NotificationMessage");
                messageTextView.setText(msg);
            }
        }
    }
}

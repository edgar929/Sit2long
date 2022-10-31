package com.example.sit2long;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private long START_TIME_IN_MILLIS = 1500000;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button mButtonSet;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private TextView mTextViewCountDown;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        mButtonSet = findViewById(R.id.button_set);

        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                mEdit =(EditText)findViewById(R.id.editTextNumber);
                String txt = mEdit.getText().toString();
                START_TIME_IN_MILLIS = Long.valueOf(txt) * 60 * 1000;
                resetTimer();
            }

        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerRunning)
                    pauseTimer();
                else
                    startTimer();
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();
    }

    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000 ) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                sendNotification();
                mTimerRunning = false;
                mButtonStartPause.setText("start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer(){
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("start");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer(){
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText(){
        int mins = (int) (mTimeLeftInMillis / 1000) / 60;
        int secs = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void sendNotification() {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("sit2LongChanel",
                    "Sit2Long", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "sit2LongChanel")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Sit2Long")
                    .setContentText("You have sat too long, you need to stand up and walk");
            Notification notification = builder.build();
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

            notificationManagerCompat.notify(1,notification);

        }
    }
}
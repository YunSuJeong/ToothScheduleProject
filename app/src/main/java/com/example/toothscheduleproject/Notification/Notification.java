package com.example.toothscheduleproject.Notification;

import android.app.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.toothscheduleproject.R;

import java.util.Calendar;


public class Notification extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);


        ImageButton ibtnBack = findViewById(R.id.ibtnBack);
        TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        TextView tvSaveTime = (TextView) findViewById(R.id.tvSaveTime);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Intent intent = new Intent(this, AlarmReceiver.class);

            btnSave.setOnClickListener(x -> {

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);

                        if (calendar.before(Calendar.getInstance())) {
                            calendar.add(Calendar.DATE, 1);
                        }
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY, alarmIntent);

                        Toast.makeText(Notification.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();

                        tvSaveTime.setText("????????? ?????? : " + hour + "??? " + minute + "???");
                    }
            );

            ibtnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PendingIntent sender = PendingIntent.getBroadcast(Notification.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (sender != null) {
                        alarmManager.cancel(sender);
                        sender.cancel();
                    }
                    Toast.makeText(Notification.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    tvSaveTime.setText("");

                }
            });

        }
    }
}


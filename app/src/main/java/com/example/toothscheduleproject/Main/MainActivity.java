package com.example.toothscheduleproject.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.toothscheduleproject.InfoBoard.Infoboard;
import com.example.toothscheduleproject.Notification.Notification;
import com.example.toothscheduleproject.R;
import com.example.toothscheduleproject.ScheduleSetting.Schedule;
import com.example.toothscheduleproject.ToothTimeInfo;
import com.example.toothscheduleproject.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ToothSchedule");

        ImageButton ibtnMypage = findViewById(R.id.ibtnMypage);
        ImageButton ibtnNotification = findViewById(R.id.ibtnNotification);
        Button btnInfo = findViewById(R.id.btnInfo);
        TextView tvAvgCount = (TextView) findViewById(R.id.tvAvgCount);

        mDatabaseRef.child("UserInfo").orderByChild("idToken").equalTo(mFirebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInfo userInfo = null;
                ToothTimeInfo toothTimeInfo = null;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userInfo = dataSnapshot.getValue(UserInfo.class);
                    toothTimeInfo = dataSnapshot.getValue(ToothTimeInfo.class);
                }

                if (userInfo != null) {
                    ArrayList<ToothTimeInfo> lstToothTimeInfo = userInfo.getLstToothTime();
                    ArrayList try_brush = userInfo.getLstToothTime();
                    if (lstToothTimeInfo == null)
                        lstToothTimeInfo = new ArrayList<>();

                    if (userInfo.getLstToothTime() == null) {
                        tvAvgCount.setText("?????? ?????? ?????? : 0???");
                    } else {
                        float count = try_brush.size();
                        if (count != 0) {

                            // ??? ??? ??????
                            ArrayList<String> lstDay = new ArrayList<>();

                            for(int i=0; i<lstToothTimeInfo.size(); i++) {
                                if(!lstDay.contains(lstToothTimeInfo.get(i).getDate())) {
                                    lstDay.add(lstToothTimeInfo.get(i).getDate());
                                }
                            }
                            String brushAvg = String.format("%.2f",count/lstDay.size());
                            tvAvgCount.setText("?????? ?????? ?????? : " + brushAvg + "???");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // ??????
        mCalendarView = findViewById(R.id.calendarView);
        try {
            mCalendarView.setDate(Calendar.getInstance());
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        // ?????? ?????? ????????? ???????????? ????????? ????????? ????????????
        setCalendarEventFromServer();


        // ?????? ?????? ?????? ?????????
        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar calendar = eventDay.getCalendar();
                int year = calendar.get(Calendar.YEAR);
                int month = (calendar.get(Calendar.MONTH)) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                Intent scheduleIntent = new Intent(MainActivity.this, Schedule.class);
                scheduleIntent.putExtra("year", year);
                scheduleIntent.putExtra("month", month);
                scheduleIntent.putExtra("day", day);
                startActivity(scheduleIntent);
            }
        });

        // ??????????????? ?????? ????????? ???
        ibtnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });

        // ????????? ????????? ???
        ibtnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Notification.class);
                startActivity(intent);
            }
        });

        // ??????????????? ?????? ????????? ??? ??????????????? ???????????? ??????
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Infoboard.class);
                startActivity(intent);
            }
        });
    }

    private void setCalendarEventFromServer() {
        List<EventDay> events = new ArrayList<>(); // ?????? ????????? ???????????? ???????????? ??????

        ArrayList<ToothTimeInfo> lstToothTime = new ArrayList<>(); // ?????? ????????? ???????????? ??????
        mDatabaseRef.child("UserInfo").child(mFirebaseAuth.getCurrentUser().getUid()).child("lstToothTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ToothTimeInfo toothTimeInfo = dataSnapshot.getValue(ToothTimeInfo.class);
                    lstToothTime.add(toothTimeInfo);
                }

                HashMap<Long, Integer> map = new HashMap<>();
                for (int i = 0; i < lstToothTime.size(); i++) {
                    String[] splitDate = lstToothTime.get(i).getDate().split("/");
                    int year = Integer.parseInt(splitDate[0]);
                    int month = Integer.parseInt(splitDate[1]) - 1;
                    int day = Integer.parseInt(splitDate[2]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);

                    events.add(new EventDay(calendar, R.drawable.dot));
                    Long asd = events.get(i).getCalendar().getTimeInMillis();
                    Integer count = map.get(asd);
                    if (count == null)
                        map.put(asd, 1);
                    else
                        map.put(asd, count + 1);

                }

                // check count for same day data
                for (int i = 0; i < events.size(); i++) {
                    for (Map.Entry<Long, Integer> entry : map.entrySet()) {
                        if (events.get(i).getCalendar().getTimeInMillis() == entry.getKey()) {
                            if (entry.getValue() == 2) {
                                events.set(i, new EventDay(events.get(i).getCalendar(), R.drawable.dot2xml));
                            } else if (entry.getValue() == 3) {
                                events.set(i, new EventDay(events.get(i).getCalendar(), R.drawable.dot3xml));
                            } else if(entry.getValue() == 4) {
                                events.set(i, new EventDay(events.get(i).getCalendar(), R.drawable.dot4));
                            } else {
                                events.set(i, new EventDay(events.get(i).getCalendar(), R.drawable.dotxml));
                            }
                        }
                    }
                }

                mCalendarView.setEvents(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
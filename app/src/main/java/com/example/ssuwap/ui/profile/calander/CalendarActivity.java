package com.example.ssuwap.ui.profile.calander;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ssuwap.R;
import com.example.ssuwap.data.calendar.CalendarData;
import com.example.ssuwap.data.calendar.LineDecorator;
import com.example.ssuwap.data.calendar.SubjectData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;  //캘린더

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        fetchDataFromDatabase();

        Log.d("day", "ss");

//        calendarView.setOnDateChangedListener((widget, date, selected) -> {
//            String selectedDate = date.getYear() + "-" +
//                    (date.getMonth() + 1) + "-" +
//                    date.getDay();
//
//            // 해당 날짜의 데이터 가져오기
//            List<CalendarData> selectedDateData = new ArrayList<>();
//            for (CalendarData data : calendarDataList) {
//                if (data.getDate().equals(selectedDate)) {
//                    selectedDateData.add(data);
//                }
//            }
//
//        });


    }

    private void fetchDataFromDatabase() {
        // Firebase Database reference
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("UserInfo");

        // 데이터를 저장할 HashMap
        HashMap<CalendarDay, List<SubjectData>> dataMap = new HashMap<>();

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    DataSnapshot timeInfo = userSnapshot.child("TimeInfo");

                    // year > month > day 순회
                    for (DataSnapshot yearSnapshot : timeInfo.getChildren()) {
                        for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                            for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                                // 날짜 생성
                                CalendarDay day = CalendarDay.from(
                                        Integer.parseInt(yearSnapshot.getKey()), // year
                                        Integer.parseInt(monthSnapshot.getKey()) - 1, // month (0-based)
                                        Integer.parseInt(daySnapshot.getKey()) // day
                                );

                                // 날짜에 해당하는 과목 리스트
                                List<SubjectData> subjectList = new ArrayList<>();

                                // 해당 날짜의 모든 과목 데이터 추가
                                for (DataSnapshot subjectSnapshot : daySnapshot.getChildren()) {
                                    int color = Color.parseColor(subjectSnapshot.child("color").getValue(String.class));
                                    float totalDuration = subjectSnapshot.child("totalDuration").getValue(Float.class);

                                    subjectList.add(new SubjectData(color, totalDuration));
                                }
                                Log.d("day", String.valueOf(day));
                                // HashMap에 추가
                                dataMap.put(day, subjectList);
                            }
                        }
                    }
                }
                updateCalendar(dataMap);    //그냥 맵에 넣고 함수화 합시다
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });
    }

    private void updateCalendar(HashMap<CalendarDay, List<SubjectData>> dataMap) {
       calendarView.removeDecorators();

        // 날짜별 데이터 처리
        for (Map.Entry<CalendarDay, List<SubjectData>> entry : dataMap.entrySet()) {
            CalendarDay day = entry.getKey(); // 날짜
            List<SubjectData> subjects = entry.getValue(); // 해당 날짜의 과목 데이터 리스트
            calendarView.addDecorator(new LineDecorator(day, subjects));
            Log.d("day", String.valueOf(day)+"why");
        }
    }


}
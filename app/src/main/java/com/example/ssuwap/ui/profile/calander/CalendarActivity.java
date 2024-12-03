package com.example.ssuwap.ui.profile.calander;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ssuwap.R;
import com.example.ssuwap.data.calendar.SubjectData;
import com.example.ssuwap.data.todolist.TodolistData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    private ArrayList<TodolistData> list;
    private DatabaseReference databaseReference;
    private LinearLayout timetableContainer;
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

    //탐테채우기
    private void loadData() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        databaseReference.child(year).child(month).child(day).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // RecyclerView 리스트 초기화
                timetableContainer.removeAllViews(); // 타임테이블 초기화
                initTimetable(); // 타임테이블 기본 UI 다시 생성

                for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                    String key = idSnapshot.getKey(); // 고유 키
                    String subject = idSnapshot.child("subject").getValue(String.class);
                    String color = idSnapshot.child("color").getValue(String.class);
                    Long totalDuration = idSnapshot.child("totalDuration").getValue(Long.class);

                    // null 값 체크 후 RecyclerView에 데이터 추가
                    if (subject != null && color != null) {
                        TodolistData todolistData = new TodolistData(key, subject, Color.parseColor(color));
                        todolistData.setTotalDuration(totalDuration != null ? totalDuration : 0);
                        list.add(todolistData);

                        // 타임블록 세션 색칠
                        DataSnapshot sessionsSnapshot = idSnapshot.child("sessions");
                        for (DataSnapshot sessionSnapshot : sessionsSnapshot.getChildren()) {
                            Long startTime = sessionSnapshot.child("startTime").getValue(Long.class);
                            Long endTime = sessionSnapshot.child("endTime").getValue(Long.class);
                            if (startTime != null && endTime != null) {
                                calendar.setTimeInMillis(startTime);
                                int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                                int startMinute = calendar.get(Calendar.MINUTE);

                                calendar.setTimeInMillis(endTime);
                                int endHour = calendar.get(Calendar.HOUR_OF_DAY);
                                int endMinute = calendar.get(Calendar.MINUTE);

                                colorTimeBlock(startHour, startMinute, endHour, endMinute, Color.parseColor(color));
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // RecyclerView 데이터 갱신
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "데이터 로드 실패: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void colorTimeBlock(int startHour, int startMinute, int endHour, int endMinute, int color) {
        int startCell = startHour * 60 + startMinute;
        int endCell = endHour * 60 + endMinute;

        for (int i = startCell; i < endCell; i++) {
            int hour = i / 60; // 현재 시간
            int minuteIndex = i % 60; // 시간 내에서 몇 번째 분인지
            FrameLayout hourFrame = (FrameLayout) timetableContainer.getChildAt(hour);
            LinearLayout minuteContainer = (LinearLayout) hourFrame.getChildAt(1); // 시간 라벨 다음에 분 영역
            View cell = minuteContainer.getChildAt(minuteIndex);
            cell.setBackgroundColor(color);
        }
    }
    private void initTimetable() {
        for (int hour = 0; hour < 24; hour++) {
            // FrameLayout 생성
            FrameLayout hourFrame = new FrameLayout(requireContext());
            hourFrame.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    80 // 각 시간의 높이
            ));

            // 시간 숫자 표시
            TextView hourLabel = new TextView(requireContext());
            hourLabel.setText(String.format("%02d", hour)); // 예: 15
            hourLabel.setTextColor(Color.BLACK);
            hourLabel.setTextSize(15);
            hourLabel.setGravity(Gravity.CENTER_VERTICAL);
            FrameLayout.LayoutParams labelParams = new FrameLayout.LayoutParams(
                    60, // 너비
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            labelParams.leftMargin = 10;
            hourLabel.setLayoutParams(labelParams);

            // 60분 영역 (LinearLayout)
            LinearLayout minuteContainer = new LinearLayout(requireContext());
            minuteContainer.setOrientation(LinearLayout.HORIZONTAL);
            FrameLayout.LayoutParams minuteParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            minuteParams.leftMargin = 60; // 시간 표시 공간 확보
            minuteContainer.setLayoutParams(minuteParams);

            // 60개의 분 셀 추가
            for (int minute = 0; minute < 60; minute++) {
                View cell = new View(requireContext());
                LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(
                        0, // 너비는 가중치로 균등 분배
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1 // 가중치
                );
                cell.setLayoutParams(cellParams);
                cell.setBackgroundColor(Color.LTGRAY); // 기본 배경색
                minuteContainer.addView(cell);
            }

            // FrameLayout에 추가
            hourFrame.addView(hourLabel);
            hourFrame.addView(minuteContainer);

            // 타임테이블에 추가
            timetableContainer.addView(hourFrame);
        }

    }

}
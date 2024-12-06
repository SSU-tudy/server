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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    HashMap<CalendarDay, Float> timeMap = new HashMap<>(); //날짜별 totalTime데이터
    HashMap<CalendarDay, List<SubjectData>> dataMap = new HashMap<>();  //전체 날짜데이터
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

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // 선택된 날짜 생성
                CalendarDay selectedDay = CalendarDay.from(date.getYear(), date.getMonth() - 1, date.getDay());

                // 해당 날짜의 데이터를 가져옴
                List<SubjectData> selectedSubjects = dataMap.get(selectedDay);

            }
        });
        CalendarDay calendarDay = calendarView.getCurrentDate();
        updateChartSelectedMonth(calendarDay.getYear(), calendarDay.getMonth());
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int selectedYear = date.getYear();
                int selectedMonth = date.getMonth(); // 0-based

                // 해당 월의 데이터로 그래프 갱신
                updateChartSelectedMonth(selectedYear, selectedMonth);
            }
        });

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

        Log.d("updateCalendar", "dataMap size: " + dataMap.size());
        for (Map.Entry<CalendarDay, List<SubjectData>> entry : dataMap.entrySet()) {
            Log.d("updateCalendar", "Date: " + entry.getKey() + ", Subjects: " + entry.getValue());
        }

    }

    private void fetchDataFromDatabase() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.e("FirebaseAuth", "User not logged in.");
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show();
            return; // 로그아웃 상태라면 메서드 종료
        }
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(currentUserId);;
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot timeInfo = snapshot.child("TimeInfo");
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
                                //꺾은선 그래프용 전체 시간
                                float dayTotalTime = 0;

                                // 날짜에 해당하는 과목 리스트
                                List<SubjectData> subjectList = new ArrayList<>();

                                // 해당 날짜의 모든 과목 데이터 추가
                                for (DataSnapshot subjectSnapshot : daySnapshot.getChildren()) {
                                    int color = Color.parseColor(subjectSnapshot.child("color").getValue(String.class));
                                    float totalDuration = subjectSnapshot.child("totalDuration").getValue(Float.class);

                                    subjectList.add(new SubjectData(color, totalDuration));
                                    dayTotalTime += totalDuration;
                                }

                                Log.d("timeMapCheck", "Date: " + day + ", Total Time: " + dayTotalTime);

                                //꺽은선그래프용 배열에 추가
                                timeMap.put(day, dayTotalTime/3600000.0f);
                                // HashMap에 추가
                                dataMap.put(day, subjectList);
                            }
                        }
                    }

                updateCalendar();//그냥 맵에 넣고 함수화 합시다
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });
    }

    private void updateCalendar() {
       calendarView.removeDecorators();

        // 날짜별 데이터 처리
        for (Map.Entry<CalendarDay, List<SubjectData>> entry : dataMap.entrySet()) {
            CalendarDay day = entry.getKey(); // 날짜
            List<SubjectData> subjects = entry.getValue(); // 해당 날짜의 과목 데이터 리스트
            calendarView.addDecorator(new LineDecorator(day, subjects));
            Log.d("day", String.valueOf(day)+"why");
        }
    }

    private void updateChartSelectedMonth(int selectedYear, int selectedMonth) {
        LineChart lineChart = findViewById(R.id.lineChart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleXEnabled(true);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth);

        XAxis xAxis = lineChart.getXAxis();

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xAxisLabels = new ArrayList<>();
        int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);


        for (Map.Entry<CalendarDay, Float> entry : timeMap.entrySet()) {
            CalendarDay day = entry.getKey();
            if ((day.getYear() == selectedYear && day.getMonth() == selectedMonth)) {
                entries.add(new Entry(day.getDay(), entry.getValue())); // X축: day, Y축: value

            }
        }
        for(int day = 1 ; day<= max ; day++){
            xAxisLabels.add(String.valueOf(day)); // X축 레이블: day
            CalendarDay currentDay = CalendarDay.from(selectedYear, selectedMonth,day);
            boolean isPresent = false;
            for (Entry entry : entries) {
                if (Math.round(entry.getX()) == day) {
                    isPresent = true;
                    break;
                }
            }

            if (!isPresent) {
                entries.add(new Entry(day, 0.0f)); // X축: day, Y축: 0
                xAxisLabels.add(String.valueOf(day)); // X축 레이블: day
            }
        }
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(max);
        xAxis.setGranularity(1f); // 1 단위로 표시
        xAxis.setGranularityEnabled(true);

        lineChart.setVisibleXRangeMaximum(7);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(20); // 최대값
        lineChart.getAxisRight().setEnabled(false);

        // LineDataSet 생성
        LineDataSet dataSet = new LineDataSet(entries, "Daily Study Time");
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        // X축 레이블 설정
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // X축 레이블
            }
        });
        lineChart.invalidate(); // 그래프 갱신
    }

}
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    HashMap<CalendarDay, Float> timeMap = new HashMap<>(); //날짜별 totalTime데이터
    HashMap<CalendarDay, List<SubjectData>> dataMap = new HashMap<>();  //전체 날짜데이터
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
                CalendarDay selectedDay = CalendarDay.from(date.getYear(), date.getMonth(), date.getDay());
                updateBarChart(selectedDay);

            }
        });
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int selectedYear = date.getYear();
                int selectedMonth = date.getMonth(); // 0-based

                // 해당 월의 데이터로 그래프 갱신
                updateLineChart(selectedYear, selectedMonth);
            }
        });

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
                                    String subject = subjectSnapshot.child("subject").getValue(String.class);
                                    subjectList.add(new SubjectData(color, totalDuration, subject));
                                    dayTotalTime += totalDuration;
                                }


                                //꺽은선그래프용 배열에 추가
                                timeMap.put(day, (dayTotalTime/3600000.0f));
                                // HashMap에 추가
                                dataMap.put(day, subjectList);
                            }
                        }
                    }

                updateCalendar();//캘린더불러오기
                CalendarDay calendarDay = calendarView.getCurrentDate();
                updateLineChart(calendarDay.getYear(), calendarDay.getMonth());//lineplot불러오기
                updateBarChart(calendarDay);
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

    private void updateLineChart(int selectedYear, int selectedMonth) {
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
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.2f",value);
            }
        });
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

        Collections.sort(entries, (e1, e2) -> Float.compare(e1.getX(), e2.getX()));
        lineChart.invalidate(); // 그래프 갱신
    }

    //bar chart
    private void updateBarChart(CalendarDay selectedDate) {
        BarChart barChart = findViewById(R.id.barChart);

        // BarEntry 리스트 초기화
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<Integer> barColors = new ArrayList<>();
        ArrayList<String> xAxisLabels = new ArrayList<>();
        TextView tvsubject = findViewById(R.id.tv_subject);
        tvsubject.setVisibility(View.GONE);

        // dataMap에서 선택된 날짜의 SubjectData 가져오기
        if (dataMap.containsKey(selectedDate)) {
            List<SubjectData> subjects = dataMap.get(selectedDate);

            // 각 과목의 데이터를 BarEntry로 추가
            for (int i = 0; i < subjects.size(); i++) {
                SubjectData subject = subjects.get(i);
                barEntries.add(new BarEntry(i, subject.getTotalDuration())); // X축: index, Y축: totalTime
                barColors.add(subject.getColor()); // 막대 색상 추가
                xAxisLabels.add(subject.getSubject()); // X축 레이블 추가
            }
        } else {
            for (int i = 0; i < 5; i++) {
                barEntries.add(new BarEntry(i, 0f)); // 빈 값 추가
                barColors.add(Color.GRAY); // 기본 색상
                xAxisLabels.add(""); // 빈 레이블
            }
        }

        // BarDataSet 생성
        BarDataSet barDataSet = new BarDataSet(barEntries, "Subjects");
        barDataSet.setColors(barColors); // 과목별 색상 설정
        barDataSet.setValueTextSize(10f); // 값 텍스트 크기
        barDataSet.setValueTextColor(Color.TRANSPARENT); // 값 텍스트 색상 클릭안하면 안보이게
        barChart.setTouchEnabled(true); // 터치 이벤트 활성화
        barChart.setHighlightPerTapEnabled(true);


        // BarData 생성 및 BarChart 설정
        BarData barData = new BarData(barDataSet);
        barChart.setData(barEntries.isEmpty() ? new BarData() : barData);
        // X축 설정
        XAxis xAxis = barChart.getXAxis();
        xAxis.setEnabled(false);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);

        // Y축 설정
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setGranularity(1f); // 1 단위로 설정
        yAxis.setAxisMinimum(0f); // 최소값 0

        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.2f", value/3600000f); // 소수점 2자리로 표시
            }
        });
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) {
                    return ""; // 값이 0이면 빈 문자열 반환
                }else{
                    return String.format("%.2f", value / 3600000f); // 밀리초 -> 시간 (소수점 1자리)
                }
            }
        });

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX(); // 선택된 막대의 인덱스
                if (index < xAxisLabels.size()) {
                    String selectedSubject = xAxisLabels.get(index);
                    tvsubject.setText(selectedSubject);
                    tvsubject.setVisibility(View.VISIBLE); // 텍스트 보이기
                }
            }

            @Override
            public void onNothingSelected() {
                tvsubject.setVisibility(View.GONE); // 아무것도 선택되지 않았을 때 숨기기
            }
        });


        barChart.getAxisRight().setEnabled(false); // 오른쪽 Y축 비활성화
        barChart.setDragEnabled(true); // 드래그 활성화
        barChart.setScaleXEnabled(true); // X축 스크롤 활성화
        barChart.setVisibleXRangeMaximum(5); // 한 번에 표시되는 최대 막대 개수 설정
        barChart.getDescription().setEnabled(false); // 설명 비활성화
        barChart.getLegend().setEnabled(false); // 범례 비활성화
        barChart.invalidate(); // 그래프 갱신
    }

}
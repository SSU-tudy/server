package com.example.ssuwap.ui.todolist;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;
import com.example.ssuwap.data.todolist.TodolistData;
import com.example.ssuwap.data.todolist.TodotimeDBHelper;
import com.example.ssuwap.databinding.ActivityTodomainBinding;

import java.util.ArrayList;

public class TodomainActivity extends AppCompatActivity {
    private ArrayList<TodolistData> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TodotimeDBHelper dbHelper = new TodotimeDBHelper(this);
    private int i = 0;
    private LinearLayout timetableContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTodomainBinding binding = ActivityTodomainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Database Helper 초기화
        dbHelper = new TodotimeDBHelper(this);

        recyclerView = binding.rcvTodo;
        adapter = new TodomainAdapter(this, list, dbHelper);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        binding.btnAddrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.add(new TodolistData(i++, "테스트용" + i));
                adapter.notifyDataSetChanged(); // RecyclerView 업데이트
            }
        });

        timetableContainer = binding.timetableContainer;
        if (timetableContainer == null) {
            System.err.println("timetableContainer is null!");
            return;
        }
        initTimetable();
        colorTimeBlock(System.currentTimeMillis(),System.currentTimeMillis()+3600000,Color.parseColor("#FFCC80"));
    }

    private void initTimetable() {
        for (int hour = 0; hour < 24; hour++) {
            // FrameLayout 생성
            FrameLayout hourFrame = new FrameLayout(this);
            hourFrame.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    80 // 각 시간의 높이
            ));

            // 시간 숫자 표시
            TextView hourLabel = new TextView(this);
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
            LinearLayout minuteContainer = new LinearLayout(this);
            minuteContainer.setOrientation(LinearLayout.HORIZONTAL);
            FrameLayout.LayoutParams minuteParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            minuteParams.leftMargin = 60; // 시간 표시 공간 확보
            minuteContainer.setLayoutParams(minuteParams);

            // 60개의 분 셀 추가
            for (int minute = 0; minute < 60; minute++) {
                View cell = new View(this);
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

    private void colorTimeBlock(long startTimeMillis, long endTimeMillis, int color) {
        int startHour = (int) (startTimeMillis / (1000 * 60 * 60) % 24); // 24시간 단위
        int startMinute = (int) (startTimeMillis / (1000 * 60) % 60);
        int endHour = (int) (endTimeMillis / (1000 * 60 * 60) % 24);
        int endMinute = (int) (endTimeMillis / (1000 * 60) % 60);

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


}
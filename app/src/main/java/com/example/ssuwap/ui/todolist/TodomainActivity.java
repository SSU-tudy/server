package com.example.ssuwap.ui.todolist;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;
import com.example.ssuwap.data.todolist.TodolistData;
import com.example.ssuwap.data.todolist.TodotimeDBHelper;
import com.example.ssuwap.databinding.ActivityTodomainBinding;
import com.example.ssuwap.databinding.NewtodolistDialogBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class TodomainActivity extends AppCompatActivity implements TodomainAdapter.OnTimeBlockListener{
    private ArrayList<TodolistData> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TodotimeDBHelper dbHelper = new TodotimeDBHelper(this);
    private int i = 0;
    private LinearLayout timetableContainer;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTodomainBinding binding = ActivityTodomainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Database Helper 초기화
        dbHelper = new TodotimeDBHelper(this);

        recyclerView = binding.rcvTodo;
       // adapter = new TodomainAdapter(this, list, dbHelper, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        binding.btnAddrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemDialog();
                adapter.notifyDataSetChanged(); // RecyclerView 업데이트


            }
        });

        timetableContainer = binding.timetableContainer;
        if (timetableContainer == null) {
            System.err.println("timetableContainer is null!");
            return;
        }

        initTimetable();

    }

    public void onTimeBlockSelected(int startHour, int startMinute, int endHour, int endMinute, int color) {
        // colorTimeBlock 호출
        colorTimeBlock(startHour, startMinute, endHour, endMinute, color);
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

    private void addItemDialog() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = getLayoutInflater().inflate(R.layout.newtodolist_dialog, null);


        EditText editText = dialogView.findViewById(R.id.et_Input);
        ViewGroup colorContainer = dialogView.findViewById(R.id.ll_color);

        // 색상 목록 (10개)
        int[] colors = {
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
                Color.CYAN, Color.MAGENTA, Color.GRAY, Color.LTGRAY,
                Color.DKGRAY, Color.BLACK
        };

        final int[] selectedColor = {colors[0]}; // 선택된 색상 (기본값)

        // 동그라미 색 버튼 동적 생성
        for (int color : colors) {
            View colorCircle = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(70, 70); // 크기 설정
            params.setMargins(16, 16, 16, 16);
            colorCircle.setLayoutParams(params);
            colorCircle.setBackgroundColor(color);
            colorCircle.setOnClickListener(v -> selectedColor[0] = color); // 색 선택

            colorContainer.addView(colorCircle);
        }

        dialog = new AlertDialog.Builder(this).setView(inflater.inflate(R.layout.newtodolist_dialog, null))
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //list.add(new TodolistData(i++, "test" ,1));
                        Toast.makeText(TodomainActivity.this,"추가되었습니다", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("취소", null)
                .create();
        dialog.show();
    }

}
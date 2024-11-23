package com.example.ssuwap.ui.todolist;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuwap.R;
import com.example.ssuwap.data.todolist.TodolistData;
import com.example.ssuwap.databinding.FragmentTodoMainBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodoMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoMainFragment extends Fragment implements TodomainAdapter.OnTimeBlockListener, TodomainAdapter.SignalRunningListener{
    private FragmentTodoMainBinding binding;
    private ArrayList<TodolistData> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private TodomainAdapter adapter;
    private LinearLayout timetableContainer;
    private DatabaseReference databaseReference;
    private String userKey; // 유저 고유키
    private Handler handler; // 타이머 핸들러
    private Runnable timerRunnable;
    private long totalTime = 0; // 총 시간 (ms)

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TodoMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodoMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoMainFragment newInstance(String param1, String param2) {
        TodoMainFragment fragment = new TodoMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("UserInfo").child(userKey).child("TimeInfo");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodoMainBinding.inflate(inflater, container, false);

        // RecyclerView 설정
        recyclerView = binding.rcvTodo;
        adapter = new TodomainAdapter(requireContext(), list, this);
        adapter.setSignalRunningListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // 데이터 로드
        loadData();

        // 추가 버튼 클릭 리스너
        binding.btnAddrecord.setOnClickListener(v -> addItemDialog());
        // 타임테이블 초기화
        timetableContainer = binding.timetableContainer;
        initTimetable();
        calculateTotalTimeFromDB();
        fetchPieChart();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    ///얘 날짜 정보를 달력에서 뽑아와서 넣어야될듯 날짜뽑아서 매개변수로 주면 그날짜의 공부정보 다가꼬오니까 그걸 넣어라
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

    private void addItemDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.newtodolist_dialog, null);
        EditText editText = dialogView.findViewById(R.id.et_Input);

        // 색상 선택 동적 생성
        ViewGroup colorContainer = dialogView.findViewById(R.id.ll_color);
        int[] colors = {
                0xFFFBA1A1, // 불투명 빨강
                0xFFFFBD94, // 불투명 주황
                0xFFFBE3A1, // 불투명 노랑
                0xFFA1FBA4, // 불투명 초록
                0xFFA1D5FB, // 불투명 파랑
                0xFFFFCFF5, // 불투명 분홍
                0xFFC4C4C4, // 불투명 회색
                0xFFECFC92,  //연노랑
                0xFF479696  //청록
        };
        final int[] selectedColor = {colors[0]};
        for (int color : colors) {
            View colorCircle = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(70, 70);
            params.setMargins(13, 16, 13, 16);
            colorCircle.setLayoutParams(params);
            colorCircle.setBackgroundColor(color);
            colorCircle.setOnClickListener(v -> selectedColor[0] = color);
            colorContainer.addView(colorCircle);
        }
        // 다이얼로그 생성
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    String input = editText.getText().toString();
                    if (!input.isEmpty()) {
                        addItemToFirebase(input, selectedColor[0]);
                        Toast.makeText(requireContext(), "추가되었습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "할 일을 입력하세요", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .create();
        dialog.show();

    }

    private void addItemToFirebase(String subject, int color) {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        DatabaseReference dayReference = databaseReference.child(year).child(month).child(day);

        // 고유 Firebase 키 생성
        String uniqueKey = dayReference.push().getKey();

        // 새 아이템 데이터 생성
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("subject", subject);
        itemData.put("color", String.format("#%06X", (0xFFFFFF & color)));
        itemData.put("totalDuration", 0); // 초기 총 지속 시간은 0
        itemData.put("sessions", null);   // 세션 정보는 초기값 없음

        // Firebase에 저장
        dayReference.child(uniqueKey).setValue(itemData)
                .addOnSuccessListener(aVoid -> {
                    loadData(); // 데이터 다시 로드
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "항목 추가 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
    public void onTimeBlockSelected(int startHour, int startMinute, int endHour, int endMinute, int color) {
        colorTimeBlock(startHour, startMinute, endHour, endMinute, color);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && timerRunnable != null) {
            handler.removeCallbacks(timerRunnable); // 타이머 종료
        }
        binding = null;
    }
    private void stopTimer() {
        if (handler != null && timerRunnable != null) {
            Log.d("Timer", "stopTimer");
            handler.removeCallbacks(timerRunnable);
        }
    }

    private void startTimer(long initialTotalTime) {
        stopTimer();
        handler = new Handler();
        totalTime = initialTotalTime; // DB에서 계산된 초기 totalTime 사용

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                totalTime += 1000;
                updateTotalTime(totalTime);
                handler.postDelayed(this, 1000);
            }
        };
        Log.d("Timer", "startTimer");
        handler.post(timerRunnable);
    }

    private void updateTotalTime(long totalTime) {
        long hours = totalTime / (1000 * 60 * 60);
        long minutes = (totalTime / (1000 * 60)) % 60;
        long seconds = (totalTime / 1000) % 60;

        binding.totalTime.setText(String.format("총 학습 시간: %02d:%02d:%02d", hours, minutes, seconds));
    }

    private void calculateTotalTimeFromDB() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        databaseReference.child(year).child(month).child(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalTime = 0;
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Long duration = taskSnapshot.child("totalDuration").getValue(Long.class);
                    if (duration != null) {
                        totalTime += duration;
                    }
                }
                updateTotalTime(totalTime);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Timer", "Failed to calculate total time from DB: " + error.getMessage());
            }
        });
    }

    //리사이클러뷰 버튼눌림
    @Override
    public void onRunningStateChanged(TodolistData item, boolean isRunning) {
        if(isRunning) {
            calculateTotalTimeFromDB(); //totalTime 계산 from DB
            startTimer(totalTime); // 타이머 시작
        }
        else{
            stopTimer();
            calculateTotalTimeFromDB();
            fetchPieChart();
        }
    }
    private void fetchPieChart() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Long> subjectDurationMap = new HashMap<>();
                Map<String, Integer> subjectColorMap = new HashMap<>();

                for (DataSnapshot yearSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                        for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                            for (DataSnapshot taskSnapshot : daySnapshot.getChildren()) {
                                String subject = taskSnapshot.child("subject").getValue(String.class);
                                Long duration = taskSnapshot.child("totalDuration").getValue(Long.class);
                                String colorHex = taskSnapshot.child("color").getValue(String.class);

                                if (subject != null && duration != null && colorHex != null) {
                                    subjectDurationMap.put(subject,
                                            subjectDurationMap.getOrDefault(subject, 0L) + duration);
                                    subjectColorMap.putIfAbsent(subject, Color.parseColor(colorHex));
                                }
                            }
                        }
                    }
                }

                // 차트 생성
                createPieChart(subjectDurationMap, subjectColorMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "데이터 로드 실패: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createPieChart(Map<String, Long> subjectDurationMap, Map<String, Integer> subjectColorMap) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        float totalDuration = 0;

        for(Long duration : subjectDurationMap.values()){
            totalDuration+=duration;
        }

        for (Map.Entry<String, Long> entry : subjectDurationMap.entrySet()) {
            String subject = entry.getKey();
            float percentage = (entry.getValue() / totalDuration) * 100;
            entries.add(new PieEntry(percentage, subject)); // 퍼센트 사용
            colors.add(subjectColorMap.getOrDefault(subject, Color.GRAY));
        }

        PieDataSet dataSet = new PieDataSet(entries, "과목별 학습 시간");
        dataSet.setColors(colors); // subject별 색상 설정
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);

        // 파이 차트 설정
        binding.pieChart.setData(data);
        binding.pieChart.setCenterText("과목별 학습 시간");
        binding.pieChart.setCenterTextSize(12f);
        binding.pieChart.getDescription().setEnabled(false); // 설명 제거
        binding.pieChart.getLegend().setEnabled(false);
        binding.pieChart.animateY(1000); // 애니메이션
        binding.pieChart.invalidate(); // 갱신
    }

}
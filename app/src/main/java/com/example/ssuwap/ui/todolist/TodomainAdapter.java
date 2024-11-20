package com.example.ssuwap.ui.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;
import com.example.ssuwap.data.todolist.TodolistData;
import com.example.ssuwap.databinding.TodoListBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class TodomainAdapter extends RecyclerView.Adapter<TodomainAdapter.MyViewHolder> {

    private OnTimeBlockListener listener;
    private ArrayList<TodolistData> list;
    private Context context;
    private DatabaseReference databaseReference; // Firebase Database Reference
    private int playingPosition = -1; // 현재 재생 중인 항목의 위치
    private long startTime = 0;
    private long endTime = 0;

    public TodomainAdapter(Context context, ArrayList<TodolistData> list, OnTimeBlockListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Todolist");
    }

    public interface OnTimeBlockListener {
        void onTimeBlockSelected(int startHour, int startMinute, int endHour, int endMinute, int color);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TodoListBinding binding = TodoListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TodolistData item = list.get(position);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));

        // 데이터 상태에 따라 버튼 이미지를 설정
        if (item.isPlaying()) {
            holder.binding.btnTodo.setBackgroundResource(R.drawable.pause); // 실행 중인 상태
        } else {
            holder.binding.btnTodo.setBackgroundResource(R.drawable.play); // 정지 상태
        }

        holder.binding.btnTodo.setOnClickListener(v -> {
            if (playingPosition != -1 && playingPosition != item.getId()) {
                // 다른 아이템이 실행 중인 경우 종료 확인 다이얼로그
                showEndDialog(item, calendar, holder);
            } else if (item.isPlaying()) {
                // 현재 아이템 정지
                showEndDialog(item, calendar, holder);
            } else {
                // 현재 아이템 시작
                startTime = System.currentTimeMillis();
                item.setPlaying(true);
                holder.binding.btnTodo.setBackgroundResource(R.drawable.pause);
                playingPosition = item.getId();
                updateFirebase(item); // Firebase 업데이트
            }
        });
    }

    private void showEndDialog(TodolistData item, Calendar calendar, MyViewHolder holder) {
        new AlertDialog.Builder(context)
                .setTitle("종료")
                .setMessage("진행 중이던 공부를 마치시겠습니까?")
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    Toast.makeText(context, "공부시간이 기록되었습니다.", Toast.LENGTH_SHORT).show();

                    endTime = System.currentTimeMillis();
                    item.setPlaying(false);
                    playingPosition = -1;
                    holder.binding.btnTodo.setBackgroundResource(R.drawable.play);

                    // 시간 계산
                    calendar.setTimeInMillis(startTime);
                    int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                    int startMinute = calendar.get(Calendar.MINUTE);

                    calendar.setTimeInMillis(endTime);
                    int endHour = calendar.get(Calendar.HOUR_OF_DAY);
                    int endMinute = calendar.get(Calendar.MINUTE);

                    // 시간 블록 색칠
                    if (listener != null) {
                        listener.onTimeBlockSelected(startHour, startMinute, endHour, endMinute, Color.RED);
                    }

                    // Firebase에 세션 기록 추가
                    addSessionToFirebase(item, startTime, endTime);
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }

    private void updateFirebase(TodolistData item) {
        // Firebase 데이터베이스 업데이트
        databaseReference.child(String.valueOf(item.getId())).setValue(item)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "데이터 업데이트 성공"))
                .addOnFailureListener(e -> Log.e("Firebase", "데이터 업데이트 실패: " + e.getMessage()));
    }

    private void addSessionToFirebase(TodolistData item, long startTime, long endTime) {
        String sessionId = databaseReference.child(String.valueOf(item.getId())).child("sessions").push().getKey();
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("startTime", startTime);
        sessionData.put("endTime", endTime);

        databaseReference.child(String.valueOf(item.getId()))
                .child("sessions")
                .child(sessionId)
                .setValue(sessionData)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "세션 저장 성공"))
                .addOnFailureListener(e -> Log.e("Firebase", "세션 저장 실패: " + e.getMessage()));

        // 총 학습 시간 업데이트
        long duration = endTime - startTime;
        databaseReference.child(String.valueOf(item.getId()))
                .child("totalDuration")
                .setValue(item.getTotalDuration() + duration)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "총 학습 시간 업데이트 성공"))
                .addOnFailureListener(e -> Log.e("Firebase", "총 학습 시간 업데이트 실패: " + e.getMessage()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TodoListBinding binding;

        public MyViewHolder(TodoListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

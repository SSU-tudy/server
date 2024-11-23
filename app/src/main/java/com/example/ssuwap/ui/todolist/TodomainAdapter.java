package com.example.ssuwap.ui.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;
import com.example.ssuwap.data.todolist.TodolistData;
import com.example.ssuwap.databinding.TodoListBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class TodomainAdapter extends RecyclerView.Adapter<TodomainAdapter.MyViewHolder> {
    private String playingPosition = "NULL";// 현재 재생 중인 항목의 위치
    private OnTimeBlockListener listener;
    private ArrayList<TodolistData> list;
    private Context context;
    private DatabaseReference databaseReference;// Firebase Database Reference
    private long startTime = 0;
    private long endTime = 0;
    SignalRunningListener signalRunningListener;
    private static String tmpstr;
    private int tmpint;


    public TodomainAdapter(Context context, ArrayList<TodolistData> list, OnTimeBlockListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("TimeInfo");
    }


    public interface OnTimeBlockListener {
        void onTimeBlockSelected(int startHour, int startMinute, int endHour, int endMinute, int color);
    }

    public interface SignalRunningListener{
        void onRunningStateChanged(TodolistData item, boolean isRunning);
    }
    void setSignalRunningListener(SignalRunningListener listener){
        this.signalRunningListener = listener;
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
        Log.d("color", String.valueOf(item.getColor()));
        holder.binding.tvTodo.setText(item.getSubject()); //text뷰 설정
        updateButtonAppearance(holder, item);

        // 데이터 상태에 따라 버튼 이미지를 설정
//        if (item.isPlaying()) {
//            holder.binding.btnTodo.setBackgroundResource(R.drawable.pause); // 실행 중인 상태
//        } else {
//            holder.binding.btnTodo.setBackgroundResource(R.drawable.play); // 정지 상태
//        }

        holder.binding.btnTodo.setOnClickListener(v -> {
            if (!playingPosition.equals("NULL") && !playingPosition.equals(item.getKey())) {
                // 다른 아이템이 실행 중인 경우 종료 확인 토스트
                //Toast.makeText(context,"현재 학습을 종료해주세요", Toast.LENGTH_LONG).show();왜안댐
                Snackbar.make(holder.binding.getRoot(), "현재 학습을 종료해주세요", Snackbar.LENGTH_LONG).show();
            } else if (item.isPlaying()) {
                // 현재 아이템 정지
                showEndDialog(item, holder);
                notifyDataSetChanged();
            } else {
                // 현재 아이템 시작
                startTime = System.currentTimeMillis();
                item.setPlaying(true);
                //holder.binding.btnTodo.setBackgroundResource(R.drawable.pause);
                playingPosition = item.getKey();

                if (signalRunningListener != null) {
                    signalRunningListener.onRunningStateChanged(item, true);
                    Log.d("Timer", "onRunningStateChanged(item, true)");
                }

                Log.d("time2", playingPosition);
                updateButtonAppearance(holder, item);
                notifyDataSetChanged();
            }
        });
    }

    private void updateButtonAppearance(MyViewHolder holder, TodolistData item) {
        // 현재 버튼 배경 가져오기
        Log.d("color", String.valueOf(item.getColor()));
        Drawable drawable = AppCompatResources.getDrawable(context,
                item.isPlaying() ? R.drawable.pause : R.drawable.play); // 상태에 따라 이미지 선택
        if (drawable != null) {
            drawable.setColorFilter(item.getColor(), PorterDuff.Mode.SRC_IN); // 색상 적용
            holder.binding.btnTodo.setBackground(drawable); // 버튼 배경 업데이트
        }
    }

    private void showEndDialog(TodolistData item, MyViewHolder holder) {
        new AlertDialog.Builder(context)
                .setTitle("종료")
                .setMessage("진행 중이던 공부를 마치시겠습니까?")
                .setPositiveButton("확인", (dialogInterface, i) -> {

                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
                    if(signalRunningListener != null){
                        signalRunningListener.onRunningStateChanged(item, false);
                        Log.d("Timer", "onRunningStateChanged(item, false)");
                    }
                    Toast.makeText(context, "공부시간이 기록되었습니다.", Toast.LENGTH_SHORT).show();

                    updateButtonAppearance(holder, item);

                    endTime = System.currentTimeMillis();
                    item.setPlaying(false);
                    playingPosition = "NULL";
                    //holder.binding.btnTodo.setBackgroundResource(R.drawable.play);

                    // 시간 계산
                    calendar.setTimeInMillis(startTime);
                    int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                    int startMinute = calendar.get(Calendar.MINUTE);

                    calendar.setTimeInMillis(endTime);
                    int endHour = calendar.get(Calendar.HOUR_OF_DAY);
                    int endMinute = calendar.get(Calendar.MINUTE);

                    // 시간 블록 색칠
                    if (listener != null) {
                        listener.onTimeBlockSelected(startHour, startMinute, endHour, endMinute, item.getColor());
                    }

                    // TimeInfo에 세션 기록 추가
                    addSessionToFirebase(item.getKey(), startTime, endTime);
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }
    //파베에서 subject color가져오기
    private void fetchSubjectAndColor(String year, String month, String day, String taskKey) {
        // Firebase Database Reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("UserInfo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("TimeInfo")
                .child(year).child(month).child(day).child(taskKey);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // subject와 color 값 가져오기
                    String subject = snapshot.child("subject").getValue(String.class);
                    String color = snapshot.child("color").getValue(String.class);

                    if (subject != null && color != null) {
                        Log.d("FirebaseData", "Subject: " + subject + ", Color: " + color);
                        tmpint = Integer.parseInt(color);
                        tmpstr = subject;
                    } else {
                        Log.w("FirebaseData", "subject 또는 color 값이 null입니다.");
                    }
                } else {
                    Log.w("FirebaseData", "해당 taskKey의 데이터가 존재하지 않습니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "데이터 읽기 실패: " + error.getMessage());
            }
        });
    }

    private void addSessionToFirebase(String uniqueKey, long startTime, long endTime) {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        // databaseReference 초기화
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("UserInfo").child(userId).child("TimeInfo");

        DatabaseReference itemReference = databaseReference.child(year).child(month).child(day).child(uniqueKey);

        // 세션 데이터 추가
        String sessionId = itemReference.child("sessions").push().getKey();
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("startTime", startTime);
        sessionData.put("endTime", endTime);

        itemReference.child("sessions").child(sessionId).setValue(sessionData)
                .addOnSuccessListener(aVoid -> {
                    // 총 지속 시간 업데이트
                    long duration = endTime - startTime;
                    itemReference.child("totalDuration").setValue(ServerValue.increment(duration))
                            .addOnSuccessListener(aVoid1 -> Log.d("Firebase", "총 학습 시간 업데이트 성공"))
                            .addOnFailureListener(e -> Log.e("Fireblisase", "총 학습 시간 업데이트 실패: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("Firebase", "세션 저장 실패: " + e.getMessage()));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TodoListBinding binding;
        public MyViewHolder(TodoListBinding binding) {
            super(binding.getRoot());

            binding.tvTodo.setText(tmpstr);
            //binding.btnTodo.setBackgroundColor(tmpstr);
            this.binding = binding;
        }

    }
}

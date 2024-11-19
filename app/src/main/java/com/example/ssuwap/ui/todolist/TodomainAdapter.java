package com.example.ssuwap.ui.todolist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;
import com.example.ssuwap.data.todolist.TodoTimeData;
import com.example.ssuwap.data.todolist.TodolistData;
import com.example.ssuwap.data.todolist.TodotimeDBHelper;
import com.example.ssuwap.databinding.TodoListBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class TodomainAdapter extends RecyclerView.Adapter<TodomainAdapter.MyViewHolder> {

    private OnTimeBlockListener listener;
    private ArrayList<TodolistData> list;
    private Context context;
    private TodotimeDBHelper dbHelper;
    private int playingPosition = -1; // 현재 재생 중인 항목의 위치
    long startTime = 0;
    long endTime = 0;

    public TodomainAdapter(Context context, ArrayList<TodolistData> list, TodotimeDBHelper dbHelper, OnTimeBlockListener listener) {
        this.context = context;
        this.list = list;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }
    public  interface OnTimeBlockListener {
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
        // 대한민국 시간대 (KST) 설정
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));

        // 데이터 상태에 따라 버튼 이미지를 설정
        if (item.isPlaying()) {
            holder.binding.btnTodo.setBackgroundResource(R.drawable.pause); // 실행 중인 상태
        } else {
            holder.binding.btnTodo.setBackgroundResource(R.drawable.play); // 정지 상태
        }

        holder.binding.btnTodo.setOnClickListener(v -> {

            if (playingPosition != -1 && playingPosition != item.getId()) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("종료")
                        .setMessage("진행 중이던 공부를 마치시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "공부시간이 기록되었습니다.", Toast.LENGTH_SHORT).show();

                                endTime = System.currentTimeMillis();
                                item.setPlaying(false);
                                TodolistData data = list.get(playingPosition);
                                data.setPlaying(false);
                                notifyItemChanged(playingPosition);
                                playingPosition = -1;

                                //시작시간
                                calendar.setTimeInMillis(startTime);
                                int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                                int startMinute = calendar.get(Calendar.MINUTE);

                                //종료시간
                                calendar.setTimeInMillis(endTime);
                                int endHour = calendar.get(Calendar.HOUR_OF_DAY);
                                int endMinute = calendar.get(Calendar.MINUTE);

                                //칠하기
                                if (listener != null) {
                                    listener.onTimeBlockSelected(startHour, startMinute, endHour, endMinute, Color.RED); // 원하는 색상 지정
                                }

                                item.getTimeData().addSession(startTime, endTime);
                                dbHelper.addSession(item.getId(), startTime, endTime);
                                dbHelper.updateTotalDuration(item.getId(), endTime - startTime);

                            }
                        })
                        .setNegativeButton("취소", null)
                        .create();

                alertDialog.show();

            }
            else if (item.isPlaying()) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("종료")
                        .setMessage("진행 중이던 공부를 마치시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "공부시간이 기록되었습니다.", Toast.LENGTH_SHORT).show();

                                endTime = System.currentTimeMillis();
                                item.setPlaying(false);
                                playingPosition = -1;
                                holder.binding.btnTodo.setBackgroundResource(R.drawable.play);

                                //시작시간
                                calendar.setTimeInMillis(startTime);
                                int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                                int startMinute = calendar.get(Calendar.MINUTE);

                                //종료시간
                                calendar.setTimeInMillis(endTime);
                                int endHour = calendar.get(Calendar.HOUR_OF_DAY);
                                int endMinute = calendar.get(Calendar.MINUTE);

                                //칠하기
                                if (listener != null) {
                                    listener.onTimeBlockSelected(startHour, startMinute, endHour, endMinute, Color.RED); // 원하는 색상 지정
                                }

                                item.getTimeData().addSession(startTime, endTime);
                                dbHelper.addSession(item.getId(), startTime, endTime);
                                dbHelper.updateTotalDuration(item.getId(), endTime - startTime);
                            }
                        }).setNegativeButton("취소", null)
                        .create();
                alertDialog.show();
            }
            else {
                // 재생버튼으로 전환
                startTime = System.currentTimeMillis();

                item.setPlaying(true);
                holder.binding.btnTodo.setBackgroundResource(R.drawable.pause);
                playingPosition = item.getId();
            }
        });
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
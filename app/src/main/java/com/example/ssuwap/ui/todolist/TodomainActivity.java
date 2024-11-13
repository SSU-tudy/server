package com.example.ssuwap.ui.todolist;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.data.todolist.todolistData;
import com.example.ssuwap.databinding.ActivityTodomainBinding;

import java.util.ArrayList;

public class TodomainActivity extends AppCompatActivity {
    private ArrayList<todolistData> list ;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<todolistData> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTodomainBinding binding = ActivityTodomainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list.add(new todolistData("테스트용입니다"));
        list.add(new todolistData("테스토스테론입니다"));
        list.add(new todolistData("테다"));


        recyclerView = binding.rcvTodo;
        adapter = new TodomainAdapter(list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }
    private int studyMinutes = 0;  // 누적된 공부 시간

    //하이라이트 함수
    private void recordStudyTime(int minutes) {
        studyMinutes += minutes;
        int hourIndex = studyMinutes / 60;  // 시간대
        int blockIndex = (studyMinutes % 60) / 20;  // 20분 단위 블록 인덱스

        if (hourIndex < 24 && blockIndex < 5) {  // 24시간 범위와 블록 5개 범위 내인지 확인
            String blockId = "block_" + hourIndex + "_" + blockIndex;
            int resId = getResources().getIdentifier(blockId, "id", getPackageName());
            TextView blockView = findViewById(resId);

            if (blockView != null) {
                blockView.setBackgroundColor(ContextCompat.getColor(this, R.color.test));
            }
        }
    }

}
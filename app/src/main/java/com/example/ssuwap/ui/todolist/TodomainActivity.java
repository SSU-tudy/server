package com.example.ssuwap.ui.todolist;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ssuwap.R;
import com.example.ssuwap.databinding.ActivityTodomainBinding;

public class TodomainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTodomainBinding binding = ActivityTodomainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    private int studyMinutes = 0;  // 누적된 공부 시간

    private void recordStudyTime(int minutes) {
        studyMinutes += minutes;
        int hourIndex = studyMinutes / 60;  // 시간대
        int blockIndex = (studyMinutes % 60) / 20;  // 20분 단위 블록 인덱스

        if (hourIndex < 24 && blockIndex < 5) {  // 24시간 범위와 블록 5개 범위 내인지 확인
            String blockId = "block_" + hourIndex + "_" + blockIndex;
            int resId = getResources().getIdentifier(blockId, "id", getPackageName());
            TextView blockView = findViewById(resId);

            if (blockView != null) {
                blockView.setBackgroundColor(ContextCompat.getColor(this, R.color.study_highlight));
            }
        }
    }

}
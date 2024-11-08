package com.example.ssuwap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.HorizontalScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ssuwap.databinding.ActivityBuyingBinding;

import java.util.ArrayList;

public class BuyingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBuyingBinding binding = ActivityBuyingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<TagData> taglist = new ArrayList<>();
        taglist.add(new TagData("2학년"));
        taglist.add(new TagData("2학기"));
        taglist.add(new TagData("2모여기공기밥추가요"));

        binding.rvTag.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvTag.setAdapter(new TaglistAdaptor(taglist));
    }
}
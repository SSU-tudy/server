package com.example.ssuwap;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ssuwap.databinding.ActivityMainPostBinding;

public class MainPostActivity extends AppCompatActivity {

    private ActivityMainPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // hey o
    }
}
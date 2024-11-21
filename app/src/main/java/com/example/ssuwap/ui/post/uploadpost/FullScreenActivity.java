package com.example.ssuwap.ui.post.uploadpost;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.databinding.ActivityFullScreenBinding;
import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenActivity extends AppCompatActivity {

    ActivityFullScreenBinding binding;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("PostAdaptor", "FullScreenActivity start()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PostAdaptor", "FullScreenActivity");
        binding = ActivityFullScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PhotoView photoView = binding.fullScreenImageView;

        String imageUrl = getIntent().getStringExtra("imageUrl");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Glide를 사용해 URL로 이미지 로드
            Glide.with(this)
                    .load(imageUrl)
                    .into(photoView);
        }
    }
}
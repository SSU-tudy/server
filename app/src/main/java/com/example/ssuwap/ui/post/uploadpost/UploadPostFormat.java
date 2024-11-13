package com.example.ssuwap.ui.post.uploadpost;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.databinding.ActivityUploadPostFormatBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class UploadPostFormat extends AppCompatActivity {

    private ActivityUploadPostFormatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadPostFormatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.uploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String detailPost = binding.postDetailInfo.getText().toString();

                Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.mipmap.posttestimage);
                String uriString = imageUri.toString();
                uploadImageToFirebase(imageUri);
            }
        });


    }

    private void uploadImageToFirebase(Uri imageUri) {
        // Firebase Storage 경로 설정
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString());

        // 파일 업로드
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            // 업로드 성공 시 다운로드 URL 가져오기
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.d("Firebase", "Image uploaded: " + downloadUrl);
                // 필요시 다운로드 URL을 DB에 저장
            });
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Image upload failed", e);
        });
    }
}
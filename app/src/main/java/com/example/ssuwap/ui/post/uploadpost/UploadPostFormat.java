package com.example.ssuwap.ui.post.uploadpost;

import static java.util.Collections.rotate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.databinding.ActivityUploadPostFormatBinding;
import com.example.ssuwap.ui.book.upload.isbn.UploadBookScan;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class UploadPostFormat extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private String imageFilePath;
    private Uri photoUri;

    private ActivityUploadPostFormatBinding binding;

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == Activity.RESULT_OK){
                        Bundle extras = o.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        binding.uploadImagePost.setImageBitmap(imageBitmap);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadPostFormatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.scanPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(UploadPostFormat.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UploadPostFormat.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    openCamera();
                }
            }
        });

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

    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityResult.launch(takePictureIntent);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("check", "onRequestPermissionsResult: Camera permission granted");
                openCamera();
            } else {
                Log.d("check", "onRequestPermissionsResult: Camera permission denied");
                Toast.makeText(this, "Camera permission is required to scan ISBN", Toast.LENGTH_SHORT).show();
            }
        }
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
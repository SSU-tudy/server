package com.example.ssuwap.ui.post.uploadpost.comment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ssuwap.R;
import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.databinding.ActivityUploadCommentFormatBinding;
import com.example.ssuwap.ui.post.uploadpost.UploadPostFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class UploadCommentFormat extends AppCompatActivity {

    ActivityUploadCommentFormatBinding binding;

    private static final String TAG = "UploadCommentFormat";
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Uri photoUri;
    private String photoURL;
    private Bitmap imageBitmap;
    private String userName;

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "start camera");
                        Bundle extras = o.getData().getExtras();
                        imageBitmap = (Bitmap) extras.get("data");
                        binding.uploadImagePost.setImageBitmap(imageBitmap);
                        photoUri = saveBitmapAndGetUri(imageBitmap);
                        Log.d(TAG, "photoUri : " + photoUri);
                        uploadImageToFirebase(photoUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadCommentFormatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.scanCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(UploadCommentFormat.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UploadCommentFormat.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    openCamera();
                }
            }
        });

        getUserNameForFirebase();

        //firebase에 저장할 경로 입력 : 여기서는 PostInfo라는 곳에 업로드를 할예정

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PostInfo");
        DatabaseReference postRef = databaseReference.child(getIntent().getStringExtra("postID"));
        binding.uploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String detailPost = binding.commentDetailInfo.getText().toString();
                Log.d(TAG, "detailPost : " + detailPost);

                DatabaseReference commentRef = postRef.child("comments").push();
                String postId = commentRef.getKey();  // 자동 생성된 ID 가져오기
                Log.d(TAG, "postID"+postId);
                //서버로 올릴 데이터 객체로 포장
                CommentInfo commentInfo = new CommentInfo(userName, postId, detailPost, photoURL);

                //위에서 저장한 경로에 올린다.
                commentRef.setValue(commentInfo).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Post added successfully!");
                    } else {
                        Log.e(TAG, "Failed to add post", task.getException());
                    }
                });

                finish();
            }
        });
    }

    private void getUserNameForFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid()).child("studentName");
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userId = task.getResult().getValue(String.class);
                    if (userId != null) {
                        userName = userId;
                    } else {
                        Log.d("UploadPostFormat", "User ID가 존재하지 않습니다.");
                    }
                } else {
                    Log.d("UploadPostFormat", "User ID 가져오기 실패.");
                }
            });
        }
    }

    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityResult.launch(takePictureIntent);
        }
    }

    private Uri saveBitmapAndGetUri(Bitmap bitmap) {
        try {
            // 임시 파일 생성
            Log.d("UploadPostFormat", "Start saveBitmapAndGetUri");
            File tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg");

            Log.d("UploadPostFormat", "File path: " + tempFile.getAbsolutePath());

            if (tempFile.exists()) {
                Log.d("UploadPostFormat", "Temp file exists");
            } else {
                Log.d("UploadPostFormat", "Temp file does not exist, attempting to create");
            }

            FileOutputStream outputStream = new FileOutputStream(tempFile);

            if(outputStream == null) Log.d("UploadPostFormat", "nothing");
            else Log.d("UploadPostFormat", "successful");

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Uri uri = FileProvider.getUriForFile(this, "com.example.ssuwap.ui.post.uploadpost.fileprovider", tempFile);
            Log.d("UploadPostFormat", "URI generated: " + uri);
            return uri;
        } catch (IOException e) {
            Log.e("UploadPostFormat", "Error in saveBitmapAndGetUri: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            Log.e("UploadPostFormat", "FileProvider URI generation failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        Log.d("UploadPostFormat", "uploadImageToFirebase()");
        // Firebase Storage 경로 설정
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString());

        // 파일 업로드
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            // 업로드 성공 시 다운로드 URL 가져오기
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                photoURL = uri.toString();
                Log.d("UploadPostFormat", "Image uploaded: " + photoURL);
                // 필요시 다운로드 URL을 DB에 저장
            });
        }).addOnFailureListener(e -> {
            Log.e("UploadPostFormat", "Image upload failed", e);
        });
    }
}
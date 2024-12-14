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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.databinding.ActivityUploadPostFormatBinding;
import com.example.ssuwap.ui.book.buying.chat.ChatActivity;
import com.example.ssuwap.ui.book.upload.isbn.UploadBookScan;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class UploadPostFormat extends AppCompatActivity implements TagDialogFragment.TagDialogListener{

    private static final String TAG = "UploadPostFormat";

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Uri photoUri;
    private String photoURL;
    private Bitmap imageBitmap;
    private String userName;
    private String userInfoId;
    private String userSemester;
    private ArrayList<String> tagList;


    private ActivityUploadPostFormatBinding binding;

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == Activity.RESULT_OK){
                        Log.d("UploadPostFormat", "start camera");
                        Bundle extras = o.getData().getExtras();
                        imageBitmap = (Bitmap) extras.get("data");
                        binding.uploadImagePost.setImageBitmap(imageBitmap);
                        photoUri = saveBitmapAndGetUri(imageBitmap);
                        Log.d("UploadPostFormat", "photoUri : " + photoUri);
                        uploadImageToFirebase(photoUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("UploadPostFormat", "onCreate()");
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

        getUserInfoForFirebase();
        if(tagList == null) tagList = new ArrayList<>();

        binding.postTag1.setVisibility(View.GONE);
        binding.postTag2.setVisibility(View.GONE);
        binding.postTag3.setVisibility(View.GONE);
        binding.uploadPostButton.setEnabled(false);


        //firebase에 저장할 경로 입력 : 여기서는 PostInfo라는 곳에 업로드를 할예정
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PostInfo");
        binding.uploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String detailPost = binding.postDetailInfo.getText().toString();
                Log.d("UploadPostFormat", "detailPost : " + detailPost);
                Log.d("UploadPostFormat", "URL : " + photoURL);
                Log.d(TAG, "Tag1 : " + tagList.get(0));
                Log.d(TAG, "Tag2 : " + tagList.get(1));
                Log.d(TAG, "Tag3 : " + tagList.get(2));

                DatabaseReference postRef = databaseReference.push();
                String postId = postRef.getKey();  // 자동 생성된 ID 가져오기
                Log.d("UploadPostFormat", "name : " + userName);
                //서버로 올릴 데이터 객체로 포장
                PostInfo postInfo = new PostInfo(userInfoId,userName, postId ,photoURL, detailPost, tagList.get(0), tagList.get(1), tagList.get(2),new HashMap<>());

                //위에서 저장한 경로에 올린다.
                postRef.setValue(postInfo).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("UploadPostFormat", "Post added successfully!");
                    } else {
                        Log.e("UploadPostFormat", "Failed to add post", task.getException());
                    }
                });
                finish();
            }
        });


        binding.addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TagDialogFragment dialog = TagDialogFragment.newInstance(userSemester);
                dialog.show(getSupportFragmentManager(), "TagDialog");
            }
        });

    }

    private void getUserInfoForFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        Log.d(TAG, ""+firebaseUser.getUid());

        if (firebaseUser != null) {
            userInfoId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid()).child("studentName");
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userId = task.getResult().getValue(String.class);
                    if (userId != null) {
                        userName = userId;
                        Log.d("UploadPostFormat", userName);
                    } else {
                        Log.d("UploadPostFormat", "User ID가 존재하지 않습니다.");
                    }
                } else {
                    Log.d("UploadPostFormat", "User ID 가져오기 실패.");
                }
            });

            userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid()).child("semester");
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userSem = task.getResult().getValue(String.class);
                    if (userSem != null) {
                        userSemester = userSem;
                        Log.d(TAG, userSemester);
                    } else {
                        Log.d(TAG, "User Semester가 존재하지 않습니다.");
                    }
                } else {
                    Log.d(TAG, "User Semester 가져오기 실패.");
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("UploadPostFormat", "onRequestPermissionsResult: Camera permission granted");
                openCamera();
            } else {
                Log.d("UploadPostFormat", "onRequestPermissionsResult: Camera permission denied");
                Toast.makeText(this, "Camera permission is required to scan ISBN", Toast.LENGTH_SHORT).show();
            }
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

    @Override
    public void onTagSelected(String grade, String subject) {

        tagList.add(grade);
        tagList.add(userSemester);
        tagList.add(subject);

        binding.initialText.setVisibility(View.GONE);
        binding.postTag1.setVisibility(View.VISIBLE);
        binding.postTag2.setVisibility(View.VISIBLE);
        binding.postTag3.setVisibility(View.VISIBLE);

        binding.postTag1.setText(grade);
        binding.postTag2.setText(userSemester);
        binding.postTag3.setText(subject);

        // 태그 리스트 크기에 따라 버튼 상태 업데이트
        binding.uploadPostButton.setEnabled(tagList.size() >= 3);
    }
}
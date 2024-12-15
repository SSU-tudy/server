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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.databinding.ActivityUploadCommentFormatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class UploadCommentFormat extends AppCompatActivity {

    ActivityUploadCommentFormatBinding binding;

    private static final String TAG = "UploadCommentFormat";
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Uri photoUri;
    private String photoURL;
    private Bitmap imageBitmap;
    private String userName;
    private String userInfoId;
    private HashMap<String, Integer> userIndexMap;

    private DatabaseReference databaseReference;
    private DatabaseReference postRef;

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

        if(userIndexMap == null){
            userIndexMap = new HashMap<>();
        }

        getUserNameForFirebase();

        binding.uploadPostButton.setEnabled(false);

        databaseReference = FirebaseDatabase.getInstance().getReference("PostInfo");
        postRef = databaseReference.child(getIntent().getStringExtra("postID"));
        binding.uploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadComment();
            }
        });
    }

    private void getUserNameForFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            userInfoId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid()).child("studentName");
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userId = task.getResult().getValue(String.class);
                    if (userId != null) {
                        userName = userId;
                    } else {
                        Log.d(TAG, "User ID가 존재하지 않습니다.");
                    }
                } else {
                    Log.d(TAG, "User ID 가져오기 실패.");
                }
            });
        }

        loadFirebaseData();
    }

    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityResult.launch(takePictureIntent);
        }
    }

    private Uri saveBitmapAndGetUri(Bitmap bitmap) {
        try {
            Log.d(TAG, "Start saveBitmapAndGetUri");
            File tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg");
            Log.d(TAG, "File path: " + tempFile.getAbsolutePath());

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Uri uri = FileProvider.getUriForFile(this, "com.example.ssuwap.ui.post.uploadpost.fileprovider", tempFile);
            Log.d(TAG, "URI generated: " + uri);
            return uri;
        } catch (IOException e) {
            Log.e(TAG, "Error in saveBitmapAndGetUri: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "FileProvider URI generation failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        Log.d(TAG, "uploadImageToFirebase()");
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString());

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                photoURL = uri.toString();
                Log.d(TAG, "Image uploaded: " + photoURL);
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Image upload failed", e);
        });
    }

    private void loadFirebaseData() {
        Log.d(TAG, "loadFirebaseData()");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("PostInfo");

        String postID = getIntent().getStringExtra("postID");
        DatabaseReference userIndexMapRef = databaseReference.child(postID).child("userIndexMap");

        userIndexMapRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot userIndexMapSnapshot = task.getResult();
                if (userIndexMapSnapshot.exists()) {
                    userIndexMap = new HashMap<>();
                    for (DataSnapshot entry : userIndexMapSnapshot.getChildren()) {
                        String key = entry.getKey();
                        Integer value = entry.getValue(Integer.class);
                        if (key != null && value != null) {
                            userIndexMap.put(key, value);
                        }
                    }
                    Log.d(TAG, "Loaded userIndexMap: " + userIndexMap.toString());
                } else {
                    Log.w(TAG, "userIndexMap does not exist. Initializing...");
                    userIndexMap = new HashMap<>();
                    userIndexMapRef.setValue(userIndexMap).addOnCompleteListener(initTask -> {
                        if (initTask.isSuccessful()) {
                            Log.d(TAG, "Initialized userIndexMap with empty HashMap");
                        } else {
                            Log.e(TAG, "Failed to initialize userIndexMap", initTask.getException());
                        }
                    });
                }

                binding.uploadPostButton.setEnabled(true);
                Log.d(TAG, "uploadPostButton enabled");
            } else {
                Log.e(TAG, "Failed to load userIndexMap", task.getException());
            }
        });
    }

    private void uploadComment() {
        String detailPost = binding.commentDetailInfo.getText().toString();
        Log.d(TAG, "detailPost : " + detailPost);

        DatabaseReference commentRef = postRef.child("comments").push();
        String postId = commentRef.getKey();
        Log.d(TAG, "postID"+postId);

        int index;
        if (!userIndexMap.containsKey(userInfoId)) {
            index = userIndexMap.size() + 1;
            userIndexMap.put(userInfoId, index);
            postRef.child("userIndexMap").setValue(userIndexMap);
        } else {
            index = userIndexMap.get(userInfoId);
        }

        CommentInfo commentInfo = new CommentInfo(userInfoId, userName, postId, detailPost, photoURL, String.valueOf(index));

        commentRef.setValue(commentInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Comment added successfully!");
                finish();
            } else {
                Log.e(TAG, "Failed to add comment", task.getException());
            }
        });
    }
}

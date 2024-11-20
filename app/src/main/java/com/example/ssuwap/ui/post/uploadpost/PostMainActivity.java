package com.example.ssuwap.ui.post.uploadpost;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ssuwap.R;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.databinding.ActivityPostMainBinding;
import com.example.ssuwap.ui.post.uploadpost.comment.UploadCommentFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class PostMainActivity extends AppCompatActivity {
    ArrayList<PostInfo> list;
    ActivityPostMainBinding binding;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private PostAdaptor postAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PostMainActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        binding = ActivityPostMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = new ArrayList<>();


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("PostInfo");

        postAdaptor = new PostAdaptor(this, list);  // 어댑터 초기화
        binding.postRV.setLayoutManager(new LinearLayoutManager(this));
        binding.postRV.setAdapter(postAdaptor);  // 어댑터 설정

        binding.postUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostMainActivity.this, UploadPostFormat.class));
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear(); // 기존 데이터 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        // PostInfo 객체 생성
                        PostInfo postInfo = snapshot.getValue(PostInfo.class);

                        if (postInfo != null) {
                            // comments 필드를 ArrayList로 변환
                            ArrayList<CommentInfo> commentsList = new ArrayList<>();
                            Map<String, CommentInfo> commentsMap = postInfo.getComments();
                            if (commentsMap != null) {
                                commentsList.addAll(commentsMap.values());
                            }

                            // 변환된 commentsList를 postInfo 객체에 설정
                            postInfo.setCommentsList(commentsList);

                            // 게시물 정보를 리스트에 추가
                            Log.d("PostMainActivity", "dataLoad : " + postInfo.getDescription());
                            list.add(postInfo);
                        } else {
                            Log.e("PostMainActivity", "Null PostInfo detected");
                        }

                    } catch (DatabaseException e) {
                        Log.e("PostMainActivity", "Error loading data: " + e.getMessage(), e);
                    }
                }
                // 데이터 로드 후 어댑터 갱신
                postAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostMainActivity", "Firebase error: " + error.toException());
            }
        });
    }
}
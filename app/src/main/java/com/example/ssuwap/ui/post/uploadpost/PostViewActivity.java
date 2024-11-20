package com.example.ssuwap.ui.post.uploadpost;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.databinding.ActivityPostViewBinding;
import com.example.ssuwap.ui.post.uploadpost.comment.CommentAdaptor;
import com.example.ssuwap.ui.post.uploadpost.comment.UploadCommentFormat;

import java.util.ArrayList;

public class PostViewActivity extends AppCompatActivity {

    private ActivityPostViewBinding binding;
    private PostInfo postInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 인텐트로부터 PostInfo 객체를 받아옴
        postInfo = getIntent().getParcelableExtra("PostData");

        if (postInfo != null) {
            // 전달된 postInfo 객체의 URL과 설명을 로그로 출력
            Log.d("PostViewActivity", "Image URL: " + postInfo.getImageUrl());
            Log.d("PostViewActivity", "Detail: " + postInfo.getDescription());

            // Glide를 사용하여 이미지를 로드하고, 이미지 뷰에 설정
            Glide.with(this)
                    .load(postInfo.getImageUrl())
                    .into(binding.postViewImage);  // ImageView에 이미지 로드

            // TextView에 설명 설정
            binding.postViewText.setText(postInfo.getDescription());  // 설명 텍스트 설정

            // Comments를 가져와서 RecyclerView에 설정
            ArrayList<CommentInfo> comments = postInfo.getCommentsList(); // commentsList 가져오기
            binding.commentRV.setLayoutManager(new LinearLayoutManager(this));
            binding.commentRV.setAdapter(new CommentAdaptor(this, comments));

        } else {
            // postInfo가 null인 경우 로깅
            Log.e("PostViewActivity", "PostInfo is null, failed to load data.");
        }

        // 댓글 추가 버튼 클릭 리스너 설정
        binding.commentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postInfo != null) {
                    startActivity(new Intent(PostViewActivity.this, UploadCommentFormat.class)
                            .putExtra("postID", postInfo.getPostID()));
                } else {
                    Log.e("PostViewActivity", "PostInfo is null, unable to post comment.");
                }
            }
        });

        binding.postViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostViewActivity.this, FullScreenActivity.class);
                intent.putExtra("imageUrl", postInfo.getImageUrl()); // 이미지 URL 전달
                startActivity(intent);
            }r
        });
    }
}

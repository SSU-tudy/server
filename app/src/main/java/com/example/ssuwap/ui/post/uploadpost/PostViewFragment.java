package com.example.ssuwap.ui.post.uploadpost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.databinding.FragmentPostViewBinding;
import com.example.ssuwap.ui.post.uploadpost.comment.CommentAdaptor;
import com.example.ssuwap.ui.post.uploadpost.comment.UploadCommentFormat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostViewFragment extends Fragment {

    FragmentPostViewBinding binding;
    private PostInfo postInfo;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PostViewFragment";
    private static final String ARG_POST_INFO = "PostInfo";

    private ArrayList<CommentInfo> list;  // 댓글 리스트 선언
    private CommentAdaptor adaptor;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostViewFragment newInstance(String param1, String param2, PostInfo postInfo) {
        PostViewFragment fragment = new PostViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putParcelable(ARG_POST_INFO, postInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            postInfo = getArguments().getParcelable(ARG_POST_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        binding = FragmentPostViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFirebaseData();

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
            list = new ArrayList<>();  // 댓글 리스트 초기화
            adaptor = new CommentAdaptor(requireContext(), list);  // 어댑터 초기화

            binding.commentRV.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.commentRV.setAdapter(adaptor);

        } else {
            // postInfo가 null인 경우 로깅
            Log.e("PostViewActivity", "PostInfo is null, failed to load data.");
        }

        binding.commentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postInfo != null) {
                    startActivity(new Intent(requireContext(), UploadCommentFormat.class)
                            .putExtra("postID", postInfo.getPostID()));
                } else {
                    Log.e("PostViewActivity", "PostInfo is null, unable to post comment.");
                }
            }
        });

        binding.postViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), FullScreenActivity.class)
                        .putExtra("imageUrl", postInfo.getImageUrl()));
            }
        });
    }

    private void loadFirebaseData() {
        Log.d("PostMainActivity", "loadFirebaseData()");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("PostInfo").child(postInfo.getPostID()).child("comments");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // 새 댓글이 추가될 때마다 호출됩니다.
                CommentInfo commentInfo = dataSnapshot.getValue(CommentInfo.class);
                if (commentInfo != null) {
                    list.add(commentInfo);  // 새로운 댓글을 리스트에 추가
                    adaptor.notifyItemInserted(list.size() - 1);  // 어댑터에 추가된 아이템을 알려줍니다.
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // 댓글이 변경될 때마다 호출됩니다.
                CommentInfo commentInfo = dataSnapshot.getValue(CommentInfo.class);
                if (commentInfo != null) {
                    // 기존 댓글을 업데이트하는 로직을 추가할 수 있습니다.
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // 댓글이 삭제될 때마다 호출됩니다.
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // 댓글의 순서가 변경될 때마다 호출됩니다.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostMainActivity", "Firebase error: " + databaseError.toException());
            }
        });
    }

}
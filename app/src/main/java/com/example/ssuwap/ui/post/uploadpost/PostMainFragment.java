package com.example.ssuwap.ui.post.uploadpost;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ssuwap.R;
import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.databinding.ActivityPostMainBinding;
import com.example.ssuwap.databinding.FragmentPostMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostMainFragment extends Fragment {

    ArrayList<PostInfo> list;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adaptor;
    private RecyclerView.LayoutManager layoutManager;
    FragmentPostMainBinding binding;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostMainFragment newInstance(String param1, String param2) {
        Log.d("PostMainActivity","newInstance()");
        PostMainFragment fragment = new PostMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PostMainActivity","onCreate()");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("PostMainActivity","onCreateView()");
        binding = FragmentPostMainBinding.inflate(inflater, container, false);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("PostMainActivity","onViewCreated()");
        recyclerView = binding.postRV;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        adaptor = new PostAdaptor(requireContext(), list, requireActivity().getSupportFragmentManager());
        recyclerView.setAdapter(adaptor);

        loadFirebaseData();
    }

    private void loadFirebaseData(){
        Log.d("PostMainActivity","loadFirebaseData()");

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("PostInfo");
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
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostMainActivity", "Firebase error: " + error.toException());
            }
        });
    }

}
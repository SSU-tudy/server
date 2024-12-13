package com.example.ssuwap.ui.profile;

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

import com.example.ssuwap.R;
import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.ui.post.uploadpost.PostAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilePostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ProfilePostFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private RecyclerView.Adapter adapter;

    private String userName;
    private ArrayList<PostInfo> list;

    public ProfilePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilePostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilePostFragment newInstance(String param1, String param2) {
        ProfilePostFragment fragment = new ProfilePostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = new ArrayList<>();

        getUserNameForFirebase();
        getMyPostForFirebase();

        RecyclerView recyclerView = view.findViewById(R.id.myPostRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PostAdaptor(requireContext(), list, requireActivity().getSupportFragmentManager());
        recyclerView.setAdapter(adapter);
    }

    private void getUserNameForFirebase(){
        Log.d(TAG, "getUserNameForFirebase()");
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
                        Log.d(TAG, "User ID가 존재하지 않습니다.");
                    }
                } else {
                    Log.d(TAG, "User ID 가져오기 실패.");
                }
                Log.d(TAG, "userName : "+userName);
            });
        }
        else {
            Log.d(TAG, "firebaseUser null");
        }
    }

    private void getMyPostForFirebase(){

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("PostInfo");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    try {
                        PostInfo postInfo = snapshot.getValue(PostInfo.class);
                        if(postInfo != null){
                            if(Objects.equals(postInfo.getUserName(), userName)){
                                Log.d(TAG, "my name : "+userName+" Other Post" + postInfo.getUserName());
                                ArrayList<CommentInfo> commentList = new ArrayList<>();
                                Map<String, CommentInfo> commentInfoMap = postInfo.getComments();

                                if(commentInfoMap != null){
                                    commentList.addAll(commentInfoMap.values());
                                }

                                postInfo.setCommentsList(commentList);
                                list.add(postInfo);
                                Log.d(TAG, postInfo.getDescription());
                            }
                            else{
                                Log.d(TAG, "my name : "+userName+" Other Post" + postInfo.getUserName());
                            }
                        }
                        else {
                            Log.e(TAG, "Null PostInfo detected");
                        }

                    } catch (DatabaseException e){
                        Log.e(TAG, "Error loading data: " + e.getMessage(), e);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.toException());
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        if (databaseReference != null && valueEventListener != null) {
            databaseReference.addValueEventListener(valueEventListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (databaseReference != null && valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}
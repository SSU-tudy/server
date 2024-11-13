package com.example.ssuwap.ui.post.uploadpost;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ssuwap.R;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.databinding.ActivityPostMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostMainActivity extends AppCompatActivity {
    ArrayList<PostInfo> list;
    ActivityPostMainBinding binding;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private PostAdaptor postAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = new ArrayList<>();


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("PostInfo");

        postAdaptor = new PostAdaptor(this, list);  // 어댑터 초기화
        binding.postRV.setLayoutManager(new LinearLayoutManager(this));
        binding.postRV.setAdapter(postAdaptor);  // 어댑터 설정


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PostInfo postInfo = snapshot.getValue(PostInfo.class);
                    if (postInfo != null) {
                        Log.d("PostMainActivity", "dataLoad : "+postInfo.getDescription());
                        list.add(postInfo);
                    }
                    postAdaptor.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostMainActivity", "Firebase error: " + error.toException());
            }
        });


        if (list == null) {
            Log.d("PostAdaptor", "List is null");
        } else if (list.isEmpty()) {
            Log.d("PostAdaptor", "List is empty");
        } else {
            Log.d("PostAdaptor", "List size: " + list.size());
        }
    }
}
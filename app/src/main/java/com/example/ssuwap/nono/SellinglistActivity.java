package com.example.ssuwap.nono;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.databinding.ActivitySellinglistAvtivityBinding;
import com.example.ssuwap.ui.book.selling.SellingAdaptor;
import com.example.ssuwap.ui.book.upload.UploadBookFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellinglistActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BookInfo> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySellinglistAvtivityBinding binding = ActivitySellinglistAvtivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.sellingrv;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>();

        // 어댑터 초기화 및 연결
        adapter = new SellingAdaptor(arrayList, SellinglistActivity.this);
        recyclerView.setAdapter(adapter);
        Log.d("SellinglistActivity", "Adapter attached to RecyclerView");

        // Firebase 데이터 로드
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("BookInfo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BookInfo bookInfo = snapshot.getValue(BookInfo.class);
                    if (bookInfo != null) {
                        Log.d("SellinglistActivity", "dataLoad");
                        arrayList.add(bookInfo);
                    }
                }
                Log.d("SellinglistActivity", "Data loaded from Firebase, arrayList size: " + arrayList.size());
                adapter.notifyDataSetChanged();  // 데이터 변경 알림
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SellinglistActivity", "Firebase error: " + error.toException());
            }
        });

        binding.sellingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 판매하러가기 실행
                startActivity(new Intent(SellinglistActivity.this, UploadBookFormat.class));
            }
        });
    }
}
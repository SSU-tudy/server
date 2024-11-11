package com.example.ssuwap.ui.book.selling;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;
import com.example.ssuwap.data.SellingListData;
import com.example.ssuwap.data.TagData;
import com.example.ssuwap.databinding.ActivityBuyingBinding;
import com.example.ssuwap.ui.book.TaglistAdaptor;
import com.example.ssuwap.databinding.ActivitySellinglistAvtivityBinding;
import com.google.firebase.Firebase;
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
    private ArrayList<SellingListData> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivitySellinglistAvtivityBinding binding = ActivitySellinglistAvtivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.sellingrv;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("SellingListData");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SellingListData bookInfo = snapshot.getValue(SellingListData.class);
                    arrayList.add(bookInfo);
                }
                adapter.notifyDataSetChanged();  // 어댑터에 데이터 변경 알림
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB 가져오다가 에러..
                Log.e("SellingActivity", String.valueOf(error.toException()));
            }
        });

        adapter = new SellingAdaptor(arrayList, this);
        recyclerView.setAdapter(adapter);
    }
}
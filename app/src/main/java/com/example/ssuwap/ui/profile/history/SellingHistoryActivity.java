// SellingHistoryActivity.java
package com.example.ssuwap.ui.profile.history;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;
import com.example.ssuwap.data.book.BookInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class SellingHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SellingHistoryAdapter adapter;
    private ArrayList<BookInfo> bookList;
    private DatabaseReference bookRef;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_history);

        recyclerView = findViewById(R.id.rv_selling_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookList = new ArrayList<>();
        adapter = new SellingHistoryAdapter(bookList, this);
        recyclerView.setAdapter(adapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            bookRef = FirebaseDatabase.getInstance().getReference("BookInfo");

            // 현재 사용자가 업로드한 책 목록 가져오기
            bookRef.orderByChild("uploaderId").equalTo(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            bookList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                BookInfo bookInfo = dataSnapshot.getValue(BookInfo.class);
                                if (bookInfo != null) {
                                    bookList.add(bookInfo);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SellingHistoryActivity.this, "데이터 로드 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
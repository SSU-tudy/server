package com.example.ssuwap.ui.profile.history;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.ui.book.selling.SellingAdaptor;
import com.example.ssuwap.ui.book.selling.SellingHistoryAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellingHistoryActivity extends AppCompatActivity {

    private RecyclerView sellingRecyclerView;
    private SellingHistoryAdaptor sellingHistoryAdaptor;

    private Button btnSelling;
    private Button btnSoldout;

    private ArrayList<BookInfo> bookList;       // 전체 책 리스트
    private ArrayList<BookInfo> filteredList;  // 필터링된 책 리스트

    private DatabaseReference bookRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_history);

        Log.d("zz", FirebaseAuth.getInstance().getCurrentUser().getUid());
        // Firebase 초기화
        bookRef = FirebaseDatabase.getInstance().getReference("BookInfo");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 현재 사용자 ID 가져오기

        // UI 초기화
        initUI();

        // 데이터 로드
        loadMyBooks();
    }

    private void initUI() {
        // RecyclerView 초기화
        sellingRecyclerView = findViewById(R.id.sellingrv);
        sellingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookList = new ArrayList<>();
        filteredList = new ArrayList<>();
        sellingHistoryAdaptor = new SellingHistoryAdaptor(filteredList, this);
        sellingRecyclerView.setAdapter(sellingHistoryAdaptor);

        // 버튼 초기화
        btnSelling = findViewById(R.id.btn_selling);
        btnSoldout = findViewById(R.id.btn_soldout);

        // "판매 중" 버튼 클릭 리스너
        btnSelling.setOnClickListener(v -> {
            filterBooks(false); // sold == false인 책 필터링
            btnSelling.setBackgroundTintList(getColorStateList(R.color.blue));
            btnSoldout.setBackgroundTintList(getColorStateList(R.color.gray));
        });

        // "판매 완료" 버튼 클릭 리스너
        btnSoldout.setOnClickListener(v -> {
            filterBooks(true); // sold == true인 책 필터링
            // db에 적용
            btnSoldout.setBackgroundTintList(getColorStateList(R.color.blue));
            btnSelling.setBackgroundTintList(getColorStateList(R.color.gray));
        });
    }

    private void loadMyBooks() {
        // 현재 사용자 ID와 일치하는 책만 가져오기
        Query myBooksQuery = bookRef.orderByChild("uploaderId").equalTo(currentUserId);
        myBooksQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookInfo book = dataSnapshot.getValue(BookInfo.class);
                    if (book != null) {
                        bookList.add(book);
                    }
                }
                // 기본적으로 "판매 중"인 책 표시
                filterBooks(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
                // Log.d("SellingHistoryActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private void filterBooks(boolean soldStatus) {
        // soldStatus에 따라 책 필터링
        filteredList.clear();
        for (BookInfo book : bookList) {
            if (book.isSold() == soldStatus) {
                filteredList.add(book);
            }
        }
        sellingHistoryAdaptor.notifyDataSetChanged(); // RecyclerView 갱신
    }
}
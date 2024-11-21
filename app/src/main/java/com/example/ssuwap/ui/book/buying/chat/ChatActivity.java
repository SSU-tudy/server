package com.example.ssuwap.ui.book.buying.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;
import com.example.ssuwap.databinding.ActivityChatBinding;
import com.example.ssuwap.data.chat.ChatData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ChatData> list;
    private String myName = "me";
    private String myMsg = "test";
    private String myBook = "book";

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private EditText etChat;
    private Button btnSend;

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // FirebaseAuth 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid()).child("studentName");
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userId = task.getResult().getValue(String.class);
                    if (userId != null) {
                        myName = userId;
                        setupRecyclerViewAndDatabase();
                    } else {
                        Toast.makeText(ChatActivity.this, "User ID가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "User ID 가져오기 실패.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "로그인 상태가 아닙니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerViewAndDatabase() {
        // RecyclerView 설정
        recyclerView = binding.rvChat;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        adapter = new ChatAdapter(ChatActivity.this, list, myName);
        recyclerView.setAdapter(adapter);

        // Database 설정
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Message").child(myBook);

        // 버튼 리스너 설정
        etChat = binding.etChat;
        btnSend = binding.btnSend;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myMsg = etChat.getText().toString().trim();
                if (!myMsg.isEmpty()) {
                    ChatData chat = new ChatData();
                    chat.setName(myName);
                    chat.setMsg(myMsg);
                    databaseReference.push().setValue(chat);
                    etChat.setText("");
                }
            }
        });

        // 새 채팅 메시지 리스너 설정
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatData chat = snapshot.getValue(ChatData.class);
                if (chat != null) {
                    ((ChatAdapter) adapter).addChat(chat);
                    recyclerView.scrollToPosition(list.size() - 1); // 새 메시지로 스크롤
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
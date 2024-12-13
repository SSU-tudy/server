package com.example.ssuwap.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ssuwap.R;
import com.example.ssuwap.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.ssuwap.data.user.UserAccount;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToSignup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // UI 요소 참조
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToSignup = findViewById(R.id.btnGoToSignup);

        // 로그인 버튼 클릭 리스너
        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // 유효성 검사
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // Firebase 로그인 로직
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // 로그인 성공
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    Toast.makeText(LoginActivity.this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                    onLoginSuccess(user); // 학과 정보 저장 로직 호출
                                }
                            } else {
                                // 로그인 실패
                                Toast.makeText(LoginActivity.this, "로그인에 실패했습니다: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // 회원가입 버튼 클릭 리스너
        btnGoToSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, UsaintLoginActivity.class);
            startActivity(intent);
        });

        // Check if user is already logged in
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            onLoginSuccess(currentUser);
//        }
    }

    private void onLoginSuccess(FirebaseUser user) {
        DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(user.getUid());
        DatabaseReference tagHistoryRef = FirebaseDatabase.getInstance().getReference("TagHistory").child(user.getUid()).child("department");

        // Load user information
        userInfoRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                UserAccount userAccount = snapshot.getValue(UserAccount.class);
                if (userAccount != null) {
                    String department = userAccount.getDepartment();

                    // Save department to TagHistory if not exists
                    tagHistoryRef.orderByValue().equalTo(department).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                tagHistoryRef.push().setValue(department)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(LoginActivity.this, "학과 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                            // 이동
                                            navigateToMainActivity();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(LoginActivity.this, "학과 정보 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            navigateToMainActivity();
                                        });
                            } else {
                                navigateToMainActivity();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginActivity.this, "학과 정보 저장 중 오류 발생: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        }
                    });
                }
            } else {
                Toast.makeText(LoginActivity.this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(LoginActivity.this, "사용자 정보 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish LoginActivity
    }
}
package com.example.ssuwap.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ssuwap.R;
import com.example.ssuwap.ui.login.UsaintAuthService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsaintLoginActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;
    private EditText mEtStudentId, mEtPassword;
    private Button mBtnLoginBtn;
    private UsaintAuthService usaintAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usaint_login);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UserInfo");
        mEtStudentId = findViewById(R.id.etUsaintStudentId);
        mEtPassword = findViewById(R.id.etUsaintPassword);
        mBtnLoginBtn = findViewById(R.id.btnUsaintAuthenticate);
        usaintAuthService = new UsaintAuthService();


        mBtnLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentId = mEtStudentId.getText().toString();
                String password = mEtPassword.getText().toString();

                if (studentId.isEmpty() || password.isEmpty()) {
                    Toast.makeText(UsaintLoginActivity.this, "모든 필드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Firebase에서 이미 등록된 학번인지 확인
                mDatabaseRef.child("UserInfo").child(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            saveStudentIdToPreferences(studentId);
                            // 학번이 이미 존재하는 경우 MainActivity로 이동
                            Toast.makeText(UsaintLoginActivity.this, "이미 등록된 사용자입니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UsaintLoginActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // 새로운 사용자일 경우 인증 및 회원가입 진행
                            usaintAuthService.authenticate(studentId, password, new UsaintAuthService.AuthCallback() {
                                @Override
                                public void onAuthSuccess(String token) {
                                    Intent intent = new Intent(UsaintLoginActivity.this, SignupActivity.class);
                                    intent.putExtra("studentId",studentId);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onAuthFailure(String message) {
                                    Toast.makeText(UsaintLoginActivity.this, "U-SAINT 인증 실패: " + message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UsaintLoginActivity.this, "데이터베이스 오류: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void saveStudentIdToPreferences(String studentId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("realStudentId", studentId);
        editor.apply();
    }
}
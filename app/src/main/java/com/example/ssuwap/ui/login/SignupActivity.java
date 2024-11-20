package com.example.ssuwap.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Firebase imports
import com.example.ssuwap.R;
import com.example.ssuwap.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// ViewBinding import
import com.example.ssuwap.databinding.ActivitySignupBinding;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private String studentId; // Intent로부터 전달받은 studentId

    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewBinding 초기화
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("UserInfo");

        // 스피너 어댑터 설정
        ArrayAdapter<CharSequence> adapterGrade = ArrayAdapter.createFromResource(this,
                R.array.grades_array, android.R.layout.simple_spinner_item);
        adapterGrade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCompleteGrade.setAdapter(adapterGrade);

        ArrayAdapter<CharSequence> adapterSemester = ArrayAdapter.createFromResource(this,
                R.array.semesters_array, android.R.layout.simple_spinner_item);
        adapterSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCompleteSemester.setAdapter(adapterSemester);

        ArrayAdapter<CharSequence> adapterDepartment = ArrayAdapter.createFromResource(this,
                R.array.departments_array, android.R.layout.simple_spinner_item);
        adapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCompleteDepartment.setAdapter(adapterDepartment);

        // Intent로부터 studentId 받기
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("studentId")) {
            studentId = intent.getStringExtra("studentId");
            Log.d(TAG, "Received studentId: " + studentId);
            if (studentId == null || studentId.isEmpty()) {
                Toast.makeText(SignupActivity.this, "유효한 학생 ID가 필요합니다.", Toast.LENGTH_SHORT).show();
                // LoginActivity로 이동
                Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return;
            }
        } else {
            Log.e(TAG, "No studentId found in intent");
            Toast.makeText(SignupActivity.this, "학생 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            // LoginActivity로 이동
            Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // studentId가 없으면 현재 액티비티 종료
            return;
        }

        // 회원가입 완료 버튼 클릭 리스너
        binding.btnCompleteSignup.setOnClickListener(view -> {
            String email = binding.etCompleteEmail.getText().toString().trim();
            String password = binding.etCompletePassword.getText().toString().trim();
            String studentName = binding.etStudentName.getText().toString().trim();
            String grade = binding.spinnerCompleteGrade.getSelectedItem().toString();
            String semester = binding.spinnerCompleteSemester.getSelectedItem().toString();
            String department = binding.spinnerCompleteDepartment.getSelectedItem().toString();

            // 유효성 검사
            if (email.isEmpty() || password.isEmpty() || studentName.isEmpty()) {
                Toast.makeText(SignupActivity.this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // Firebase Authentication을 통한 회원가입
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // 회원가입 성공
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();
                                    Log.d(TAG, "User created with UID: " + uid);
                                    saveUserInfo(uid, email, studentName, grade, semester, department, studentId);
                                }
                            } else {
                                // 회원가입 실패
                                Log.e(TAG, "회원가입 실패: " + task.getException().getMessage());
                                Toast.makeText(SignupActivity.this, "회원가입에 실패했습니다: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void saveUserInfo(String uid, String email, String studentName, String grade, String semester, String department, String studentId) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("studentId", studentId);
        userInfo.put("studentName", studentName);
        userInfo.put("grade", grade);
        userInfo.put("semester", semester);
        userInfo.put("department", department);
        // 비밀번호는 Firebase Authentication에서 안전하게 관리하므로, Realtime Database에 저장하지 않습니다.

        databaseReference.child(uid).setValue(userInfo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User info saved successfully.");
                        Toast.makeText(SignupActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        // LoginActivity로 이동
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // SignupActivity 종료
                    } else {
                        Log.e(TAG, "데이터 저장 실패: " + task.getException().getMessage());
                        Toast.makeText(SignupActivity.this, "데이터 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 메모리 누수를 방지하기 위해 binding을 null로 설정
    }
}
package com.example.ssuwap.ui.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ssuwap.data.user.UserAccount;
import com.example.ssuwap.databinding.BottomSheetEditProfileBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetEditProfileBinding binding;
    private UserAccount currentUser;
    private OnProfileUpdatedListener listener;

    public EditProfileBottomSheet(UserAccount user, OnProfileUpdatedListener listener) {
        this.currentUser = user;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 기존 사용자 정보 세팅
        binding.etDepartment.setText(currentUser.getDepartment());

        // 저장 버튼 클릭 리스너
        binding.btnSave.setOnClickListener(v -> {
            String newDepartment = binding.etDepartment.getText().toString().trim();
            if (!newDepartment.isEmpty()) {
                updateUserProfile(newDepartment);
            } else {
                Toast.makeText(getContext(), "학과를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile(String newDepartment) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userId);
        DatabaseReference tagHistoryRef = FirebaseDatabase.getInstance().getReference("TagHistory").child(userId).child("department");

        // 업데이트된 사용자 정보 저장
        currentUser.setDepartment(newDepartment);
        userRef.setValue(currentUser).addOnSuccessListener(aVoid -> {
            // Firebase에서 중복 확인
            tagHistoryRef.get().addOnSuccessListener(snapshot -> {
                boolean isDuplicate = false;

                // 중복 확인
                for (DataSnapshot tagSnapshot : snapshot.getChildren()) {
                    String existingTag = tagSnapshot.getValue(String.class);
                    if (existingTag != null && existingTag.equals(newDepartment)) {
                        isDuplicate = true;
                        break;
                    }
                }

                // 중복되지 않는 경우에만 추가
                if (!isDuplicate) {
                    String key = tagHistoryRef.push().getKey(); // 고유 키 생성
                    if (key != null) {
                        tagHistoryRef.child(key).setValue(newDepartment)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(getContext(), "학과 수정 및 기록 완료.", Toast.LENGTH_SHORT).show();
                                    if (listener != null) listener.onProfileUpdated(currentUser);
                                    dismiss(); // 바텀시트 닫기
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "태그 기록 실패.", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(getContext(), "태그가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onProfileUpdated(currentUser);
                    dismiss(); // 바텀시트 닫기
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "중복 확인 실패.", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "프로필 업데이트 실패.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) listener.onDismissed();
    }

    public interface OnProfileUpdatedListener {
        void onProfileUpdated(UserAccount updatedUser); // 프로필 업데이트 콜백
        void onDismissed(); // 바텀시트 닫힘 콜백
    }
}
package com.example.ssuwap.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ssuwap.R;
import com.example.ssuwap.data.user.UserAccount;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditProfileBottomSheet extends BottomSheetDialogFragment {

    public interface EditProfileListener {
        void onProfileUpdated(UserAccount updatedUser);
    }

    private EditProfileListener listener;
    private UserAccount currentUser;

    public EditProfileBottomSheet(UserAccount currentUser, EditProfileListener listener) {
        this.currentUser = currentUser;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_profile, container, false);

        EditText etName = view.findViewById(R.id.et_name);
        EditText etGrade = view.findViewById(R.id.et_grade);
        EditText etSemester = view.findViewById(R.id.et_semester);
        EditText etDepartment = view.findViewById(R.id.et_department);
        Button btnSave = view.findViewById(R.id.btn_save);

        // 기존 데이터 로드
        if (currentUser != null) {
            etName.setText(currentUser.getStudentName());
            etGrade.setText(currentUser.getGrade());
            etSemester.setText(currentUser.getSemester());
            etDepartment.setText(currentUser.getDepartment());
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String grade = etGrade.getText().toString().trim();
            String semester = etSemester.getText().toString().trim();
            String department = etDepartment.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(grade) || TextUtils.isEmpty(semester) || TextUtils.isEmpty(department)) {
                Toast.makeText(getContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 기존 객체를 복사하고 필요한 필드만 업데이트
            UserAccount updatedUser = new UserAccount();
            updatedUser.setEmailId(currentUser.getEmailId());
            updatedUser.setIdToken(currentUser.getIdToken());
            updatedUser.setUserName(currentUser.getUserName());
            updatedUser.setImageUrl(currentUser.getImageUrl());
            updatedUser.setSubjects(currentUser.getSubjects());
            // 수정된 필드만 업데이트
            updatedUser.setStudentName(name);
            updatedUser.setGrade(grade);
            updatedUser.setSemester(semester);
            updatedUser.setDepartment(department);

            if (listener != null) {
                listener.onProfileUpdated(updatedUser);
            }
            dismiss();
        });

        return view;
    }
}
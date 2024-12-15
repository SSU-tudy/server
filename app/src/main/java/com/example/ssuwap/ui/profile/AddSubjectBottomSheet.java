package com.example.ssuwap.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ssuwap.R;
import com.example.ssuwap.data.user.Subject;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddSubjectBottomSheet extends BottomSheetDialogFragment {

    private OnSubjectAddedListener listener;

    public void setSubjectAddedListener(OnSubjectAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_subject, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etSubjectName = view.findViewById(R.id.et_subject_name);
        EditText etSubjectGrade = view.findViewById(R.id.et_subject_grade);
        EditText etSubjectSemester = view.findViewById(R.id.et_subject_semester);
        EditText etSubjectDepartment = view.findViewById(R.id.et_subject_department);
        EditText etDay = view.findViewById(R.id.et_subject_day);
        EditText etStartTime = view.findViewById(R.id.et_subject_start_time);
        EditText etEndTime = view.findViewById(R.id.et_subject_end_time);

        view.findViewById(R.id.btn_save_subject).setOnClickListener(v -> {
            String subjectName = etSubjectName.getText().toString().trim();
            String grade = etSubjectGrade.getText().toString().trim();
            String semester = etSubjectSemester.getText().toString().trim();
            String department = etSubjectDepartment.getText().toString().trim();
            String day = etDay.getText().toString().trim();
            String startTime = etStartTime.getText().toString().trim();
            String endTime = etEndTime.getText().toString().trim();

            if (TextUtils.isEmpty(subjectName) || TextUtils.isEmpty(grade) || TextUtils.isEmpty(semester) ||
                    TextUtils.isEmpty(department) || TextUtils.isEmpty(day) ||
                    TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
                Toast.makeText(getContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Subject subject = new Subject(subjectName, grade, semester, department, day, startTime, endTime);

            saveSubjectToDatabase(subject);
        });
    }

    private void saveSubjectToDatabase(Subject subject) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String gradeSemesterKey = subject.getGrade() + "_" + subject.getSemester(); // 학년_학기 Key 생성
        DatabaseReference subjectsRef = FirebaseDatabase.getInstance()
                .getReference("timetableInfo")
                .child(userId)
                .child(gradeSemesterKey);

        DatabaseReference tagSubjectsRef = FirebaseDatabase.getInstance()
                .getReference("TagHistory")
                .child(userId)
                .child("subjects");

        // 중복 확인
        tagSubjectsRef.get().addOnSuccessListener(snapshot -> {
            boolean isDuplicate = false;

            for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                String existingSubject = subjectSnapshot.getValue(String.class);
                if (existingSubject != null && existingSubject.equals(subject.getSubjectName())) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                // 중복이 없으면 데이터 추가
                String subjectKey = subjectsRef.push().getKey();
                if (subjectKey != null) {
                    subjectsRef.child(subjectKey).setValue(subject)
                            .addOnSuccessListener(unused -> {
                                // TagHistory에도 추가
                                String tagKey = tagSubjectsRef.push().getKey();
                                if (tagKey != null) {
                                    tagSubjectsRef.child(tagKey).setValue(subject.getSubjectName())
                                            .addOnSuccessListener(unused1 -> {
                                                Toast.makeText(getContext(), "과목이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                                if (listener != null) listener.onSubjectAdded(subject);
                                                dismiss();
                                            });
                                }
                            });
                }
            } else {
                Toast.makeText(getContext(), "이미 존재하는 과목입니다.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "중복 확인 실패.", Toast.LENGTH_SHORT).show();
        });
    }

    public interface OnSubjectAddedListener {
        void onSubjectAdded(Subject subject);
    }
}
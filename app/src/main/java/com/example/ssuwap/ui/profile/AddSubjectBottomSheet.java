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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddSubjectBottomSheet extends BottomSheetDialogFragment {

    public interface SubjectAddedListener {
        void onSubjectAdded(Subject subject);
    }

    private SubjectAddedListener listener;

    public void setSubjectAddedListener(SubjectAddedListener listener) {
        this.listener = listener;
    }

    public AddSubjectBottomSheet() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_subject, container, false);

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

            if (listener != null) {
                listener.onSubjectAdded(subject);
            }
        });

        return view;
    }

    private void saveSubjectToDatabase(Subject subject) {
        String gradeSemesterKey = subject.getGrade() + "_" + subject.getSemester(); // 학년_학기 Key 생성
        DatabaseReference userSubjectsRef = FirebaseDatabase.getInstance()
                .getReference("timetableInfo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(gradeSemesterKey); // 학년_학기 Key 하위에 저장

        String key = userSubjectsRef.push().getKey(); // Unique key for the subject
        if (key != null) {
            userSubjectsRef.child(key).setValue(subject)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "과목이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "과목 추가 실패.", Toast.LENGTH_SHORT).show());
        }
    }
}
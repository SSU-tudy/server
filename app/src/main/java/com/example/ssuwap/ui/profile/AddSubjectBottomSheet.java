package com.example.ssuwap.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.concurrent.atomic.AtomicBoolean;

public class AddSubjectBottomSheet extends BottomSheetDialogFragment {

    private OnSubjectAddedListener listener;

    private String selectedSubjectName = "";
    private String selectedGrade = "";
    private String selectedSemester = "";
    private String selectedDepartment = "";
    private String selectedDay = "";
    private String selectedStartTime = "";
    private String selectedEndTime = "";

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

        // 각 버튼 클릭 시 Dialog 실행
        view.findViewById(R.id.btn_select_subject_name).setOnClickListener(v -> showDialog("과목명", new String[]{"알고리즘", "자료구조", "데이터베이스","컴퓨터네트워크"}, result -> selectedSubjectName = result));
        view.findViewById(R.id.btn_select_grade).setOnClickListener(v -> showDialog("학년", new String[]{"1학년", "2학년", "3학년", "4학년"}, result -> selectedGrade = result));
        view.findViewById(R.id.btn_select_semester).setOnClickListener(v -> showDialog("학기", new String[]{"1학기", "2학기"}, result -> selectedSemester = result));
        view.findViewById(R.id.btn_select_department).setOnClickListener(v -> showDialog("학과", new String[]{"컴퓨터공학", "소프트웨어학", "정보통신공학"}, result -> selectedDepartment = result));
        view.findViewById(R.id.btn_select_day).setOnClickListener(v -> showDialog("요일", new String[]{"월요일", "화요일", "수요일", "목요일", "금요일"}, result -> selectedDay = result));
        view.findViewById(R.id.btn_select_start_time).setOnClickListener(v -> showDialog("시작 시간", new String[]{"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"}, result -> selectedStartTime = result));
        view.findViewById(R.id.btn_select_end_time).setOnClickListener(v -> showDialog("종료 시간", new String[]{"10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00"}, result -> selectedEndTime = result));

        // 저장 버튼 클릭 시 처리
        view.findViewById(R.id.btn_save_subject).setOnClickListener(v -> saveSubject());
    }

    private void showDialog(String title, String[] options, OnDialogResultListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);
        builder.setItems(options, (dialog, which) -> listener.onResult(options[which]));
        builder.create().show();
    }

    private void saveSubject() {
        if (TextUtils.isEmpty(selectedSubjectName) || TextUtils.isEmpty(selectedGrade) || TextUtils.isEmpty(selectedSemester) ||
                TextUtils.isEmpty(selectedDepartment) || TextUtils.isEmpty(selectedDay) ||
                TextUtils.isEmpty(selectedStartTime) || TextUtils.isEmpty(selectedEndTime)) {
            Toast.makeText(requireContext(), "모든 항목을 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Subject subject = new Subject(selectedSubjectName, selectedGrade, selectedSemester, selectedDepartment, selectedDay, selectedStartTime, selectedEndTime);
        saveSubjectToDatabase(subject);
    }

    private void saveSubjectToDatabase(Subject subject) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String gradeSemesterKey = subject.getGrade().substring(0,1) + "_" + subject.getSemester().substring(0,1); // 학년_학기 Key 생성
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
                if (existingSubject != null &&
                        existingSubject.equals(subject.getSubjectName()) &&
                        subject.getStartTime().equals(subject.getStartTime()) &&
                        subject.getEndTime().equals(subject.getEndTime())) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                // 중복이 없으면 TagHistory에 추가
                String tagKey = tagSubjectsRef.push().getKey();
                if (tagKey != null) {
                    tagSubjectsRef.child(tagKey).setValue(subject.getSubjectName())
                            .addOnSuccessListener(unused -> {
                                if (isAdded()) {
                                    Toast.makeText(requireContext(), "태그가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (isAdded()) {
                                    Toast.makeText(requireContext(), "태그 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                if (isAdded()) {
                    //Toast.makeText(requireContext(), "이미 존재하는 태그입니다.", Toast.LENGTH_SHORT).show();
                }
            }

            // 시간표 정보 저장 (항상 실행)
            String subjectKey = subjectsRef.push().getKey();
            if (subjectKey != null) {
                subjectsRef.child(subjectKey).setValue(subject)
                        .addOnSuccessListener(unused -> {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "시간표에 과목이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                if (listener != null) listener.onSubjectAdded(subject);
                                dismiss(); // BottomSheet 닫기
                            }
                        })
                        .addOnFailureListener(e -> {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "시간표 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(e -> {
            if (isAdded()) {
                Toast.makeText(requireContext(), "중복 확인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnSubjectAddedListener {
        void onSubjectAdded(Subject subject);
    }
    private interface OnDialogResultListener {
        void onResult(String result);
    }
}
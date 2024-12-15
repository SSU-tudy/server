package com.example.ssuwap.ui.book.upload.isbn;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.ssuwap.R;
import com.example.ssuwap.ui.post.uploadpost.TagDialogFragment;

public class TagSelectFragment extends DialogFragment {

    private static final String TAG = "TagDialogFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public interface TagDialogListener {
        void onTagSelected(String grade, String semester, String subject);
    }

    private TagDialogListener listener;

    public static TagSelectFragment newInstance(String param1, String param2) {
        TagSelectFragment fragment = new TagSelectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TagDialogListener) {
            listener = (TagDialogListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement TagDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog");
        Context context = requireContext();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        View dialogView = inflater.inflate(R.layout.tag_selected_dialog, null);

        Spinner gradeSpinner = dialogView.findViewById(R.id.spinner_grade);
        Spinner semesterSpinner = dialogView.findViewById(R.id.spinner_semester);
        Spinner subjectSpinner = dialogView.findViewById(R.id.spinner_subject);

        // 학년 데이터 설정
        String[] grades = context.getResources().getStringArray(R.array.grades);
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, grades);
        gradeSpinner.setAdapter(gradeAdapter);

        // 학기 데이터 설정
        String[] semesters = context.getResources().getStringArray(R.array.term);
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, semesters);
        semesterSpinner.setAdapter(semesterAdapter);

        // 학년과 학기 선택 시 과목 업데이트
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSubjects(context, gradeSpinner, semesterSpinner, subjectSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택 안됨
            }
        };

        gradeSpinner.setOnItemSelectedListener(spinnerListener);
        semesterSpinner.setOnItemSelectedListener(spinnerListener);

        // AlertDialog 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("학년 및 과목 선택");
        builder.setView(dialogView);
        builder.setPositiveButton("확인", (dialog, which) -> {
            String selectedGrade = gradeSpinner.getSelectedItem().toString();
            String selectedSemester = semesterSpinner.getSelectedItem().toString();
            String selectedSubject = subjectSpinner.getSelectedItem().toString();
            if (listener != null) {
                listener.onTagSelected(selectedGrade, selectedSemester, selectedSubject);
            }
        });
        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void updateSubjects(Context context, Spinner gradeSpinner, Spinner semesterSpinner, Spinner subjectSpinner) {
        int gradePosition = gradeSpinner.getSelectedItemPosition();
        int semesterPosition = semesterSpinner.getSelectedItemPosition();

        int[][] subjectArrayIds = {
                {R.array.subject_1_1, R.array.subject_1_2},
                {R.array.subject_2_1, R.array.subject_2_2},
                {R.array.subject_3_1, R.array.subject_3_2},
                {R.array.subject_4_1, R.array.subject_4_2}
        };

        int selectedArrayId = subjectArrayIds[gradePosition][semesterPosition];
        String[] subjects = context.getResources().getStringArray(selectedArrayId);

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, subjects);
        subjectSpinner.setAdapter(subjectAdapter);
    }
}

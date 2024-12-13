package com.example.ssuwap.ui.post.uploadpost;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.ssuwap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TagDialogFragment extends DialogFragment {

    private static final String TAG = "TagDialogFragment";

    public interface TagDialogListener {
        void onTagSelected(String grade, String subject);
    }

    private TagDialogListener listener;
    private String userSemester;

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

    public static TagDialogFragment newInstance(String semester) {
        TagDialogFragment fragment = new TagDialogFragment();
        Bundle args = new Bundle();
        args.putString("semester", semester);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userSemester = getArguments().getString("semester");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog");
        Context context = requireContext();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.tag_dialog, null);

        Spinner gradeSpinner = dialogView.findViewById(R.id.spinner_grade);
        Spinner subjectSpinner = dialogView.findViewById(R.id.spinner_subject);

        // 학년 데이터 설정 (strings.xml에서 불러오기)
        String[] grades = context.getResources().getStringArray(R.array.grades);
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, grades);
        gradeSpinner.setAdapter(gradeAdapter);


        // 학년 선택 시 과목 업데이트
        gradeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                int[][] subjectArrayIds = {
                        {R.array.subject_1_1, R.array.subject_1_2}, // 1학년
                        {R.array.subject_2_1, R.array.subject_2_2}, // 2학년
                        {R.array.subject_3_1, R.array.subject_3_2}, // 3학년
                        {R.array.subject_4_1, R.array.subject_4_2}  // 4학년
                };

                // 사용자 학기에 해당하는 과목만 필터링

                int semester = 0;
                if(userSemester.equals("1학기")) semester = 1;
                else if(userSemester.equals("2학기")) semester = 2;

                int selectedArrayId = subjectArrayIds[position][semester - 1];
                String[] subjects = context.getResources().getStringArray(selectedArrayId);

                ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item, subjects);
                subjectSpinner.setAdapter(subjectAdapter);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // 아무것도 선택되지 않았을 때 처리 (필요 시)
            }
        });

        // AlertDialog 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("학년 및 과목 선택");
        builder.setView(dialogView);
        builder.setPositiveButton("확인", (dialog, which) -> {
            String selectedGrade = gradeSpinner.getSelectedItem().toString();
            String selectedSubject = subjectSpinner.getSelectedItem().toString();
            if (listener != null) {
                listener.onTagSelected(selectedGrade, selectedSubject);
            }
        });
        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}

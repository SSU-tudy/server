package com.example.ssuwap.ui.post.uploadpost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.ssuwap.R;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.data.book.TagItem;
import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.databinding.ActivityPostMainBinding;
import com.example.ssuwap.databinding.FragmentPostMainBinding;
import com.example.ssuwap.ui.book.selling.TagAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostMainFragment extends Fragment{

    private static final String TAG = "PostMainFragment";

    ArrayList<PostInfo> list;
    private ArrayList<String> tagList;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adaptor;
    private RecyclerView.LayoutManager layoutManager;
    FragmentPostMainBinding binding;
    private String userSemester;

    private String selectedGrade = "";
    private String selectedSemester = "";
    private String selectedDepartment = "";

    // 태그 RecyclerView 관련 변수
    private RecyclerView tagRecyclerView;
    private TagAdapter tagAdapter;
    private ArrayList<TagItem> selectedTags;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostMainFragment newInstance(String param1, String param2) {
        Log.d(TAG,"newInstance()");
        PostMainFragment fragment = new PostMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate()");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView()");
        binding = FragmentPostMainBinding.inflate(inflater, container, false);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated()");

        getUserInfoForFirebase();

        recyclerView = binding.postRV;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        binding.selectedTag1.setVisibility(View.GONE);
        binding.selectedTag2.setVisibility(View.GONE);
        binding.selectedTag3.setVisibility(View.GONE);

        adaptor = new PostAdaptor(requireContext(), list, requireActivity().getSupportFragmentManager());
        recyclerView.setAdapter(adaptor);

        binding.addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Log.d(TAG, TAG+" onClick()");
                    Log.d(TAG, TAG+" onClick()" + " userSemester : "+userSemester);
                    showTagSelectionBottomSheet();
            }
        });
    }

    private void getUserInfoForFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid()).child("semester");
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userSem = task.getResult().getValue(String.class);
                    if (userSem != null) {
                        userSemester = userSem;
                        Log.d(TAG, userSemester);
                    } else {
                        Log.d(TAG, "User Semester가 존재하지 않습니다.");
                    }
                } else {
                    Log.d(TAG, "User Semester 가져오기 실패.");
                }
            });
        }
        loadFirebaseData();
    }

    private void loadFirebaseData(){
        Log.d(TAG,"loadFirebaseData()");

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("PostInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear(); // 기존 데이터 초기화

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        // PostInfo 객체 생성
                        PostInfo postInfo = snapshot.getValue(PostInfo.class);

                        if (postInfo != null) {
                            // comments 필드를 ArrayList로 변환
                            ArrayList<CommentInfo> commentsList = new ArrayList<>();
                            Map<String, CommentInfo> commentsMap = postInfo.getComments();
                            if (commentsMap != null) {
                                commentsList.addAll(commentsMap.values());
                            }

                            // 변환된 commentsList를 postInfo 객체에 설정
                            postInfo.setCommentsList(commentsList);

                            // 게시물 정보를 리스트에 추가
                            Log.d(TAG, "dataLoad : " + postInfo.getDescription());
                            list.add(postInfo);
                        } else {
                            Log.e(TAG, "Null PostInfo detected");
                        }

                    } catch (DatabaseException e) {
                        Log.e(TAG, "Error loading data: " + e.getMessage(), e);
                    }
                }
                // 데이터 로드 후 어댑터 갱신
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.toException());
            }
        });
    }

    private void showTagSelectionBottomSheet() {
        // Bottom Sheet Dialog 생성
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_tags, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Context context = requireContext();

        // Spinner 및 버튼 참조
        Spinner gradeSpinner = bottomSheetView.findViewById(R.id.spinner_grade);
        Spinner semesterSpinner = bottomSheetView.findViewById(R.id.spinner_semester);
        Spinner subjectSpinner = bottomSheetView.findViewById(R.id.spinner_department);
        Button btnApplyTags = bottomSheetView.findViewById(R.id.btn_apply_tags);

        if (gradeSpinner == null || semesterSpinner == null || subjectSpinner == null) {
            Log.e(TAG, "Spinner is null, check your layout ids");
            return;
        }

        // 학년 데이터 설정
        String[] grades = context.getResources().getStringArray(R.array.grades);
        String[] gradesAll = new String[grades.length + 1];
        gradesAll[0] = "전체";
        System.arraycopy(grades, 0, gradesAll, 1, grades.length);
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, gradesAll);
        gradeSpinner.setAdapter(gradeAdapter);

        // 학기 데이터 설정
        String[] semesters = context.getResources().getStringArray(R.array.term);
        String[] semestersAll = new String[semesters.length+1];
        semestersAll[0] = "전체";
        System.arraycopy(semesters, 0, semestersAll, 1, semesters.length);
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, semestersAll);
        semesterSpinner.setAdapter(semesterAdapter);

        // 학년 및 학기 선택에 따라 과목 리스트 업데이트
        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int gradePosition, long id) {
                updateSubjectSpinner(subjectSpinner, gradePosition, semesterSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int semesterPosition, long id) {
                updateSubjectSpinner(subjectSpinner, gradeSpinner.getSelectedItemPosition(), semesterPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // 확인 버튼 클릭 이벤트 처리
        btnApplyTags.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            selectedGrade = gradeSpinner.getSelectedItem().toString();
            selectedSemester = semesterSpinner.getSelectedItem().toString();
            selectedDepartment = subjectSpinner.getSelectedItem().toString();

            binding.selectedTag1.setText(selectedGrade);
            binding.selectedTag2.setText(selectedSemester);
            binding.selectedTag3.setText(selectedDepartment);

            if(binding.selectedTag1.getText().equals("전체"))
                binding.selectedTag1.setVisibility(View.GONE);
            else
                binding.selectedTag1.setVisibility(View.VISIBLE);

            if(binding.selectedTag2.getText().equals("전체"))
                binding.selectedTag2.setVisibility(View.GONE);
            else
                binding.selectedTag2.setVisibility(View.VISIBLE);

            if(binding.selectedTag3.getText().equals("전체"))
                binding.selectedTag3.setVisibility(View.GONE);
            else
                binding.selectedTag3.setVisibility(View.VISIBLE);
            applySelectedTags();
        });

        bottomSheetDialog.show();
    }

    private void applySelectedTags() {

        Log.d(TAG, "applySelectedTags() "+selectedGrade);
        Log.d(TAG, "applySelectedTags() "+selectedSemester);
        Log.d(TAG, "applySelectedTags() "+selectedDepartment);

        loadPostsByTags();
    }

    private void updateSubjectSpinner(Spinner subjectSpinner, int gradePosition, int semesterPosition) {
        int[][] subjectArrayIds = {
                {R.array.subject_1_1, R.array.subject_1_2}, // 1학년
                {R.array.subject_2_1, R.array.subject_2_2}, // 2학년
                {R.array.subject_3_1, R.array.subject_3_2}, // 3학년
                {R.array.subject_4_1, R.array.subject_4_2}  // 4학년
        };

        // 학년과 학기가 모두 "전체"인 경우 모든 과목 보기
        if (gradePosition == 0 && semesterPosition == 0) {
            String[] allSubjects = {"전체"};
            ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, allSubjects);
            subjectSpinner.setAdapter(subjectAdapter);
            return;
        }

        // 학년만 선택된 경우 해당 학년의 모든 과목 보기
        if (gradePosition > 0 && semesterPosition == 0) {
            List<String> gradeSubjects = new ArrayList<>();
            gradeSubjects.add("전체");
            for (int semesterIdx = 0; semesterIdx < subjectArrayIds[gradePosition - 1].length; semesterIdx++) {
                String[] subjects = getResources().getStringArray(subjectArrayIds[gradePosition - 1][semesterIdx]);
                gradeSubjects.addAll(Arrays.asList(subjects));
            }
            ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, gradeSubjects);
            subjectSpinner.setAdapter(subjectAdapter);
            return;
        }

        // 학년과 학기 모두 선택된 경우 해당 학년, 학기의 과목 보기
        if (gradePosition > 0 && semesterPosition > 0) {
            int selectedArrayId = subjectArrayIds[gradePosition - 1][semesterPosition - 1]; // 인덱스 조정
            String[] subjects = getResources().getStringArray(selectedArrayId);

            String[] subjectsWithAllOption = new String[subjects.length + 1];
            subjectsWithAllOption[0] = "전체";
            System.arraycopy(subjects, 0, subjectsWithAllOption, 1, subjects.length);

            ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, subjectsWithAllOption);
            subjectSpinner.setAdapter(subjectAdapter);
            return;
        }
    }

    private void loadPostsByTags() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("PostInfo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PostInfo postInfo = snapshot.getValue(PostInfo.class);
                    if (postInfo != null) {
                        // 태그 필터링 로직
                        boolean matches = true;
                        if (!selectedGrade.equals("전체")) {
                            if (!postInfo.getPostTag1().equals(selectedGrade)) {
                                matches = false;
                            }
                        }
                        if (!selectedSemester.equals("전체")) {
                            if (!postInfo.getPostTag2().equals(selectedSemester)) {
                                matches = false;
                            }
                        }
                        if (!selectedDepartment.equals("전체")) {
                            if (!postInfo.getPostTag3().equals(selectedDepartment)) {
                                matches = false;
                            }
                        }
                        if (matches) {
                            Log.d(TAG, TAG+postInfo.getDescription());
                            list.add(postInfo);
                        }
                    }
                }
                Log.d("SellingFragment", "Data filtered by tags, arrayList size: " + list.size());
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SellingFragment", "Firebase error: " + error.toException());
            }
        });
    }
}
// SellingFragment.java
package com.example.ssuwap.ui.book.selling;

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
import com.example.ssuwap.databinding.FragmentSellingBinding;
import com.example.ssuwap.ui.book.upload.UploadBookFormat;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellingFragment extends Fragment {
    private RecyclerView recyclerView;
    private SellingAdaptor adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BookInfo> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FragmentSellingBinding binding;

    // 태그 RecyclerView 관련 변수
    private RecyclerView tagRecyclerView;
    private TagAdapter tagAdapter;
    private ArrayList<TagItem> selectedTags;

    // 태그 선택을 위한 변수 선언
    private String selectedGrade = "";
    private String selectedSemester = "";
    private String selectedDepartment = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SellingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellingFragment newInstance(String param1, String param2) {
        SellingFragment fragment = new SellingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Firebase 초기화
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("BookInfo");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSellingBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.sellingrv;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>();

        // 어댑터 초기화 및 연결
        adapter = new SellingAdaptor(arrayList, requireContext());
        recyclerView.setAdapter(adapter);
        Log.d("SellingFragment", "Adapter attached to RecyclerView");

        // 태그 RecyclerView 설정
        tagRecyclerView = binding.tagrv;
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        selectedTags = new ArrayList<>();
        tagAdapter = new TagAdapter(selectedTags);
        tagRecyclerView.setAdapter(tagAdapter);

        // 초기 데이터 로드
        loadAllBooks();

        // 태그 추가 버튼 클릭 리스너 설정
        binding.btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTagSelectionBottomSheet();
            }
        });

        // 업로드로 이동
        binding.sellingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), UploadBookFormat.class));
            }
        });
    }

    private void loadAllBooks() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BookInfo bookInfo = snapshot.getValue(BookInfo.class);
                    if (bookInfo != null) {
                        arrayList.add(bookInfo);
                    }
                }
                Log.d("SellingFragment", "All data loaded, arrayList size: " + arrayList.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SellingFragment", "Firebase error: " + error.toException());
            }
        });
    }

    private void showTagSelectionBottomSheet() {
        // Bottom Sheet Dialog 생성
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_tags, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Spinner 및 버튼 참조
        Spinner spinnerGrade = bottomSheetView.findViewById(R.id.spinner_grade);
        Spinner spinnerSemester = bottomSheetView.findViewById(R.id.spinner_semester);
        Spinner spinnerDepartment = bottomSheetView.findViewById(R.id.spinner_department);
        Button btnApplyTags = bottomSheetView.findViewById(R.id.btn_apply_tags);

        // Spinner에 데이터 설정
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getGrades());
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrade.setAdapter(gradeAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getSemesters());
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getDepartments());
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(departmentAdapter);

        // Spinner 선택 이벤트 처리
        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedGrade = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedGrade = "";
            }
        });

        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedSemester = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedSemester = "";
            }
        });

        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedDepartment = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedDepartment = "";
            }
        });

        // 확인 버튼 클릭 이벤트 처리
        btnApplyTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                applySelectedTags();
            }
        });

        bottomSheetDialog.show();
    }

    // 학년 리스트 반환
    private List<String> getGrades() {
        return Arrays.asList("전체", "1학년", "2학년", "3학년", "4학년");
    }

    // 학기 리스트 반환
    private List<String> getSemesters() {
        return Arrays.asList("전체", "1학기", "2학기");
    }

    // 학과 리스트 반환
    private List<String> getDepartments() {
        return Arrays.asList("전체", "컴퓨터공학과", "소프트웨어학과", "정보통신공학과", "전자공학과");
    }

    private void applySelectedTags() {
        // 선택한 태그를 기반으로 데이터 필터링 및 UI 업데이트
        // 가로 방향 RecyclerView에 태그 표시
        updateTagRecyclerView();
        // 세로 방향 RecyclerView에 해당 태그를 가진 책만 표시
        loadBooksByTags();
    }

    private void updateTagRecyclerView() {
        selectedTags.clear();
        if (!selectedGrade.equals("") && !selectedGrade.equals("전체")) {
            selectedTags.add(new TagItem(selectedGrade));
        }
        if (!selectedSemester.equals("") && !selectedSemester.equals("전체")) {
            selectedTags.add(new TagItem(selectedSemester));
        }
        if (!selectedDepartment.equals("") && !selectedDepartment.equals("전체")) {
            selectedTags.add(new TagItem(selectedDepartment));
        }
        tagAdapter.notifyDataSetChanged();
    }

    private void loadBooksByTags() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BookInfo bookInfo = snapshot.getValue(BookInfo.class);
                    if (bookInfo != null) {
                        // 태그 필터링 로직
                        boolean matches = true;
                        if (!selectedGrade.equals("") && !selectedGrade.equals("전체")) {
                            if (!bookInfo.getTag_grade().equals(selectedGrade)) {
                                matches = false;
                            }
                        }
                        if (!selectedSemester.equals("") && !selectedSemester.equals("전체")) {
                            if (!bookInfo.getTag_semester().equals(selectedSemester)) {
                                matches = false;
                            }
                        }
                        if (!selectedDepartment.equals("") && !selectedDepartment.equals("전체")) {
                            if (!bookInfo.getTag_subject().equals(selectedDepartment)) {
                                matches = false;
                            }
                        }
                        if (matches) {
                            arrayList.add(bookInfo);
                        }
                    }
                }
                Log.d("SellingFragment", "Data filtered by tags, arrayList size: " + arrayList.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SellingFragment", "Firebase error: " + error.toException());
            }
        });
    }
}
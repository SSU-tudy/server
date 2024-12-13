package com.example.ssuwap.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.data.user.Subject;
import com.example.ssuwap.data.user.UserAccount;
import com.example.ssuwap.databinding.FragmentProfileMainBinding;
import com.example.ssuwap.ui.profile.calander.CalendarActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileMainFragment extends Fragment {

    private FragmentProfileMainBinding binding;
    private DatabaseReference userRef;
    private DatabaseReference tagHistoryRef;
    private TagHistoryAdapter tagAdapter;

    private List<Subject> subjectList = new ArrayList<>();
    private UserAccount currentUser;

    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";

    private String param1;
    private String param2;

    public static ProfileMainFragment newInstance(String param1, String param2) {
        ProfileMainFragment fragment = new ProfileMainFragment();
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
            param1 = getArguments().getString(ARG_PARAM1);
            param2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public ProfileMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase references
        userRef = FirebaseDatabase.getInstance()
                .getReference("UserInfo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        tagHistoryRef = FirebaseDatabase.getInstance()
                .getReference("TagHistory")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Initialize UI components
        setupUserProfile();
        setupTagsRecyclerView();
        setupEditProfileButton();
        setupActivityButtons();
        loadTagHistory();
    }

    private void setupUserProfile() {
        userRef.get().addOnSuccessListener(snapshot -> {
            currentUser = snapshot.getValue(UserAccount.class);
            if (currentUser != null) {
                binding.tvUserName.setText(currentUser.getStudentName());
                binding.tvUserDetails.setText(String.format("%s | %s %s",
                        currentUser.getDepartment(), currentUser.getGrade(), currentUser.getSemester()));
                if (currentUser.getImageUrl() != null) {
                    Glide.with(requireContext()).load(currentUser.getImageUrl()).into(binding.ivProfilePicture);
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "사용자 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show());
    }

    private void setupEditProfileButton() {
        binding.btnEditProfile.setOnClickListener(v -> {
            if (currentUser == null) return;

            EditProfileBottomSheet bottomSheet = new EditProfileBottomSheet(currentUser, updatedUser -> {
                // Firebase 업데이트
                userRef.setValue(updatedUser).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "프로필이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    setupUserProfile();
                    saveDepartmentHistory(updatedUser.getDepartment());
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "프로필 업데이트 실패.", Toast.LENGTH_SHORT).show();
                });
            });

            bottomSheet.show(getParentFragmentManager(), "EditProfileBottomSheet");
        });
    }

    private void saveDepartmentHistory(String department) {
        DatabaseReference departmentRef = tagHistoryRef.child("department");

        // Check for duplicates before adding
        departmentRef.orderByValue().equalTo(department).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    departmentRef.push().setValue(department)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "학과 태그가 저장되었습니다.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "학과 태그 저장 실패.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "학과 태그 저장 중 오류 발생.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSubjectHistory(String subject) {
        DatabaseReference subjectRef = tagHistoryRef.child("subject");

        // Check for duplicates before adding
        subjectRef.orderByValue().equalTo(subject).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    subjectRef.push().setValue(subject)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "과목 태그가 저장되었습니다.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "과목 태그 저장 실패.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "과목 태그 저장 중 오류 발생.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTagsRecyclerView() {
        tagAdapter = new TagHistoryAdapter(new ArrayList<>());
        binding.rvTags.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTags.setAdapter(tagAdapter);
    }

    private void loadTagHistory() {
        tagHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> tags = new ArrayList<>();
                // Load department tags
                if (snapshot.child("department").exists()) {
                    for (DataSnapshot deptSnapshot : snapshot.child("department").getChildren()) {
                        String tag = deptSnapshot.getValue(String.class);
                        if (tag != null) tags.add(tag);
                    }
                }
                // Load subject tags
                if (snapshot.child("subject").exists()) {
                    for (DataSnapshot subjectSnapshot : snapshot.child("subject").getChildren()) {
                        String tag = subjectSnapshot.getValue(String.class);
                        if (tag != null) tags.add(tag);
                    }
                }
                if (!tags.isEmpty()) {
                    tagAdapter.setTagList(tags);
                    binding.rvTags.setVisibility(View.VISIBLE);
                } else {
                    binding.rvTags.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "태그 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupActivityButtons() {
        binding.btnSalesHistory.setOnClickListener(v -> {
            // 판매 내역 이동 로직
        });
        binding.btnMyPosts.setOnClickListener(v -> {
            // 내가 쓴 글 이동 로직
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ProfilePostFragment.newInstance("param1", "param2"))
                    .addToBackStack(null)
                    .commit();
            // Navigate to my posts
        });
        binding.btnStudyRecords.setOnClickListener(v -> {
            // 공부 기록 이동 로직
            startActivity(new Intent(requireActivity(), CalendarActivity.class));
            // Navigate to study records
        });
    }
}
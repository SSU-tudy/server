package com.example.ssuwap.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.data.user.Subject;
import com.example.ssuwap.data.user.UserAccount;
import com.example.ssuwap.databinding.FragmentProfileMainBinding;
import com.example.ssuwap.ui.profile.calander.CalendarActivity;
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
    private DatabaseReference subjectRef;

    private List<Subject> subjectList = new ArrayList<>();
    private UserAccount currentUser;

    private String selectedGrade = "2"; // Default grade
    private String selectedSemester = "2"; // Default semester

    public static ProfileMainFragment newInstance(String param1, String param2) {
        ProfileMainFragment fragment = new ProfileMainFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRef = FirebaseDatabase.getInstance()
                .getReference("UserInfo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        subjectRef = FirebaseDatabase.getInstance()
                .getReference("timetableInfo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        setupUserProfile();
        setupTagsRecyclerView();
        setupEditProfileButton();
        setupActivityButtons();
        setupAddSubjectButton();
        setupTimetableTitleClickListener();

        initTimetableHeaders();
        initTimetable();
        loadSubjects();
    }

    // 유저 정보 설정
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
                userRef.setValue(updatedUser).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "프로필이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    setupUserProfile();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "프로필 업데이트 실패.", Toast.LENGTH_SHORT).show();
                });
            });

            bottomSheet.show(getParentFragmentManager(), "EditProfileBottomSheet");
        });
    }

    // 태그들
    private void setupTagsRecyclerView() {
        TagHistoryAdapter tagAdapter = new TagHistoryAdapter(new ArrayList<>());
        binding.rvTags.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTags.setAdapter(tagAdapter);
    }

    // 활동 3개로 이동
    private void setupActivityButtons() {
        binding.btnSalesHistory.setOnClickListener(v -> { });
        binding.btnMyPosts.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ProfilePostFragment.newInstance("param1", "param2"))
                    .addToBackStack(null)
                    .commit();
        });
        binding.btnStudyRecords.setOnClickListener(v -> startActivity(new Intent(requireActivity(), CalendarActivity.class)));
    }

    // 년도-학기 선택
    private void setupTimetableTitleClickListener() {
        binding.tvTimetableTitle.setOnClickListener(v -> {
            TimetableDialogFragment dialog = new TimetableDialogFragment((year, semester) -> {
                selectedGrade = year;
                selectedSemester = semester;
                binding.tvTimetableTitle.setText(String.format("%s학년 %s학기 시간표", selectedGrade, selectedSemester));
            });
            dialog.show(getParentFragmentManager(), "TimetableDialogFragment");
        });
    }
    // 과목 추가하기
    private void setupAddSubjectButton() {
        binding.btnAddSubject.setOnClickListener(v -> {
            AddSubjectBottomSheet bottomSheet = new AddSubjectBottomSheet();
            bottomSheet.setSubjectAddedListener(subject -> {
                Toast.makeText(getContext(), "과목이 추가되었습니다.", Toast.LENGTH_SHORT).show();
            });
            bottomSheet.show(getParentFragmentManager(), "AddSubjectBottomSheet");
        });
    }

    // 시간표 초기화
    private void initTimetableHeaders() {
        // 상단 요일 표시 (헤더)
        LinearLayout dayHeader = new LinearLayout(requireContext());
        dayHeader.setOrientation(LinearLayout.HORIZONTAL);
        dayHeader.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // 빈 공간 (시간 열과 맞추기)
        TextView emptySpace = new TextView(requireContext());
        emptySpace.setLayoutParams(new LinearLayout.LayoutParams(
                120, // 시간 표시 크기와 동일
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        dayHeader.addView(emptySpace);

        // 요일 추가
        String[] days = {"월요일", "화요일", "수요일", "목요일", "금요일"};
        for (String day : days) {
            TextView dayLabel = new TextView(requireContext());
            dayLabel.setText(day);
            dayLabel.setTextColor(Color.BLACK);
            dayLabel.setGravity(Gravity.CENTER);
            dayLabel.setTextSize(16);
            dayLabel.setLayoutParams(new LinearLayout.LayoutParams(
                    0, // 가중치로 균등 분배
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1 // 가중치
            ));
            dayHeader.addView(dayLabel);
        }

        // 요일 헤더 추가
        binding.timetableContainer.addView(dayHeader);
    }
    private void initTimetable() {
        for (int hour = 8; hour <= 21; hour++) { // 08:00 ~ 21:00
            // FrameLayout 생성
            FrameLayout hourFrame = new FrameLayout(requireContext());
            hourFrame.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    100 // 각 시간의 높이
            ));

            // 시간 숫자 표시
            TextView hourLabel = new TextView(requireContext());
            hourLabel.setText(String.format("%02d:00", hour)); // 예: 08:00
            hourLabel.setTextColor(Color.BLACK);
            hourLabel.setTextSize(15);
            hourLabel.setGravity(Gravity.CENTER_VERTICAL);
            FrameLayout.LayoutParams labelParams = new FrameLayout.LayoutParams(
                    120, // 시간 열 크기
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            labelParams.leftMargin = 10;
            hourLabel.setLayoutParams(labelParams);

            // 요일별 블록 영역 (LinearLayout)
            LinearLayout dayContainer = new LinearLayout(requireContext());
            dayContainer.setOrientation(LinearLayout.HORIZONTAL);
            FrameLayout.LayoutParams dayParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            dayParams.leftMargin = 120; // 시간 표시 공간 확보
            dayContainer.setLayoutParams(dayParams);

            // 요일 칸 추가
            for (int dayIndex = 0; dayIndex < 5; dayIndex++) { // 월~금
                View dayCell = new View(requireContext());
                LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(
                        0, // 너비는 가중치로 균등 분배
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1 // 가중치
                );
                dayCell.setLayoutParams(cellParams);
                dayCell.setBackgroundColor(Color.LTGRAY); // 기본 배경색
                dayContainer.addView(dayCell);
            }

            // FrameLayout에 추가
            hourFrame.addView(hourLabel);
            hourFrame.addView(dayContainer);

            // 타임테이블에 추가
            binding.timetableContainer.addView(hourFrame);
        }
    }

    // DB에서 가져오기
    private void loadSubjects() {
        // 학년_학기 키 설정
        String gradeSemesterKey = selectedGrade + "_" + selectedSemester;

        // Firebase 참조 경로에서 데이터 가져오기
        subjectRef.child(gradeSemesterKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 기존 데이터 초기화
                subjectList.clear();
                binding.timetableContainer.removeAllViews();

                // 헤더와 기본 시간표 초기화
                initTimetableHeaders();
                initTimetable();

                // Firebase에서 과목 데이터를 읽어와 리스트에 추가 및 시간표에 표시
                for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                    Subject subject = subjectSnapshot.getValue(Subject.class);
                    if (subject != null) {
                        subjectList.add(subject);
                        addSubjectToTimetable(subject); // 과목 데이터 추가
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
                Toast.makeText(getContext(), "시간표 데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addSubjectToTimetable(Subject subject) {
        int dayIndex = getDayIndex(subject.getDay());
        if (dayIndex < 0) {
            Log.e("Timetable", "Invalid day: " + subject.getDay());
            return; // 잘못된 요일
        }

        int startHour = getHourFromTime(subject.getStartTime());
        int endHour = getHourFromTime(subject.getEndTime());
        if (startHour < 8 || endHour > 21 || startHour >= endHour) {
            Log.e("Timetable", "Invalid time range: " + subject.getStartTime() + " - " + subject.getEndTime());
            return; // 잘못된 시간 범위
        }

        // 각 시간대에 새로운 View 추가
        for (int hour = startHour; hour < endHour; hour++) {
            int hourIndex = hour - 8; // 08:00부터 시작
            LinearLayout targetHour = (LinearLayout) ((FrameLayout) binding.timetableContainer.getChildAt(hourIndex + 1))
                    .getChildAt(1); // FrameLayout의 두 번째 자식이 dayContainer

            // 과목 블록 생성 (새로 생성해야 함)
            FrameLayout subjectBlock = new FrameLayout(requireContext());
            LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(
                    0, // 요일별로 가중치로 너비 설정
                    LinearLayout.LayoutParams.MATCH_PARENT, // 높이는 시간당 동일
                    1 // 가중치
            );
            subjectBlock.setLayoutParams(blockParams);
            subjectBlock.setBackgroundColor(Color.BLUE); // 배경색 (테스트용)

            // 과목명 텍스트
            TextView subjectLabel = new TextView(requireContext());
            subjectLabel.setText(subject.getSubjectName());
            subjectLabel.setTextColor(Color.WHITE);
            subjectLabel.setBackgroundColor(Color.DKGRAY);
            subjectLabel.setGravity(Gravity.CENTER);
            subjectBlock.addView(subjectLabel);

            // 기존 View 제거
            if (targetHour.getChildAt(dayIndex) != null) {
                targetHour.removeViewAt(dayIndex); // 기존 뷰 제거
            }

            // 새로운 View 추가
            targetHour.addView(subjectBlock, dayIndex);
        }
    }
    private int getDayIndex(String day) {
        if (day == null) return -1;
        switch (day) {
            case "월요일":
                return 0;
            case "화요일":
                return 1;
            case "수요일":
                return 2;
            case "목요일":
                return 3;
            case "금요일":
                return 4;
            default:
                return -1;
        }
    }
    private int getHourFromTime(String time) {
        if (time == null) return -1;
        try {
            String[] parts = time.split(":");
            return Integer.parseInt(parts[0]);
        } catch (Exception e) {
            return -1;
        }
    }
}
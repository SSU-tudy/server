package com.example.ssuwap.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ssuwap.R;
import com.example.ssuwap.data.user.Subject;
import com.example.ssuwap.data.user.UserAccount;
import com.example.ssuwap.databinding.FragmentProfileMainBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProfileMainFragment extends Fragment {

    private FragmentProfileMainBinding binding;
    private DatabaseReference userRef;

    private List<Subject> subjectList = new ArrayList<>();
    private UserAccount currentUser;

    public static ProfileMainFragment newInstance(String param1, String param2) {
        ProfileMainFragment fragment = new ProfileMainFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String param1 = getArguments().getString("ARG_PARAM1");
            String param2 = getArguments().getString("ARG_PARAM2");
            // 전달받은 인수를 처리할 수 있음
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

        // Firebase User Reference
        userRef = FirebaseDatabase.getInstance()
                .getReference("UserInfo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Setup UI Components
        setupUserProfile();
        setupTimetableBottomSheet();
        setupActivityButtons();
    }

    private void setupUserProfile() {
    }

    private void setupTimetableBottomSheet() {
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
        });
        binding.btnStudyRecords.setOnClickListener(v -> {
            // 공부 기록 이동 로직
        });
    }
}
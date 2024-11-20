package com.example.ssuwap.ui.todolist;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuwap.R;
import com.example.ssuwap.data.todolist.TodolistData;
import com.example.ssuwap.databinding.FragmentTodoMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodoMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoMainFragment extends Fragment implements TodomainAdapter.OnTimeBlockListener {
    private FragmentTodoMainBinding binding;
    private ArrayList<TodolistData> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private TodomainAdapter adapter;
    private LinearLayout timetableContainer;
    private DatabaseReference databaseReference;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TodoMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodoMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoMainFragment newInstance(String param1, String param2) {
        TodoMainFragment fragment = new TodoMainFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodoMainBinding.inflate(inflater, container, false);

        // Firebase Database Reference 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("TimeInfo");

        // RecyclerView 설정
        recyclerView = binding.rcvTodo;
        adapter = new TodomainAdapter(requireContext(), list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // 데이터 로드
        loadData();

        // 추가 버튼 클릭 리스너
        binding.btnAddrecord.setOnClickListener(v -> addItemDialog());

        // 타임테이블 초기화
        timetableContainer = binding.timetableContainer;
        initTimetable();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadData() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        databaseReference.child(year).child(month).child(day).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                    String subject = subjectSnapshot.getKey();
                    TodolistData todolistData = new TodolistData(subject);
                    list.add(todolistData);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "데이터 로드 실패: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addItemDialog() {

    }

    private void initTimetable() {
        for (int hour = 0; hour < 24; hour++) {
            FrameLayout hourFrame = new FrameLayout(requireContext());
            hourFrame.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    80
            ));

            TextView hourLabel = new TextView(requireContext());
            hourLabel.setText(String.format("%02d", hour));
            hourLabel.setTextColor(Color.BLACK);
            hourFrame.addView(hourLabel);

            timetableContainer.addView(hourFrame);
        }
    }

    @Override
    public void onTimeBlockSelected(int startHour, int startMinute, int endHour, int endMinute, int color) {
        colorTimeBlock(startHour, startMinute, endHour, endMinute, color);
    }

    private void colorTimeBlock(int startHour, int startMinute, int endHour, int endMinute, int color) {
        int startCell = startHour * 60 + startMinute;
        int endCell = endHour * 60 + endMinute;

        for (int i = startCell; i < endCell; i++) {
            int hour = i / 60;
            FrameLayout hourFrame = (FrameLayout) timetableContainer.getChildAt(hour);
            hourFrame.setBackgroundColor(color);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
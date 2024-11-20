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

import com.example.ssuwap.R;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.databinding.ActivitySellinglistAvtivityBinding;
import com.example.ssuwap.databinding.FragmentSellingBinding;
import com.example.ssuwap.ui.book.upload.UploadBookFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellingFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BookInfo> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FragmentSellingBinding binding;

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
        Log.d("SellinglistActivity", "Adapter attached to RecyclerView");

        // Firebase 데이터 로드
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("BookInfo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BookInfo bookInfo = snapshot.getValue(BookInfo.class);
                    if (bookInfo != null) {
                        Log.d("SellinglistActivity", "dataLoad");
                        arrayList.add(bookInfo);
                    }
                }
                Log.d("SellinglistActivity", "Data loaded from Firebase, arrayList size: " + arrayList.size());
                adapter.notifyDataSetChanged();  // 데이터 변경 알림
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SellinglistActivity", "Firebase error: " + error.toException());
            }
        });

        binding.sellingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 판매하러가기 실행
                startActivity(new Intent(requireContext(), UploadBookFormat.class));
            }
        });
    }
}
// BookMainFragment.java
package com.example.ssuwap.ui.book.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.data.user.UserAccount;
import com.example.ssuwap.databinding.FragmentBookMainBinding;
import com.example.ssuwap.ui.book.buying.BuyingActivity;
import com.example.ssuwap.ui.book.selling.SellingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookMainFragment extends Fragment {
    private ArrayList<BookInfo> arrayList;
    private MainAdapter mainAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageButton[] imageButtons;
    private String myName;
    private String myGrade;
    private String mySemester;
    private String myDepartment;
    private String myImgUrl;
    private FragmentBookMainBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public BookMainFragment() {
        // Required empty public constructor
    }

    public static BookMainFragment newInstance(String param1, String param2) {
        BookMainFragment fragment = new BookMainFragment();
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
        binding = FragmentBookMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView 설정
        recyclerView = binding.rv;
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        // arrayList 초기화
        arrayList = new ArrayList<>();

        // 추천도서 이미지버튼 설정
        imageButtons = new ImageButton[]{
                binding.recBook1,
                binding.recBook2,
                binding.recBook3,
                binding.recBook4,
                binding.recBook5,
                binding.recBook6
        };

        // 어댑터 초기화 및 연결 (Context 전달)
        mainAdapter = new MainAdapter(arrayList, requireContext());
        recyclerView.setAdapter(mainAdapter);

        // Firebase 사용자 인증 확인
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef;

        if (firebaseUser != null) {
            // 사용자 정보 참조
            userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid());

            // 사용자 정보 가져오기
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot taskSnapshot) {
                    UserAccount userInfo = taskSnapshot.getValue(UserAccount.class);
                    if (userInfo != null) {
                        // 올바른 필드로 변경
                        myName = userInfo.getStudentName(); // 수정된 필드명 사용
                        myGrade = userInfo.getGrade();
                        mySemester = userInfo.getSemester();
                        myDepartment = userInfo.getDepartment();
                        myImgUrl = userInfo.getImageUrl();

                        // 타이틀 설정
                        binding.tvTitle.setText(myName + "님 의 " + myGrade + " " + mySemester + " 추천도서");

                        // Firebase BookInfo 데이터 리스너 설정
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BookInfo");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                arrayList.clear();
                                int count = 0; // 로드된 책의 수를 추적

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    BookInfo bookInfo = dataSnapshot.getValue(BookInfo.class);
                                    if (bookInfo != null) {
                                        Log.d("BookMainFragment", "Book tag_subject: " + bookInfo.getTag_subject());
                                        Log.d("BookMainFragment", "My Department: " + myDepartment);

                                        // 조건 확인
                                        if (bookInfo.getTag_subject().equals(myDepartment)) {
                                            String imageUrl = bookInfo.getImageUrl();
                                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                                arrayList.add(bookInfo);
                                                count++;
                                            } else {
                                                Log.w("BookMainFragment", "Image URL is null or empty for book: " + bookInfo.getTitle());
                                            }
                                        }
                                    } else {
                                        Log.w("BookMainFragment", "BookInfo is null for key: " + dataSnapshot.getKey());
                                    }
                                }

                                // 데이터 변경 알림
                                mainAdapter.notifyDataSetChanged();

                                // 디버깅을 위해 데이터 수 출력
                                Log.d("BookMainFragment", "Loaded " + count + " books.");

                                // 추천도서 이미지 재설정
                                setImageButtons(snapshot);

                                // 내 얼굴 사진 설정
                                Glide.with(requireContext())
                                        .load(myImgUrl)
                                        .error(R.drawable.baseline_face_24)
                                        .into(binding.ivFace);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // 에러 처리
                                Toast.makeText(requireContext(), "데이터 로드 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("BookMainFragment", "Failed to load books", error.toException());
                            }
                        });
                    } else {
                        // 유저 정보가 null인 경우 처리
                        Toast.makeText(requireContext(), "유저 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                        Log.w("BookMainFragment", "UserInfo is null for user: " + firebaseUser.getUid());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 에러 처리
                    Toast.makeText(requireContext(), "유저 정보 가져오기 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("BookMainFragment", "Failed to get user info", error.toException());
                }
            });
        } else {
            Toast.makeText(requireContext(), "로그인 상태가 아닙니다.", Toast.LENGTH_SHORT).show();
            Log.w("BookMainFragment", "No user is currently logged in.");
        }

        // 판매 버튼 클릭 리스너 설정
        binding.btnSurfbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, SellingFragment.newInstance("param1", "param2"))
                        .addToBackStack(null).commit();
            }
        });
    }

    private void setImageButtons(DataSnapshot snapshot) {
        int count = 0;
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            if (count >= imageButtons.length) break;

            BookInfo bookInfo = dataSnapshot.getValue(BookInfo.class);
            if (bookInfo != null && bookInfo.getTag_subject().equals(myDepartment)) {
                String imageUrl = bookInfo.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imageButtons[count]);

                    final BookInfo currentBookInfo = bookInfo; // 변수를 final로 저장
                    imageButtons[count].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(requireContext(), BuyingActivity.class);
                            intent.putExtra("bookInfo", currentBookInfo); // 키를 "bookInfo"로 통일
                            startActivity(intent);
                        }
                    });
                    count++;
                } else {
                    Log.w("BookMainFragment", "Image URL is null or empty for imageButton at count " + count);
                }
            }
        }
    }
}
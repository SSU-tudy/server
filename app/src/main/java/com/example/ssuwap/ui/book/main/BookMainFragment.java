package com.example.ssuwap.ui.book.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.ssuwap.R;
import com.example.ssuwap.data.book.MainData;
import com.example.ssuwap.databinding.ActivityBookMainBinding;
import com.example.ssuwap.databinding.FragmentBookMainBinding;
import com.example.ssuwap.ui.book.selling.SellingFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookMainFragment extends Fragment {
    private ArrayList<MainData> arrayList;
    private MainAdapter mainAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageButton[] imageButtons;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookMainFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(requireContext() ,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //해당학기도서 RecyclerView
        arrayList = new ArrayList<>();
        arrayList.add(new MainData(R.mipmap.ic_launcher, "test1"));
        arrayList.add(new MainData(R.mipmap.ic_launcher, "test2"));
        arrayList.add(new MainData(R.mipmap.ic_launcher, "test3"));
        arrayList.add(new MainData(R.mipmap.ic_launcher, "test4"));
        arrayList.add(new MainData(R.mipmap.ic_launcher, "test5"));
        arrayList.add(new MainData(R.mipmap.ic_launcher, "test6"));

        mainAdapter = new MainAdapter(arrayList);
        recyclerView.setAdapter(mainAdapter);

        //추천도서 이미지버튼
        imageButtons = new ImageButton[]{
                view.findViewById(R.id.recBook1),
                view.findViewById(R.id.recBook2),
                view.findViewById(R.id.recBook3),
                view.findViewById(R.id.recBook4),
                view.findViewById(R.id.recBook5),
                view.findViewById(R.id.recBook6)
        };

        view.findViewById(R.id.btn_surfbook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, SellingFragment.newInstance("param1","param2"))
                        .addToBackStack(null).commit();
            }
        });
    }
}
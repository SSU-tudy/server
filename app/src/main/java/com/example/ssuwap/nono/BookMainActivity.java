//package com.example.ssuwap.nono;
//
//import android.os.Bundle;
//import android.widget.ImageButton;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.ssuwap.data.book.MainData;
//import com.example.ssuwap.R;
//import com.example.ssuwap.databinding.ActivityBookMainBinding;
//import com.example.ssuwap.ui.book.main.MainAdapter;
//
//import java.util.ArrayList;
//
//public class BookMainActivity extends AppCompatActivity {
//
//    private ArrayList<MainData> arrayList;
//    private MainAdapter mainAdapter;
//    private RecyclerView recyclerView;
//    private LinearLayoutManager linearLayoutManager;
//    private ImageButton[] imageButtons;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        ActivityBookMainBinding binding = ActivityBookMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        recyclerView = (RecyclerView) findViewById(R.id.rv);
//        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        //해당학기도서 RecyclerView
////        arrayList = new ArrayList<>();
////        arrayList.add(new MainData(R.mipmap.ic_launcher, "test1"));
////        arrayList.add(new MainData(R.mipmap.ic_launcher, "test2"));
////        arrayList.add(new MainData(R.mipmap.ic_launcher, "test3"));
////        arrayList.add(new MainData(R.mipmap.ic_launcher, "test4"));
////        arrayList.add(new MainData(R.mipmap.ic_launcher, "test5"));
////        arrayList.add(new MainData(R.mipmap.ic_launcher, "test6"));
//
//        mainAdapter = new MainAdapter(arrayList);
//        recyclerView.setAdapter(mainAdapter);
//
//        //추천도서 이미지버튼
//        imageButtons = new ImageButton []{
//
//        };
//    }
//}

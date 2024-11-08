package com.example.ssuwap;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<MainData> arrayList;
    private MainAdapter mainAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageButton[] imageButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
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
        imageButtons = new ImageButton []{
                binding.recBook1,
                binding.recBook2,
                binding.recBook3,
                binding.recBook4,
                binding.recBook5,
                binding.recBook6
        };
    }
}

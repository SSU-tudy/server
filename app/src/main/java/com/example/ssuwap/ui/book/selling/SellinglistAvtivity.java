package com.example.ssuwap.ui.book.selling;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ssuwap.R;
import com.example.ssuwap.data.SellingListData;
import com.example.ssuwap.data.TagData;
import com.example.ssuwap.ui.book.TaglistAdaptor;
import com.example.ssuwap.databinding.ActivitySellinglistAvtivityBinding;

import java.util.ArrayList;

public class SellinglistAvtivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySellinglistAvtivityBinding binding = ActivitySellinglistAvtivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<SellingListData> list = new ArrayList<>();
        list.add(new SellingListData("책팔아요","군주론", R.mipmap.ic_launcher,10000,1,1));
        list.add(new SellingListData("안팔아요","컴네",R.mipmap.ic_launcher,20000,2,2));
        list.add(new SellingListData("책안팔아요","컴구",R.mipmap.ic_launcher,30000,3,3));
        list.add(new SellingListData("책을팔아요","어쩌구",R.mipmap.ic_launcher,40000,4,4));
        list.add(new SellingListData("책팔까요","알고",R.mipmap.ic_launcher,50000,5,5));
        list.add(new SellingListData("눈치게임 시작","모르고ㅋㅋ",R.mipmap.ic_launcher,60000,6,6));

        binding.sellingrv.setLayoutManager(new LinearLayoutManager(this));
        binding.sellingrv.setAdapter(new SellingAdaptor(list));

        ArrayList<TagData> taglist = new ArrayList<>();
        taglist.add(new TagData("2학년"));
        taglist.add(new TagData("2학기"));
        taglist.add(new TagData("2모여기공기밥추가요"));

        binding.tagrv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false ));
        binding.tagrv.setAdapter(new TaglistAdaptor(taglist));
    }
}
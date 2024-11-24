package com.example.ssuwap.ui.book.buying;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.data.tag.TagData;
import com.example.ssuwap.databinding.ActivityBuyingBinding;
import com.example.ssuwap.ui.book.buying.chat.ChatActivity;

import java.util.ArrayList;

public class BuyingActivity extends AppCompatActivity {
    BookInfo bookinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBuyingBinding binding = ActivityBuyingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Intent 로 전달받은 book
        BookInfo bookInfo = (BookInfo) getIntent().getSerializableExtra("BookInfo");
        if (bookInfo != null) {
            Glide.with(this)
                    .load(bookInfo.getImageUrl())
                    .into(binding.ivBook);
            binding.tvTitle.setText(bookInfo.getTitle());
            binding.tvDescription.setText(bookInfo.getDescription());

            ArrayList<TagData> taglist = new ArrayList<>();
            taglist.add(new TagData(bookInfo.getTag_grade()));
            taglist.add(new TagData(bookInfo.getTag_semester()));
            taglist.add(new TagData(bookInfo.getTag_subject()));
            binding.rvTag.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvTag.setAdapter(new TaglistAdaptor(taglist));
        }

        Button button = binding.btnGoToChat;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyingActivity.this, ChatActivity.class);
                intent.putExtra("BookInfo", bookInfo);
                startActivity(intent);
            }
        });
    }
}
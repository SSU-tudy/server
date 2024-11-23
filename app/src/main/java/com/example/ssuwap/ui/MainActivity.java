package com.example.ssuwap.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ssuwap.R;
import com.example.ssuwap.databinding.ActivityMainBinding;
import com.example.ssuwap.ui.book.main.BookMainFragment;
import com.example.ssuwap.ui.post.uploadpost.PostMainFragment;
import com.example.ssuwap.ui.post.uploadpost.UploadPostFormat;
import com.example.ssuwap.ui.profile.ProfileMainFragment;
import com.example.ssuwap.ui.todolist.TodoMainFragment;
import com.example.ssuwap.ui.todolist.TodoMainFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.page_book) {
                    transferTo(BookMainFragment.newInstance("parma1", "param2"));
                    return true;
                }
                if (itemId == R.id.page_todo) {
                    transferTo(TodoMainFragment.newInstance("parma1", "param2"));
                    return true;
                }
                if (itemId == R.id.page_profile) {
                    transferTo(ProfileMainFragment.newInstance("parma1", "param2"));
                    return true;
                }

                if(itemId == R.id.page_question){
                    Log.d("MainActivity", "page_question");
                    transferTo(PostMainFragment.newInstance("Param1", "Param2"));
                    return true;
                }

                if(itemId == R.id.page_plus){
                    Log.d("MainActivity", "page_plus");
                    startActivity(new Intent(MainActivity.this, UploadPostFormat.class));
                }

                return false;
            }
        });
        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

            }
        });

        transferTo(BookMainFragment.newInstance("param1","param2"));
    }

    private void transferTo(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}

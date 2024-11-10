package com.example.ssuwap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.ssuwap.databinding.ActivityUploadBookFormatBinding;

public class UploadBookFormat extends AppCompatActivity {

    private ActivityUploadBookFormatBinding binding;
    private AlertDialog gradesDialog;
    private AlertDialog termsDialog;
    private AlertDialog subjectDialog;

    private DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(dialog == gradesDialog){
                String[] data = getResources().getStringArray(R.array.grades);
                binding.selectGrades.setText(data[which]);
                dialog.dismiss();
            }

            else if(dialog == termsDialog) {
                String[] data = getResources().getStringArray(R.array.term);
                binding.selectTerm.setText(data[which]);
                dialog.dismiss();
            }

            else if(dialog == subjectDialog){
                String[] data = getResources().getStringArray(R.array.subject);
                binding.selectSubject.setText(data[which]);
                dialog.dismiss();
            }
        }
    };

    private final ActivityResultLauncher<Intent> uploadBookLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // 결과 데이터에서 ISBN 가져오기
                    String isbn = result.getData().getStringExtra("ISBN");
                    Log.d("UploadBookFormatCheck", "Received ISBN: " + isbn);
                    fetchAndDisplayBookInfoNaver(isbn);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBookFormatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.scanBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("UploadBookFormat", "Starting UploadBook Activity");
                Intent intent = new Intent(UploadBookFormat.this, UploadBookScan.class);
                uploadBookLauncher.launch(intent);
            }
        });

        binding.selectGrades.setOnClickListener(view -> {
            gradesDialog = new AlertDialog.Builder(this)
                    .setTitle("학년을 선택하세요.")
                    .setSingleChoiceItems(R.array.grades, 0, dialogListener)
                    .create();
            gradesDialog.show();
        });

        binding.selectTerm.setOnClickListener(view -> {
            termsDialog = new AlertDialog.Builder(this)
                    .setTitle("학기를 선택하세요.")
                    .setSingleChoiceItems(R.array.term, 0, dialogListener)
                    .create();
            termsDialog.show();
        });

        binding.selectSubject.setOnClickListener(view -> {
            subjectDialog = new AlertDialog.Builder(this)
                    .setTitle("과목을 선택하세요.")
                    .setSingleChoiceItems(R.array.subject, 0, dialogListener)
                    .create();
            subjectDialog.show();
        });
    }

    private void fetchAndDisplayBookInfoNaver(String isbn) {
        NaverBookInfoFetcher fetcher = new NaverBookInfoFetcher(new NaverBookInfoFetcher.OnBookInfoFetchedListener() {
            @Override
            public void onBookInfoFetched(String title, String authors, String publisher, String publishedDate, String imageUrl) {
                binding.titleTextView.setText("제목: " + title);
                binding.authorsTextView.setText("저자: " + authors);
                binding.publisherTextView.setText("출판사: " + publisher);
                // Glide를 사용해 이미지 로드
                Glide.with(UploadBookFormat.this)
                        .load(imageUrl)
                        .into(binding.uploadBookimage); // 이미지가 표시될 ImageView
            }

            @Override
            public void onError(Exception e) {
                Log.e("UploadBookFormat", "Error fetching book info", e);
                binding.titleTextView.setText("Error fetching book info");
            }
        });

        fetcher.fetchBookInfo(isbn);
    }
}
package com.example.ssuwap.ui.book.upload;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.databinding.ActivityUploadBookFormatBinding;
import com.example.ssuwap.ui.book.buying.chat.ChatActivity;
import com.example.ssuwap.ui.book.upload.isbn.NaverBookInfoFetcher;
import com.example.ssuwap.ui.book.upload.isbn.TagSelectFragment;
import com.example.ssuwap.ui.book.upload.isbn.UploadBookScan;
import com.example.ssuwap.ui.post.uploadpost.TagDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class UploadBookFormat extends AppCompatActivity implements TagSelectFragment.TagDialogListener {

    private static final String TAG = "UploadBookFormat";

    private ActivityUploadBookFormatBinding binding;
    private AlertDialog gradesDialog;
    private AlertDialog termsDialog;
    private AlertDialog subjectDialog;

    private String titleBook;
    private String authorBook;
    private String publisherBook;
    private String imageUrlBook;
    private String myName;
    private int timeUpload;

    private FirebaseAuth mFirebaseAuth;     // FB 인증
    private DatabaseReference mDatabaseRef;

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
        Log.d("UploadBookFormat", "excute check");
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBookFormatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getUserInfroForFirebase();

        binding.selectGrades.setVisibility(View.VISIBLE);
        binding.selectTerm.setVisibility(View.GONE);
        binding.selectSubject.setVisibility(View.GONE);


        if(binding == null) Log.d("UploadBookFormat", "binding fail");
        Log.d("UploadBookFormat", "binding check");
        binding.scanBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("UploadBookFormat", "Starting UploadBook Activity");
                Intent intent = new Intent(UploadBookFormat.this, UploadBookScan.class);
                uploadBookLauncher.launch(intent);

            }
        });

        binding.selectGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick()+"+TAG);
                TagSelectFragment dialog = TagSelectFragment.newInstance("p1", "p2");
                dialog.show(getSupportFragmentManager(), "TagDialog");
            }
        });

        binding.uploadBookButton.setEnabled(false);
        getUserInfroForFirebase();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("BookInfo");
        binding.uploadBookButton.setOnClickListener(view -> {
            Log.d(TAG, "uploadCheck");
            String price = binding.bookPrice.getText().toString();
            String grade = binding.selectGrades.getText().toString();
            String subject = binding.selectSubject.getText().toString();
            String semester = binding.selectTerm.getText().toString();
            String description = binding.detailInfoBook.getText().toString();
            long time = System.currentTimeMillis();

            Log.d(TAG, price+"원");
            Log.d(TAG, grade);
            Log.d(TAG, subject);
            Log.d(TAG, semester);
            Log.d(TAG, description);

            BookInfo book = new BookInfo(titleBook, imageUrlBook, authorBook, publisherBook, description, grade, semester, subject, price, time, false, myName);
            mDatabaseRef.push().setValue(book)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "업로드 성공", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            finish();
        });
    }

    private void fetchAndDisplayBookInfoNaver(String isbn) {
        NaverBookInfoFetcher fetcher = new NaverBookInfoFetcher(new NaverBookInfoFetcher.OnBookInfoFetchedListener() {
            @Override
            public void onBookInfoFetched(String title, String authors, String publisher, String publishedDate, String imageUrl) {
                titleBook = title;
                authorBook = authors;
                publisherBook = publisher;
                imageUrlBook = imageUrl;
                updateUploadButtonState();

                Log.d(TAG, titleBook);
                Log.d(TAG, imageUrlBook);
                Log.d(TAG, authorBook);
                Log.d(TAG, publisherBook);

                binding.titleTextView.setText("제목: " + title);
                binding.authorsTextView.setText("저자: " + authors);
                binding.publisherTextView.setText("출판사: " + publisher);
                Log.d(TAG, imageUrlBook);
                Log.d(TAG, myName);

                // Glide를 사용해 이미지 로드
                Glide.with(UploadBookFormat.this)
                        .load(imageUrl)
                        .into(binding.uploadBookimage); // 이미지가 표시될 ImageView
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error fetching book info", e);
                binding.titleTextView.setText("Error fetching book info");
                imageUrlBook = null;
                updateUploadButtonState();
            }
        });

        fetcher.fetchBookInfo(isbn);
    }

    private void getUserInfroForFirebase() {
        Log.d(TAG, "getUserInfroForFirebase()");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        Log.d(TAG, ""+firebaseUser.getUid());

        if (firebaseUser != null) {
            myName = firebaseUser.getUid();
            updateUploadButtonState();
        } else {
            myName = null;
            updateUploadButtonState();
        }
    }

    private void updateUploadButtonState() {
        binding.uploadBookButton.setEnabled(imageUrlBook != null && myName != null);
    }

    @Override
    public void onTagSelected(String grade, String semester, String subject) {
        binding.selectGrades.setVisibility(View.VISIBLE);
        binding.selectTerm.setVisibility(View.VISIBLE);
        binding.selectSubject.setVisibility(View.VISIBLE);

        binding.selectGrades.setText(grade);
        binding.selectTerm.setText(semester);
        binding.selectSubject.setText(subject);
    }
}
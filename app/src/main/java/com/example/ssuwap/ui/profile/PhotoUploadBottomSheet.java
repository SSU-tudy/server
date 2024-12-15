package com.example.ssuwap.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ssuwap.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PhotoUploadBottomSheet extends BottomSheetDialogFragment {

    private static final int SELECT_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    public interface OnPhotoUploadedListener {
        void onPhotoUploaded(Uri photoUri);
    }

    private OnPhotoUploadedListener listener;

    public void setOnPhotoUploadedListener(OnPhotoUploadedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_photo_upload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_select_image).setEnabled(true);
        view.findViewById(R.id.btn_upload_image).setEnabled(true);

        // 이미지 선택 버튼
        view.findViewById(R.id.btn_select_image).setOnClickListener(v -> openImagePicker());

        // 업로드 버튼
        view.findViewById(R.id.btn_upload_image).setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri);
            } else {
                Toast.makeText(requireContext(), "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                view.findViewById(R.id.btn_select_image).setEnabled(false);
                view.findViewById(R.id.btn_upload_image).setEnabled(false);
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // 선택한 이미지 미리보기
            if (getView() != null && selectedImageUri != null) {
                ImageView imageView = getView().findViewById(R.id.iv_photo_preview);
                imageView.setImageURI(selectedImageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // Firebase Storage 참조
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images").child(userId + ".jpg");

        // 이미지 업로드
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Firebase Realtime Database에 이미지 URL 저장
                    saveImageUrlToDatabase(uri.toString());
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "이미지 URL 가져오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userId);

        // imageUrl 필드 업데이트
        userRef.child("imageUrl").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "프로필 이미지가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onPhotoUploaded(Uri.parse(imageUrl));
                    }
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "이미지 URL 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
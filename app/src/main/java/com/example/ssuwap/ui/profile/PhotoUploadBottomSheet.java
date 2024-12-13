package com.example.ssuwap.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ssuwap.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PhotoUploadBottomSheet extends BottomSheetDialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ivPreview;
    private Uri selectedImageUri;

    public interface PhotoUploadListener {
        void onPhotoUploaded(String imageUrl);
    }

    private PhotoUploadListener listener;

    public PhotoUploadBottomSheet(PhotoUploadListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_photo_upload, container, false);
        ivPreview = view.findViewById(R.id.iv_photo_preview);
        Button btnSelect = view.findViewById(R.id.btn_select_image);
        Button btnUpload = view.findViewById(R.id.btn_upload_image);

        btnSelect.setOnClickListener(v -> openImagePicker());
        btnUpload.setOnClickListener(v -> uploadImageToFirebase());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ivPreview.setImageURI(selectedImageUri);
        }
    }

    private void uploadImageToFirebase() {
        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "No image selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

        storageRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            if (listener != null) {
                                listener.onPhotoUploaded(uri.toString());
                            }
                            dismiss();
                        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to retrieve image URL.", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed.", Toast.LENGTH_SHORT).show());
    }
}
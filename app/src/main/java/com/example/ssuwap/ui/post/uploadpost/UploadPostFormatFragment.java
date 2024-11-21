//package com.example.ssuwap.ui.post.uploadpost;
//
//import android.Manifest;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//import androidx.fragment.app.Fragment;
//
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.ssuwap.R;
//import com.example.ssuwap.data.post.PostInfo;
//import com.example.ssuwap.databinding.FragmentUploadPostFormatBinding;
//import com.example.ssuwap.ui.post.uploadpost.comment.PostMainFragment;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.UUID;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link UploadPostFormatFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class UploadPostFormatFragment extends Fragment {
//
//    private static final int REQUEST_CAMERA_PERMISSION = 200;
//    private FragmentUploadPostFormatBinding binding;
//    private Uri photoUri;
//    private String photoURL;
//    private Bitmap imageBitmap;
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public UploadPostFormatFragment() {
//        // Required empty public constructor
//    }
//
//    private final ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == getActivity().RESULT_OK) {
//                    Log.d("UploadPostFragment", "Camera activity result received");
//                    Bundle extras = result.getData().getExtras();
//                    imageBitmap = (Bitmap) extras.get("data");
//                    binding.uploadImagePost.setImageBitmap(imageBitmap);
//                    photoUri = saveBitmapAndGetUri(imageBitmap);
//                    Log.d("UploadPostFragment", "photoUri: " + photoUri);
//                    uploadImageToFirebase(photoUri);
//                }
//            }
//    );
//
//
//    // TODO: Rename and change types and number of parameters
//    public static UploadPostFormatFragment newInstance(String param1, String param2) {
//        UploadPostFormatFragment fragment = new UploadPostFormatFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d("UploadPostFragment", "onCreate()");
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentUploadPostFormatBinding.inflate(inflater, container, false);
//
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        binding.scanPostButton.setOnClickListener(v -> {
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
//            } else {
//                openCamera();
//            }
//        });
//
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PostInfo");
//        binding.uploadPostButton.setOnClickListener(v -> {
//            String detailPost = binding.postDetailInfo.getText().toString();
//            Log.d("UploadPostFragment", "detailPost: " + detailPost);
//            Log.d("UploadPostFragment", "URL: " + photoURL);
//
//            DatabaseReference postRef = databaseReference.push();
//            String postId = postRef.getKey();
//
//            PostInfo postInfo = new PostInfo(postId, photoURL, detailPost, new HashMap<>());
//
//            postRef.setValue(postInfo).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Log.d("UploadPostFragment", "Post added successfully!");
//
//                    navigateToPostMainFragment();
//                } else {
//                    Log.e("UploadPostFragment", "Failed to add post", task.getException());
//                }
//            });
//        });
//    }
//
//    private void openCamera() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
//            startActivityResult.launch(takePictureIntent);
//        }
//    }
//
//    private Uri saveBitmapAndGetUri(Bitmap bitmap) {
//        try {
//            File tempFile = new File(requireContext().getExternalFilesDir(null), "temp_image.jpg");
//            FileOutputStream outputStream = new FileOutputStream(tempFile);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//            outputStream.flush();
//            outputStream.close();
//            return FileProvider.getUriForFile(requireContext(), "com.example.ssuwap.ui.post.uploadpost.fileprovider", tempFile);
//        } catch (IOException e) {
//            Log.e("UploadPostFragment", "Error saving bitmap: " + e.getMessage(), e);
//            return null;
//        }
//    }
//
//    private void uploadImageToFirebase(Uri imageUri) {
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString());
//        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
//            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                photoURL = uri.toString();
//                Log.d("UploadPostFragment", "Image uploaded: " + photoURL);
//            });
//        }).addOnFailureListener(e -> Log.e("UploadPostFragment", "Image upload failed", e));
//    }
//
//    private void navigateToPostMainFragment() {
//        Log.d("UploadPostFragment","navigateToPostMainFragment()");
//        PostMainFragment postMainFragment = new PostMainFragment();
//        Log.d("UploadPostFragment", "Created PostMainFragment: " + postMainFragment);
//        Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        Log.d("FragmentManager", "Current Fragment: " + currentFragment);
//        requireActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, postMainFragment) // container는 Fragment를 표시할 FrameLayout ID
//                .commit();
//        Log.d("UploadPostFragment", "Transaction committed for PostMainFragment");
//    }
//}
//

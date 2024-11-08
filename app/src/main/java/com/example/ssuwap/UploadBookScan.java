package com.example.ssuwap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ssuwap.databinding.ActivityUploadBookScanBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.concurrent.ExecutionException;


public class UploadBookScan extends AppCompatActivity {

        private ActivityUploadBookScanBinding binding;
        private static final int REQUEST_CAMERA_PERMISSION = 200;
        private PreviewView previewView;
        private Button startCameraButton;
        private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
        private ProcessCameraProvider cameraProvider;
        private boolean isCameraInitialized = false;
        private static final String check = "check";
        private boolean barcodeFound = false; // 바코드가 이미 인식되었는지 여부

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityUploadBookScanBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            Log.d("aaaa", "intetn");
            previewView = binding.previewView;

            Log.d("check", "onCreate: Activity created");

            if (ContextCompat.checkSelfPermission(UploadBookScan.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraInitialized) {
                    Log.d("check", "onClick: Camera not initialized, starting camera");
                    isCameraInitialized = true;
                    startCamera();
                } else {
                    Log.d("check", "onClick: Camera already initialized");
                }
            } else {
                Log.d("check", "onClick: Camera permission not granted, requesting permission");
                ActivityCompat.requestPermissions(UploadBookScan.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        }

        private void startCamera() {
            Log.d("check", "startCamera: Starting camera");
            cameraProviderFuture = ProcessCameraProvider.getInstance(this);
            cameraProviderFuture.addListener(() -> {
                try {
                    cameraProvider = cameraProviderFuture.get();
                    Log.d("check", "startCamera: CameraProvider obtained");
                    bindPreviewAndAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    Log.e("check", "startCamera: Error starting camera", e);
                    isCameraInitialized = false;
                    runOnUiThread(() -> startCameraButton.setEnabled(true)); // 초기화 실패 시 버튼 다시 활성화
                }
            }, ContextCompat.getMainExecutor(this));
        }

        private void bindPreviewAndAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
            try {
                Log.d("check", "bindPreviewAndAnalysis: Binding camera preview and image analysis");

                // 기존 바인딩 해제
                cameraProvider.unbindAll();

                // Preview Use Case 설정
                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // ImageAnalysis Use Case 설정 - 바코드 인식을 위한 실시간 이미지 분석
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setTargetResolution(new android.util.Size(640, 480)) // 해상도 낮추기
                        .build();

                BarcodeScanner barcodeScanner = BarcodeScanning.getClient();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
                    if (!barcodeFound) { // 바코드가 인식되지 않았을 때만 분석
                        try {
                            Log.d("check", "bindPreviewAndAnalysis: Analyzing image");
                            InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
                            barcodeScanner.process(image)
                                    .addOnSuccessListener(barcodes -> {
                                        Log.d("check", "bindPreviewAndAnalysis: Barcode scanning successful");
                                        for (Barcode barcode : barcodes) {
                                            String rawValue = barcode.getRawValue();
                                            if (rawValue != null) {
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("ISBN", rawValue); // 스캔한 ISBN 값
                                                setResult(RESULT_OK, resultIntent);
                                                Log.d("check", "bindPreviewAndAnalysis: ISBN found - " + rawValue);
                                                barcodeFound = true; // 바코드를 인식한 후 추가 분석 중지
                                                finish();
                                                break;
                                            }
                                        }
                                        imageProxy.close();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("check", "bindPreviewAndAnalysis: Barcode scanning failed", e);
                                        imageProxy.close();
                                    });
                        } catch (Exception e) {
                            Log.e("check", "bindPreviewAndAnalysis: Error analyzing image", e);
                            imageProxy.close();
                        }
                    } else {
                        imageProxy.close();
                    }
                });

                // Preview와 ImageAnalysis를 바인딩
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

                Log.d(check, "bindPreviewAndAnalysis: Camera preview and analysis bound successfully");

                // 카메라 초기화가 끝났으므로 상태 초기화
                runOnUiThread(() -> {
                    isCameraInitialized = false;
                });

            } catch (Exception e) {
                Log.e("check", "bindPreviewAndAnalysis: Error binding camera preview and analysis", e);
                isCameraInitialized = false;
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_CAMERA_PERMISSION) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("check", "onRequestPermissionsResult: Camera permission granted");
                    previewView.postDelayed(this::startCamera, 1000); // 권한 허용 후 500ms 지연 후 카메라 시작
                } else {
                    Log.d("check", "onRequestPermissionsResult: Camera permission denied");
                    Toast.makeText(this, "Camera permission is required to scan ISBN", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
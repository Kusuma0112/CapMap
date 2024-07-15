package com.example.capmap.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.capmap.Constant.Allconstant;
import com.example.capmap.R;
import com.example.capmap.Usermodel;
import com.example.capmap.Utility.LoadingDialog;
import com.example.capmap.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private Uri ImageUri;
    private LoadingDialog loadingDialog;
    private String email, username, password;
    private StorageReference storageReference;

    // Permission constant based on Android version
    private static final String READ_STORAGE_PERMISSION = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ?
            Manifest.permission.READ_MEDIA_IMAGES :
            Manifest.permission.READ_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadingDialog = new LoadingDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.txtLogin.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.btnSignUp.setOnClickListener(view -> {
            if (areFieldReady()) {
                if (ImageUri != null) {
                    signUp();
                } else {
                    Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.imgPick.setOnClickListener(view -> {
            pickImage();
        });
    }

    private void requestStoragePermission() {
        if (shouldShowRequestPermissionRationale(READ_STORAGE_PERMISSION)) {
            Toast.makeText(this, "Storage permission is required to pick image", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{READ_STORAGE_PERMISSION}, Allconstant.STORAGE_REQUEST_CODE);
    }

    private void pickImage() {
        if (ContextCompat.checkSelfPermission(this, READ_STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Allconstant.PICK_IMAGE_REQUEST_CODE);
        } else {
            requestStoragePermission();
        }
    }

    private boolean areFieldReady() {
        username = binding.edtUsername.getText().toString().trim();
        email = binding.edtEmail.getText().toString().trim();
        password = binding.edtPassword.getText().toString().trim();

        boolean flag = false;
        View requestView = null;

        if (username.isEmpty()) {
            binding.edtUsername.setError("Field is required");
            flag = true;
            requestView = binding.edtUsername;
        } else if (email.isEmpty()) {
            binding.edtEmail.setError("Field is required");
            flag = true;
            requestView = binding.edtEmail;
        } else if (password.isEmpty()) {
            binding.edtPassword.setError("Field is required");
            flag = true;
            requestView = binding.edtPassword;
        } else if (password.length() < 8) {
            binding.edtPassword.setError("Minimum 8 characters");
            flag = true;
            requestView = binding.edtPassword;
        }

        if (flag) {
            requestView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void signUp() {
        loadingDialog.startLoading();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> signUp) {
                if (signUp.isSuccessful()) {
                    storageReference.child(firebaseAuth.getUid() + Allconstant.IMAGE_PATH).putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> image = taskSnapshot.getStorage().getDownloadUrl();
                            image.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> imageTask) {
                                    if (imageTask.isSuccessful()) {
                                        String url = imageTask.getResult().toString();
                                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .setPhotoUri(Uri.parse(url))
                                                .build();

                                        firebaseAuth.getCurrentUser().updateProfile(profileChangeRequest).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Usermodel userModel = new Usermodel(email, username, url, true);
                                                            databaseReference.child(firebaseAuth.getUid())
                                                                    .setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            loadingDialog.stopLoading();
                                                                                            Toast.makeText(SignUpActivity.this, "Verify email", Toast.LENGTH_SHORT).show();
                                                                                            onBackPressed();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        } else {
                                                            loadingDialog.stopLoading();
                                                            Log.d("TAG", "onComplete: Update Profile" + task.getException());
                                                            Toast.makeText(SignUpActivity.this, "Update Profile" + task.getException(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        loadingDialog.stopLoading();
                                        Log.d("TAG", "onComplete: Image Path" + imageTask.getException());
                                        Toast.makeText(SignUpActivity.this, "Image Path" + imageTask.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                } else {
                    loadingDialog.stopLoading();
                    Log.d("TAG", "onComplete: Create user" + signUp.getException());
                    Toast.makeText(SignUpActivity.this, "" + signUp.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Allconstant.PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                startCrop(selectedImageUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null) {
            ImageUri = UCrop.getOutput(data);
            if (ImageUri != null) {
                Glide.with(this).load(ImageUri).into(binding.imgPick);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.d("TAG", "onActivityResult: " + cropError);
        }
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = "SampleCropImage.jpeg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(450, 450);
        uCrop.withOptions(getCropOptions());
        uCrop.start(SignUpActivity.this);
    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);
        options.setCircleDimmedLayer(true);
        // Set toolbar color
        options.setToolbarColor(ContextCompat.getColor(this, R.color.primaryColor));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.primaryDarkColor));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.primaryColor));

        return options;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Allconstant.STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(this, "Storage permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

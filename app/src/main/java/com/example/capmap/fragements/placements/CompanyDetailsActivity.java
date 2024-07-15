package com.example.capmap.fragements.placements;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.capmap.R;
import com.example.capmap.fragements.contribute.CompanyModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CompanyDetailsActivity extends AppCompatActivity {

    private TextView companyNameTextView;
    private TextView statusTextView;
    private TextView offerTextView;
    private TextView descriptionTextView;
    private ImageView companyLogoImageView;
    private Button backButton;

    private DatabaseReference databaseReference;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_details_activity);

        companyNameTextView = findViewById(R.id.edt_company_name);
        statusTextView = findViewById(R.id.edt_status);
        offerTextView = findViewById(R.id.edt_offer);
        descriptionTextView = findViewById(R.id.edt_description);
        companyLogoImageView = findViewById(R.id.img_logo);
        backButton = findViewById(R.id.btn_add_company);

        databaseReference = FirebaseDatabase.getInstance().getReference("Companies");
        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CompanyModel company = snapshot.getValue(CompanyModel.class);
                if (company != null) {
                    companyNameTextView.setText(company.getCompanyName());
                    statusTextView.setText(company.getStatus());
                    offerTextView.setText(company.getOffer());
                    descriptionTextView.setText(company.getDescription());

                    StorageReference storageReference = storage.getReference("companies/" + company.getKey() + "/logo.jpg");
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            Glide.with(CompanyDetailsActivity.this)
                                    .load(downloadUrl)
                                    .into(companyLogoImageView);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CompanyDetailsActivity", "Error fetching company data", error.toException());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
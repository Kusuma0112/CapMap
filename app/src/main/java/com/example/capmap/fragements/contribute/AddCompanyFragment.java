package com.example.capmap.fragements.contribute;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.capmap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class AddCompanyFragment extends Fragment {

    private EditText edtCompanyName, edtStatus, edtOffer, edtDescription;
    private ImageView imgLogo;
    private Button btnAddCompany;
    private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_company, container, false);

        edtCompanyName = root.findViewById(R.id.edt_company_name);
        edtStatus = root.findViewById(R.id.edt_status);
        edtOffer = root.findViewById(R.id.edt_offer);
        edtDescription = root.findViewById(R.id.edt_description);
        imgLogo = root.findViewById(R.id.img_logo);
        btnAddCompany = root.findViewById(R.id.btn_add_company);

        storageReference = FirebaseStorage.getInstance().getReference();

        btnAddCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCompany();
            }
        });

        return root;
    }

    private void addCompany() {
        String companyName = edtCompanyName.getText().toString();
        String status = edtStatus.getText().toString();
        String offer = edtOffer.getText().toString();
        String description = edtDescription.getText().toString();

        if (companyName.isEmpty() || status.isEmpty() || offer.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload logo to Firebase Storage
        imgLogo.setDrawingCacheEnabled(true);
        imgLogo.buildDrawingCache();
        Bitmap bitmap = imgLogo.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference logoRef = storageReference.child("companies/" + companyName + "/logo.jpg");
        logoRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            // Add company data to Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Companies");
            String key = databaseReference.push().getKey();
            String contributorKey = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user's UID
            CompanyModel companyModel = new CompanyModel(companyName, status, offer, description, logoRef.toString(), key, contributorKey); // Pass contributorKey
            databaseReference.child(key).setValue(companyModel);

            Toast.makeText(getContext(), "Company added successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error adding company", Toast.LENGTH_SHORT).show();
        });
    }
}
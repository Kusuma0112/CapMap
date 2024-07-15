package com.example.capmap.fragements.contribute;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.capmap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class EditCompanyFragment extends Fragment {

    private Spinner spinnerCompanies;
    private EditText edtCompanyName, edtStatus, edtOffer, edtDescription;
    private ImageView imgLogo;
    private Button btnEditCompany;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private CompanyModel companyModel;
    private ArrayList<String> companyNames = new ArrayList<>();
    private ArrayAdapter<String> companyAdapter;
    private FirebaseUser currentUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_company, container, false);

        spinnerCompanies = root.findViewById(R.id.spinner_companies);
        edtCompanyName = root.findViewById(R.id.edt_company_name);
        edtStatus = root.findViewById(R.id.edt_status);
        edtOffer = root.findViewById(R.id.edt_offer);
        edtDescription = root.findViewById(R.id.edt_description);
        imgLogo = root.findViewById(R.id.img_logo);
        btnEditCompany = root.findViewById(R.id.btn_edit_company);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Companies");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get company names from Firebase Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companyNames.clear();
                for (DataSnapshot companySnapshot : snapshot.getChildren()) {
                    String companyName = companySnapshot.child("companyName").getValue(String.class);
                    companyNames.add(companyName);
                }
                companyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching company names: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        companyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, companyNames);
        companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCompanies.setAdapter(companyAdapter);

        spinnerCompanies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCompany = companyNames.get(position);
                Log.d("EditCompanyFragment", "Selected company: " + selectedCompany);

                // Find the correct company node based on the selected company name
                databaseReference.orderByChild("companyName").equalTo(selectedCompany).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot companySnapshot : snapshot.getChildren()) {
                                companyModel = companySnapshot.getValue(CompanyModel.class);
                                if (companyModel != null) {
                                    Log.d("EditCompanyFragment", "Company data: " + companyModel.toString());
                                    if (companyModel.getContributorKey().equals(currentUser.getUid())) {
                                        edtCompanyName.setText(companyModel.getCompanyName());
                                        edtStatus.setText(companyModel.getStatus());
                                        edtOffer.setText(companyModel.getOffer());
                                        edtDescription.setText(companyModel.getDescription());
                                        Glide.with(EditCompanyFragment.this).load(companyModel.getLogoUrl()).into(imgLogo);
                                        // Call editCompany method here
                                        btnEditCompany.setOnClickListener(v -> editCompany());
                                    } else {
                                        Toast.makeText(getContext(), "You are not authorized to edit this company", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Company data not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "Company data not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error fetching company data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }

    private void editCompany() {
        if (companyModel == null) {
            Toast.makeText(getContext(), "Please select a company to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        String companyName = edtCompanyName.getText().toString();
        String status = edtStatus.getText().toString();
        String offer = edtOffer.getText().toString();
        String description = edtDescription.getText().toString();

        if (companyName.isEmpty() || status.isEmpty() || offer.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        companyModel.setCompanyName(companyName);
        companyModel.setStatus(status);
        companyModel.setOffer(offer);
        companyModel.setDescription(description);

        databaseReference.child(companyModel.getKey()).setValue(companyModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Company updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error updating company: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

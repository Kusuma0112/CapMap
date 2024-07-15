package com.example.capmap.fragements.placements;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capmap.R;
import com.example.capmap.databinding.FragmentPlacementBinding;
import com.example.capmap.fragements.contribute.CompanyModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PlacementFragment extends Fragment {

    private FragmentPlacementBinding binding;
    private PlacementAdapter adapter;
    private List<CompanyModel> companies;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlacementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlacementAdapter();
        recyclerView.setAdapter(adapter);

        companies = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Companies");
        storage = FirebaseStorage.getInstance();

        fetchCompanyData();

        binding.calendarButton.setOnClickListener(v -> {
            // Show calendar dialog or activity here
        });

        return root;
    }

    private void fetchCompanyData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companies.clear();
                for (DataSnapshot companySnapshot : snapshot.getChildren()) {
                    CompanyModel company = companySnapshot.getValue(CompanyModel.class);
                    if (company != null) {
                        company.setKey(companySnapshot.getKey());
                        companies.add(company);
                    }
                }
                adapter.setCompanies(companies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PlacementFragment", "Error fetching company data", error.toException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class PlacementAdapter extends RecyclerView.Adapter<PlacementAdapter.ViewHolder> {

        private List<CompanyModel> companies;

        public void setCompanies(List<CompanyModel> companies) {
            this.companies = companies;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.placement_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CompanyModel company = companies.get(position);
            StorageReference storageReference = storage.getReference("companies/" + company.getKey() + "/logo.jpg");
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String downloadUrl = uri.toString();
                    Glide.with(holder.itemView.getContext())
                            .load(downloadUrl)
                            .into(holder.companyLogo);
                }
            });

            holder.companyName.setText(company.getCompanyName());
            holder.status.setText(company.getStatus());
            holder.offer.setText(company.getOffer());

            holder.itemView.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    CompanyModel selectedCompany = companies.get(adapterPosition);
                    Intent intent = new Intent(getContext(), CompanyDetailsActivity.class);
                    intent.putExtra("key", selectedCompany.getKey());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return companies == null ? 0 : companies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView companyLogo;
            TextView companyName;
            TextView status;
            TextView offer;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                companyLogo = itemView.findViewById(R.id.company_logo);
                companyName = itemView.findViewById(R.id.company_name);
                status = itemView.findViewById(R.id.status);
                offer = itemView.findViewById(R.id.offer);
            }
        }
    }
}
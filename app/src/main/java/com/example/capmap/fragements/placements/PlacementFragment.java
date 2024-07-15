package com.example.capmap.fragements.placements;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.capmap.databinding.FragmentPlacementBinding;
import com.example.capmap.R;

import java.util.ArrayList;
import java.util.List;

public class PlacementFragment extends Fragment {

    private FragmentPlacementBinding binding;
    private PlacementAdapter adapter;
    private List<Company> companies; // Add a list to hold company data

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentPlacementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlacementAdapter();
        recyclerView.setAdapter(adapter);

        // Initialize company data
        companies = new ArrayList<>();
        companies.add(new Company("Company 1", "Active", "Offer 1"));
        companies.add(new Company("Company 2", "Inactive", "Offer 2"));
        companies.add(new Company("Company 3", "Active", "Offer 3"));
        // Add more companies to the list...

        adapter.setCompanies(companies); // Pass the company data to the adapter

        // Add a calendar button
        binding.calendarButton.setOnClickListener(v -> {
            // Show calendar dialog or activity here
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Adapter for the RecyclerView
    public class PlacementAdapter extends RecyclerView.Adapter<PlacementAdapter.ViewHolder> {
        private List<Company> companies;

        public void setCompanies(List<Company> companies) {
            this.companies = companies;
            notifyDataSetChanged(); // Update the adapter
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.placement_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Company company = companies.get(position);
            holder.companyLogo.setImageResource(R.drawable.company_logo);
            holder.companyName.setText(company.getName());
            holder.status.setText(company.getStatus());
            holder.offer.setText(company.getOffer());
        }

        @Override
        public int getItemCount() {
            return companies.size();
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

                itemView.setOnClickListener(v -> {
                    // Start activity to show company details and description
                    Intent intent = new Intent(getContext(), CompanyDetailsActivity.class);
                    startActivity(intent);
                });
            }
        }
    }

    // Company model class
    public static class Company {
        private String name;
        private String status;
        private String offer;

        public Company(String name, String status, String offer) {
            this.name = name;
            this.status = status;
            this.offer = offer;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }

        public String getOffer() {
            return offer;
        }
    }
}
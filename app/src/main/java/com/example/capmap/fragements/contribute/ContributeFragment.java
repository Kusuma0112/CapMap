package com.example.capmap.fragements.contribute;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.capmap.R;
import com.example.capmap.databinding.FragmentContributeBinding;

public class ContributeFragment extends Fragment {

    private FragmentContributeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContributeViewModel contributeViewModel =
                new ViewModelProvider(this).get(ContributeViewModel.class);

        binding = FragmentContributeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button toAddCompany = binding.ToAddCompany;
        Button toEditCompany = binding.ToEditCompany;
        Button toMarkCalender = binding.ToMarkCalender;
        Button toEditCalender = binding.ToEditCalender;
        Button toGiveNotification = binding.ToGiveNotification;
        Button toEditNotification = binding.ToEditNotification;
        Button myContributions = binding.button;

        // Add click listeners to buttons
        toAddCompany.setOnClickListener(view -> {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_addCompanyFragment);
        });

        toEditCompany.setOnClickListener(view -> {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_editCompanyFragment);
        });

        toMarkCalender.setOnClickListener(view -> {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_markCalenderFragment);
        });

        toEditCalender.setOnClickListener(view -> {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_editCalenderFragment);

        });

        toGiveNotification.setOnClickListener(view -> {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_giveNotificationFragment);

        });

        toEditNotification.setOnClickListener(view -> {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_editNotificationFragment);

        });

        myContributions.setOnClickListener(view -> {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_myContributionsFragment);

        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
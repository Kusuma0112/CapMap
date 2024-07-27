package com.example.capmap.fragements.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.capmap.R;
import com.example.capmap.Usermodel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private ViewPager2 viewPager;
    private EventPagerAdapter eventPagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        ImageView imgHeader = root.findViewById(R.id.imgProfile);
        TextView txtName = root.findViewById(R.id.textView);

        getUserData(imgHeader, txtName);

        // Initialize ViewPager2
        viewPager = root.findViewById(R.id.viewPager);

        List<Integer> eventImages = new ArrayList<>();
        eventImages.add(R.drawable.event1);
        eventImages.add(R.drawable.event2);
        eventImages.add(R.drawable.event3);
        eventImages.add(R.drawable.event4);


        eventPagerAdapter = new EventPagerAdapter(getContext(), eventImages);
        viewPager.setAdapter(eventPagerAdapter);

        return root;
    }

    private void getUserData(ImageView imgHeader, TextView txtName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Usermodel userModel = snapshot.getValue(Usermodel.class);
                    Glide.with(HomeFragment.this).load(userModel.getImage()).into(imgHeader);
                    txtName.setText("Hi " + userModel.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

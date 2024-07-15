package com.example.capmap.fragements.navigate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.capmap.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class NavigateFragment extends Fragment implements OnMapReadyCallback {
    private NavigateViewModel mViewModel;
    private GoogleMap mMap;
    private MapView mapView;
    private Spinner spinnerStart, spinnerEnd;
    private Button buttonShowPath;
    private FusedLocationProviderClient fusedLocationClient;

    public static NavigateFragment newInstance() {
        return new NavigateFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigate, container, false);

        mViewModel = new ViewModelProvider(this).get(NavigateViewModel.class);

        spinnerStart = view.findViewById(R.id.spinnerStart);
        spinnerEnd = view.findViewById(R.id.spinnerEnd);
        buttonShowPath = view.findViewById(R.id.buttonShowPath);
        mapView = view.findViewById(R.id.mapView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize the map
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey");
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        // Populate the spinners with options
        String[] locations = {"Live Location", "Location A", "Location B", "Location C"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, locations);
        spinnerStart.setAdapter(adapter);
        spinnerEnd.setAdapter(adapter);

        // Set the button click listener
        buttonShowPath.setOnClickListener(v -> showPath());

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add default marker to the map
        LatLng defaultLocation = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Default Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
    }

    private void showPath() {
        String start = spinnerStart.getSelectedItem().toString();
        String end = spinnerEnd.getSelectedItem().toString();

        if (start.equals("Live Location") || end.equals("Live Location")) {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        LatLng liveLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        LatLng startLatLng = start.equals("Live Location") ? liveLocation : getLatLngFromLocationName(start);
                        LatLng endLatLng = end.equals("Live Location") ? liveLocation : getLatLngFromLocationName(end);

                        drawPath(startLatLng, endLatLng);
                    } else {
                        Toast.makeText(requireActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            LatLng startLatLng = getLatLngFromLocationName(start);
            LatLng endLatLng = getLatLngFromLocationName(end);

            drawPath(startLatLng, endLatLng);
        }
    }

    private LatLng getLatLngFromLocationName(String locationName) {
        switch (locationName) {
            case "Location A":
                return new LatLng(-34.0, 151.0); // Replace with actual coordinates
            case "Location B":
                return new LatLng(-34.5, 151.5); // Replace with actual coordinates
            case "Location C":
                return new LatLng(-35.0,152.0); // Replace with actual coordinates
            default:
                return new LatLng(-34, 151); // Default location
        }
    }

    private void drawPath(LatLng start, LatLng end) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(start).title("Start"));
        mMap.addMarker(new MarkerOptions().position(end).title("End"));
        mMap.addPolyline(new PolylineOptions().add(start, end));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 15));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle("MapViewBundleKey");
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle("MapViewBundleKey", mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
}
package com.example.emsismartpresence;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Maps extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LatLng currentLocationLatLng;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // EMSI sites with placeholder coordinates (replace with actual coordinates)
    private final Map<String, LatLng> emsiSites = new HashMap<>();
    {
        emsiSites.put("Centre 1", new LatLng(33.5731, -7.5898));
        emsiSites.put("Centre 2", new LatLng(33.5836, -7.6114));
        emsiSites.put("Moulay Youssef", new LatLng(33.5812, -7.6407));
        emsiSites.put("Roudani", new LatLng(33.5667, -7.6231));
        emsiSites.put("Bernoussi", new LatLng(33.6074, -7.5069));
        emsiSites.put("Orangiers", new LatLng(33.5547, -7.5983));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        // Initialize Spinner
        Spinner siteSelector = view.findViewById(R.id.site_selector);
        List<String> siteNames = new ArrayList<>(emsiSites.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, siteNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        siteSelector.setAdapter(adapter);

        // Handle site selection
        siteSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSite = siteNames.get(position);
                LatLng destination = emsiSites.get(selectedSite);
                if (currentLocationLatLng != null && destination != null) {
                    drawRoute(currentLocationLatLng, destination, selectedSite);
                } else {
                    Toast.makeText(getContext(), "Emplacement actuel non disponible", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        // Add markers for EMSI sites
        for (Map.Entry<String, LatLng> entry : emsiSites.entrySet()) {
            mMap.addMarker(new MarkerOptions().position(entry.getValue()).title(entry.getKey()));
        }

        // Move camera to Casablanca (default view)
        LatLng casablanca = new LatLng(33.5731, -7.5898); // Center of Casablanca
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casablanca, 12));

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    currentLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocationLatLng).title("Ma position"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 15));
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void drawRoute(LatLng origin, LatLng destination, String destinationName) {
        String apiKey = "AIzaSyA-l825z_McWfj0Pb_Jf6pmej1QOJ5sa3Q";
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving" +
                "&key=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Erreur lors du calcul de l'itinéraire", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray routes = json.getJSONArray("routes");

                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
                            String duration = leg.getJSONObject("duration").getString("text");
                            String distance = leg.getJSONObject("distance").getString("text");

                            String polyline = route.getJSONObject("overview_polyline").getString("points");
                            List<LatLng> path = PolyUtil.decode(polyline);

                            requireActivity().runOnUiThread(() -> {
                                mMap.clear();
                                // Re-add EMSI site markers
                                for (Map.Entry<String, LatLng> entry : emsiSites.entrySet()) {
                                    mMap.addMarker(new MarkerOptions().position(entry.getValue()).title(entry.getKey()));
                                }
                                // Re-add current location marker
                                if (currentLocationLatLng != null) {
                                    mMap.addMarker(new MarkerOptions().position(currentLocationLatLng).title("Ma position"));
                                }
                                // Add destination marker
                                mMap.addMarker(new MarkerOptions().position(destination).title(destinationName));
                                // Draw route
                                PolylineOptions options = new PolylineOptions()
                                        .addAll(path)
                                        .width(12)
                                        .color(Color.BLUE)
                                        .geodesic(true);
                                mMap.addPolyline(options);
                                // Zoom to fit route
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 14));
                                // Show duration and distance
                                Toast.makeText(getContext(), "Durée: " + duration + " | Distance: " + distance, Toast.LENGTH_LONG).show();
                            });
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Aucun itinéraire trouvé", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Erreur lors du traitement de l'itinéraire", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    startLocationUpdates();
                }
            } else {
                Toast.makeText(getContext(), "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
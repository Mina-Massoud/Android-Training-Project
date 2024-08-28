package com.example.e_commerce.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.e_commerce.Adapter.CartAdapter;
import com.example.e_commerce.Database.MyDatabase;
import com.example.e_commerce.Model.ProductModel;
import com.example.e_commerce.R;
import com.example.e_commerce.activity.CustomDialogClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class Cart extends Fragment implements LocationListener {

    private ListView cart_products;
    private CartAdapter adapter;
    private ArrayList<ProductModel> data = new ArrayList<>();

    private EditText add_address;
    private MyDatabase database;
    private SharedPreferences sharedPreferences;
    private static final int REQUEST_CODE = 100;
    String addr = null;
    FusedLocationProviderClient fusedLocationProviderClient;

    TextView orignal_price, delivery_cost, total_cost;
    Button apply_address, confirm_order;

    double cost = 0;

    int PERMISSION_ID = 44;
    String value = null;
    private Context context;

    public Cart() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cart_products = view.findViewById(R.id.cart_product);
        database = MyDatabase.getInstance(getContext());
        orignal_price = view.findViewById(R.id.order_price);
        delivery_cost = view.findViewById(R.id.delivery_cost);
        total_cost = view.findViewById(R.id.total_cost);
        apply_address = view.findViewById(R.id.apply_address);
        confirm_order = view.findViewById(R.id.confirm_order);
        add_address = view.findViewById(R.id.add_address);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        apply_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getLastLocation();
                    if (addr != null) {
                        Toast.makeText(getContext(), addr, Toast.LENGTH_SHORT).show();
                        add_address.setText(addr);
                    }
                } catch (Exception e) {
                    Log.e("CartFragment", "Error in apply_address click", e);
                    Toast.makeText(getContext(), "An error occurred while applying the address.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        confirm_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (orignal_price.getText().toString().equalsIgnoreCase("0.0" + " $") || orignal_price.getText().toString().equalsIgnoreCase("000"))
                        Toast.makeText(getContext(), "Not confirmed", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(getContext(), "Confirmed", Toast.LENGTH_SHORT).show();
                        CustomDialogClass cdd = new CustomDialogClass(getActivity());
                        cdd.show();
                    }
                } catch (Exception e) {
                    Log.e("CartFragment", "Error in confirm_order click", e);
                    Toast.makeText(getContext(), "An error occurred while confirming the order.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            getProductsids();
        } catch (Exception e) {
            Log.e("CartFragment", "Error in getProductsids", e);
            Toast.makeText(getContext(), "An error occurred while loading products.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void getLastLocation() {
        try {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        try {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                addr = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
                            }
                        } catch (IOException e) {
                            Log.e("CartFragment", "Geocoder failed", e);
                            Toast.makeText(getContext(), "Unable to get address. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                askPermission();
            }
        } catch (SecurityException e) {
            Log.e("CartFragment", "SecurityException: Missing permission for location", e);
            Toast.makeText(getContext(), "Location permission missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void askPermission() {
        try {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } catch (Exception e) {
            Log.e("CartFragment", "Error requesting permissions", e);
            Toast.makeText(getContext(), "An error occurred while requesting permissions.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getProductsids() {
        try {
            sharedPreferences = this.getActivity().getSharedPreferences("cart", Context.MODE_PRIVATE);
            String ids = sharedPreferences.getString("lastorder", null);
            if (ids != null) {
                Gson gson = new Gson();
                ArrayList id = gson.fromJson(ids, ArrayList.class);
                getCartProduct(id);

                adapter = new CartAdapter(getContext(), data);
                adapter.setTotal_cost(cost);
                cart_products.setAdapter(adapter);

                orignal_price.setText(database.getCost(1).getFloat(0) + " $");
                delivery_cost.setText("20.0 $");
                float currentorignal_price = Float.parseFloat(orignal_price.getText().toString().substring(0, orignal_price.getText().toString().length() - 2));
                float currentdelivery_cost = Float.parseFloat(delivery_cost.getText().toString().substring(0, delivery_cost.getText().toString().length() - 2));
                total_cost.setText(currentorignal_price + currentdelivery_cost + " $");
            }
        } catch (Exception e) {
            Log.e("CartFragment", "Error in getProductsids", e);
            Toast.makeText(getContext(), "Error loading products", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCartProduct(ArrayList<Integer> ids) {
        try {
            data.clear();
            for (int i = 0; i < ids.size(); i++) {
                Cursor cursor = database.getProductbyId(String.valueOf(ids.get(i)));

                if (cursor != null && cursor.moveToFirst()) {
                    // Cursor.moveToFirst() returns true if the cursor has at least one row
                    ProductModel productModel = new ProductModel(getContext(),
                            Integer.parseInt(cursor.getString(4)),
                            Integer.parseInt(cursor.getString(5)),
                            cursor.getString(1), cursor.getBlob(2),
                            Double.parseDouble(cursor.getString(3)));
                    productModel.setPro_id(Integer.parseInt(cursor.getString(0)));
                    data.add(productModel);
                    cost += Double.parseDouble(cursor.getString(3));
                } else {
                    // Handle the case where the cursor is empty
                    Log.e("CartFragment", "No product found with id: " + ids.get(i));
                }

                if (cursor != null) {
                    cursor.close(); // Always close the cursor to avoid memory leaks
                }
            }
        } catch (Exception e) {
            Log.e("CartFragment", "Error in getCartProduct", e);
            Toast.makeText(getContext(), "Error loading products", Toast.LENGTH_SHORT).show();
        }
    }


    public void getUserLocation(View view) {
        // Unused method, can add error handling if necessary
    }

    @Override
    public void onLocationChanged(Location location) {
        // Handle location changes if necessary
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // Handle status changes if necessary
    }

    @Override
    public void onProviderEnabled(String s) {
        // Handle provider enabled events if necessary
    }

    @Override
    public void onProviderDisabled(String s) {
        // Handle provider disabled events if necessary
    }

    private boolean checkPermissions() {
        try {
            return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            Log.e("CartFragment", "Error checking permissions", e);
            return false;
        }
    }

    private void requestPermissions() {
        try {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ID
            );
        } catch (Exception e) {
            Log.e("CartFragment", "Error requesting permissions", e);
            Toast.makeText(getContext(), "An error occurred while requesting permissions.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLocationEnabled() {
        try {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
            );
        } catch (Exception e) {
            Log.e("CartFragment", "Error checking if location is enabled", e);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == PERMISSION_ID) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start getting the location information
                }
            }
        } catch (Exception e) {
            Log.e("CartFragment", "Error handling permission result", e);
            Toast.makeText(getContext(), "An error occurred while handling permission result.", Toast.LENGTH_SHORT).show();
        }
    }
}
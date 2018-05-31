package com.railway_services.indian.serviceindustry;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SecondMapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 102;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    DatabaseReference workerDatabaseReference;
    ArrayList<LatLng> workerLatLng;
    ArrayList<String> workerNameList;
    ArrayList<String> workerNumber;
    Button booknow;
    FirebaseAuth mAuth;
    protected LatLng start;
    protected LatLng end;
    DatabaseReference myDatabaseReference;
    DatabaseReference pendingOrderDatabaseReference;
    private List<Polyline> polylines;
    View ComplainDialogueBox;
    protected GoogleApiClient mGoogleApiClient;
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark, R.color.colorAccent, R.color.colorPrimary, R.color.colorAccent, R.color.primary_dark_material_light};
    TimePickerDialog mTimePicker;
    DatePickerDialog mDatePicker;
    EditText customer_clock;
    EditText customer_pincode;
    EditText customer_name;
    EditText customer_address;
    EditText customer_date;
    EditText customer_number;
    EditText customer_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        workerLatLng = new ArrayList<>();
        workerNameList = new ArrayList<>();
        workerNumber = new ArrayList<>();

        polylines = new ArrayList<>();
        start = new LatLng(26.4499, 80.3319);
        end = new LatLng(26.8467, 80.9462);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            pendingOrderDatabaseReference = FirebaseDatabase.getInstance().getReference().child(ConstantUtils.COMPLAINS).child(ConstantUtils.PENDING_COMPLAINS).child("plumber").child("mAuth").push();
            myDatabaseReference = FirebaseDatabase.getInstance().getReference().child(ConstantUtils.COMPLAINS).child(ConstantUtils.MY_COMPLAINS).child("mAuth").push();
        }
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        if (ComplainDialogueBox == null) {
            ComplainDialogueBox = layoutInflater.inflate(R.layout.activity_bookings, null);
        }
        final AlertDialog.Builder placeOrderDialog = new AlertDialog.Builder(this);
        placeOrderDialog.setView(ComplainDialogueBox);
        placeOrderDialog.setIcon(android.R.drawable.ic_menu_info_details);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we place you order");
        customer_clock = ComplainDialogueBox.findViewById(R.id.customer_time);
        customer_date = ComplainDialogueBox.findViewById(R.id.customer_date);
        customer_name = ComplainDialogueBox.findViewById(R.id.customer_name);
        customer_number = ComplainDialogueBox.findViewById(R.id.customer_mobile);
        customer_description = ComplainDialogueBox.findViewById(R.id.customer_complain_desc);
        customer_pincode = ComplainDialogueBox.findViewById(R.id.customer_pin);
        customer_address = ComplainDialogueBox.findViewById(R.id.customer_address);
        customer_pincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked Mee", Toast.LENGTH_LONG).show();
            }
        });
        final Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);

        customer_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked me", Toast.LENGTH_LONG).show();

                TimePickerDialog mTimePicker = new TimePickerDialog(SecondMapsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        customer_clock.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        customer_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePicker = new DatePickerDialog(SecondMapsActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        customer_date.setText(i + "/" + i1 + "/" + i2);
                    }
                }, mcurrentTime.get(Calendar.YEAR),
                        mcurrentTime.get(Calendar.MONTH),
                        mcurrentTime.get(Calendar.DAY_OF_MONTH));

                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });

        placeOrderDialog.setPositiveButton("Place Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialog.show();
                if (mAuth.getCurrentUser() == null) {
                    if (!TextUtils.isEmpty(customer_address.getText()) || !TextUtils.isEmpty(customer_name.getText().toString())
                            || !TextUtils.isEmpty(customer_number.getText()) || !TextUtils.isEmpty(customer_pincode.getText()) ||
                            !TextUtils.isEmpty(customer_clock.getText()) || !TextUtils.isEmpty(customer_date.getText()) || !TextUtils.isEmpty(customer_description.getText())) {

                        final Map<String, String> myComplains = new HashMap<>();
                        myComplains.put("Address", customer_address.getText().toString());
                        myComplains.put("mobile", customer_number.getText().toString());
                        myComplains.put("Name", customer_name.getText().toString());
                        myComplains.put("Date", customer_date.getText().toString());
                        myComplains.put("Time", customer_clock.getText().toString());
                        myComplains.put("Work", customer_description.getText().toString());
                        myComplains.put("pincode", customer_pincode.getText().toString());

                        if (ActivityCompat.checkSelfPermission(SecondMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SecondMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        myComplains.put("lat", Double.toString(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude()));
                        myComplains.put("lng", Double.toString(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()));
                        myDatabaseReference.setValue(myComplains).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    pendingOrderDatabaseReference.setValue(myComplains).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                                Intent intent = new Intent();
                                                Bundle Bundle = new Bundle();
                                                Bundle.putString("name", customer_name.getText().toString());
                                                Bundle.putString("address", customer_address.getText().toString());
                                                Bundle.putString("mobile", customer_number.getText().toString());
                                                Bundle.putString("date", customer_date.getText().toString());
                                                Bundle.putString("time", customer_clock.getText().toString());
                                                Bundle.putString("work", customer_description.getText().toString());
                                                intent.putExtras(Bundle);
                                                startActivity(intent);

                                            }
                                        }
                                    });

                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Fields should not be empty", Toast.LENGTH_LONG).show();
                    }


                } else {
                    startActivity(new Intent(SecondMapsActivity.this, LoginActivity.class));
                }

            }
        });


        placeOrderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        booknow = findViewById(R.id.book_now);

        //order id should be generated must be unique


        booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrderDialog.show();

            }
        });


        getWorker();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("TAG", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(16, 16, 32, 16);
        getLocationAccess();
        StartRoute(new LatLng(26.4499, 80.3319), new LatLng(26.8467, 80.9462));
        //mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
    }

    public void getLocationAccess() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //mMap.clear();
                Log.i("Location", location.toString());
                Marker m1 = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .anchor(0.5f, 0.5f)
                        .title("You are Here")
                        .snippet("Address"));
                //     .icon(BitmapDescriptorFactory.fromResource(R.drawable.fan)));
                // mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                //Enabled or disabled
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (Build.VERSION.SDK_INT > 23) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30 * 60 * 1000, 100, locationListener);

            }
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30 * 60 * 1000, 100, locationListener);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30 * 60 * 1000, 100, locationListener);

        }
    }

    void getWorker() {
        workerDatabaseReference = FirebaseDatabase.getInstance().getReference().child("workers").child("plumbers");
        workerDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();

                if (dataSnapshot != null) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Worker worker = dataSnapshot1.getValue(Worker.class);
                        LatLng latLng = new LatLng(Double.parseDouble(worker.getLocation().get("lat")), Double.parseDouble(worker.getLocation().get("lng")));

                        workerLatLng.add(latLng);
                        workerNameList.add(worker.getName());
                        workerNumber.add(worker.getProfile());
                        Log.i("WorkerInFo", dataSnapshot.toString());
                    }

                    setMarker(workerLatLng, workerNameList, workerNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setMarker(ArrayList<LatLng> workersLatLng, ArrayList<String> workerNameList, ArrayList<String> workerNumber) {

        int i = 0;
        for (LatLng latLng : workersLatLng) {
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .anchor(0.5f, 0.5f)
                    .title(workerNameList.get(i))
                    .snippet(workerNumber.get(i))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.plumber_men)));
            ++i;

        }
        mMap.setInfoWindowAdapter(new CustomWIndowAdapter(SecondMapsActivity.this, workerNumber));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        // progressDialog.dismiss();
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }


    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        //   progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() / 1000 + "KM." + ": duration - " + route.get(i).getDurationValue() / 3600 + "Hrs", Toast.LENGTH_LONG).show();
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        mMap.addMarker(options);

    }

    @Override
    public void onRoutingCancelled() {

        Log.i("TAG", "Routing was cancelled.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.v("TAG", connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    void StartRoute(LatLng start, LatLng end) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(start, end)
                .build();
        routing.execute();
    }
}

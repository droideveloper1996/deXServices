package com.railway_services.indian.serviceindustry;

import android.*;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Build;

import com.appnext.nativeads.MediaView;
import com.appnext.nativeads.NativeAdView;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.app.SearchManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.appnext.base.Appnext;
import com.appnext.core.AppnextError;
import com.appnext.nativeads.NativeAd;
import com.appnext.nativeads.NativeAdListener;
import com.appnext.nativeads.NativeAdRequest;
import com.appnext.nativeads.PrivacyIcon;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.railway_services.indian.serviceindustry.CarouselAdapter;
import com.railway_services.indian.serviceindustry.CategoryRecyclerAdapter;
import com.railway_services.indian.serviceindustry.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements CarouselAdapter.ServiceOnClickInterface, CategoryRecyclerAdapter.CategoryItemClickListner {
    ViewPager viewPager;
    AutoCompleteTextView searchBarEditText;
    DatabaseReference databaseReference;
    ArrayList<String> name;
    ArrayList<String> classes;
    ArrayAdapter<String> searchAdapter;
    ArrayList<String> sliderImages;
    private LocationManager locationManager;
    private LocationListener locationListener;
    FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    NativeAdView nativeAdView;
    ImageView imageView;
    TextView textView;
    MediaView mediaView;
    ProgressBar progressBar;
    Button button;
    NativeAd nativeAd;
    TextView rating;
    TextView description;
    ArrayList<View> viewArrayList;
    CoordinatorLayout coordinatorLayout;
    RecyclerView adsRecyclerView;
    RecyclerView adsRecyclerView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("student");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        //bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);

        adsRecyclerView = findViewById(R.id.adsRecyclerView);
        adsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager adsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adsRecyclerView.setLayoutManager(adsLayoutManager);
        AdsRecyclerAdapter adsRecyclerAdapter = new AdsRecyclerAdapter(this);
        adsRecyclerView.setAdapter(adsRecyclerAdapter);

        adsRecyclerView2 = findViewById(R.id.adsRecyclerView2);
        adsRecyclerView2.setHasFixedSize(true);
        LinearLayoutManager adsLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adsRecyclerView2.setLayoutManager(adsLayoutManager2);
        AdsRecyclerAdapter adsRecyclerAdapter2 = new AdsRecyclerAdapter(this);
        adsRecyclerView2.setAdapter(adsRecyclerAdapter2);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, (R.string.True), R.string.False);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        searchBarEditText = findViewById(R.id.searchBar);
        name = new ArrayList<>();
        classes = new ArrayList<>();
        sliderImages = new ArrayList<>();
        new BackgroundActivity().execute("");
        //setupSlider();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("TextChanged", charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(editable.toString())) {
                    searchDatabase(editable.toString());
                }

            }
        });
        setViews();
        hideSoftKeyboard();
        getSubscriptionServices();
        getRepairServices();
        getBeautyServices();
        getPackersnMovers();
        getPersonalServices();
        getHomeNeeds();
        setupCategoryAdapter();
        //   locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocationAccess();
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Welcome to deX Services", Snackbar.LENGTH_LONG);

        snackbar.show();
       /* Appnext.init(this);

        nativeAd = new NativeAd(this, ConstantUtils.APPNEXT_PLACEMENT_ID);
        nativeAd.setPrivacyPolicyColor(PrivacyIcon.PP_ICON_COLOR_DARK);
        nativeAd.setPrivacyPolicyPosition(PrivacyIcon.PP_ICON_POSITION_BOTTOM_LEFT);
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(final NativeAd nativeAd) {
                super.onAdLoaded(nativeAd);
                progressBar.setVisibility(View.GONE);
                nativeAd.downloadAndDisplayImage(imageView, nativeAd.getIconURL());
                textView.setText(nativeAd.getAdTitle());
                nativeAd.setMediaView(mediaView);
                rating.setText(nativeAd.getStoreRating());
                description.setText(nativeAd.getAdDescription());
                nativeAd.registerClickableViews(viewArrayList);
                nativeAd.setNativeAdView(nativeAdView);
            }

            @Override
            public void onAdClicked(NativeAd nativeAd) {
                super.onAdClicked(nativeAd);
            }

            @Override
            public void onError(NativeAd nativeAd, AppnextError appnextError) {
                super.onError(nativeAd, appnextError);
                Toast.makeText(getApplicationContext(), "Error loading ads", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void adImpression(NativeAd nativeAd) {
                super.adImpression(nativeAd);
            }
        });


        nativeAd.loadAd(new NativeAdRequest()
                // optional - config your ad request:
                .setPostback("")
                .setCategories("")
                .setCachingPolicy(NativeAdRequest.CachingPolicy.ALL)
                .setCreativeType(NativeAdRequest.CreativeType.ALL)
                .setVideoLength(NativeAdRequest.VideoLength.SHORT)
                .setVideoQuality(NativeAdRequest.VideoQuality.HIGH)
        );
*/
    }

    private void setViews() {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.app_next_native_ads, null);
        nativeAdView = (NativeAdView) findViewById(R.id.na_view);
        imageView = (ImageView) findViewById(R.id.na_icon);
        textView = (TextView) findViewById(R.id.na_title);
        mediaView = (MediaView) findViewById(R.id.na_media);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        button = (Button) findViewById(R.id.install);
        rating = (TextView) findViewById(R.id.rating);
        description = (TextView) findViewById(R.id.description);
        viewArrayList = new ArrayList<>();
        viewArrayList.add(button);
        viewArrayList.add(mediaView);
    }

    private void searchDatabase(final CharSequence s) {
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classes.clear();
                name.clear();
                if (dataSnapshot != null) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String nameUnit = dataSnapshot1.child("name").getValue(String.class);
                        String classess = dataSnapshot1.child("class").getValue(String.class);

                        if (nameUnit.toLowerCase().contains(s.toString().toLowerCase())) {
                            classes.add(classess);
                            name.add(nameUnit);
                        } else if (classess.toLowerCase().contains(s.toString().toLowerCase())) {
                            name.add(nameUnit);
                            classes.add(classess);
                        }
                    }
                }

                for (String names : name) {
                    Log.d("SearchResult", names);
                }
                for (String clases : classes) {
                    Log.d("SearchResult", clases);
                }
                searchAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, name);
                searchBarEditText.setAdapter(searchAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void click(int position, int params) {

        switch (params) {


        }

    }

    @Override
    public void onCategoryClick(int position) {

        // Toast.makeText(getApplicationContext(), "Clicked ", Toast.LENGTH_LONG).show();
        startActivity(new Intent(MainActivity.this, SecondMapsActivity.class));


    }

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(1);
                    } else if (viewPager.getCurrentItem() == 1) {
                        viewPager.setCurrentItem(2);
                    } else if (viewPager.getCurrentItem() == 2) {
                        viewPager.setCurrentItem(3);
                    } else if (viewPager.getCurrentItem() == 3) {
                        viewPager.setCurrentItem(4);
                    } else if (viewPager.getCurrentItem() == 4) {
                        viewPager.setCurrentItem(5);
                    } else if (viewPager.getCurrentItem() == 5) {
                        viewPager.setCurrentItem(6);
                    } else if (viewPager.getCurrentItem() == 6) {
                        viewPager.setCurrentItem(7);
                    } else if (viewPager.getCurrentItem() == 7) {
                        viewPager.setCurrentItem(8);
                    } else if (viewPager.getCurrentItem() == 8) {
                        viewPager.setCurrentItem(0);
                    }


                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
       /* SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.i("Search Begin",newText);
                return true;
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    void setupSlider() {
        DatabaseReference sliderDatabase = FirebaseDatabase.getInstance().getReference().child("sliderImages");
        sliderDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        sliderImages.add(dataSnapshot1.getValue(String.class));

                    }
                    viewPager = findViewById(R.id.viewPager);
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(MainActivity.this, sliderImages);
                    viewPager.setAdapter(viewPagerAdapter);
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new MyTimerTask(), 2500, 6000);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getSubscriptionServices() {
        final ArrayList<ServiceClass> subscriptionArrayList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(ConstantUtils.SUBSCRIPTIONS);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    subscriptionArrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            Log.d("Subs", dataSnapshot1.toString());
                            ServiceClass serviceClass = dataSnapshot2.getValue(ServiceClass.class);
                            subscriptionArrayList.add(serviceClass);
                        }
                    }

                    RecyclerView carouselRecyclerView = findViewById(R.id.servicesRecyclerView);
                    carouselRecyclerView.setHasFixedSize(true);
                    CarouselAdapter carouselAdapter = new CarouselAdapter(MainActivity.this, subscriptionArrayList, MainActivity.this, ConstantUtils.SUBSCRIPTION_HEAD);
                    carouselRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    carouselRecyclerView.setAdapter(carouselAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void getRepairServices() {
        final ArrayList<ServiceClass> subscriptionArrayList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(ConstantUtils.REPAIR);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    subscriptionArrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            Log.d("Subs", dataSnapshot1.toString());
                            ServiceClass serviceClass = dataSnapshot2.getValue(ServiceClass.class);
                            subscriptionArrayList.add(serviceClass);
                        }
                    }

                    RecyclerView repaircarouselRecyclerView = findViewById(R.id.repairServicesRecyclerView);
                    repaircarouselRecyclerView.setHasFixedSize(true);
                    CarouselAdapter repaircarouselAdapter = new CarouselAdapter(MainActivity.this, subscriptionArrayList, MainActivity.this, ConstantUtils.REPAIR_HEAD);
                    repaircarouselRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    repaircarouselRecyclerView.setAdapter(repaircarouselAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getBeautyServices() {

        final ArrayList<ServiceClass> beautysubscriptionArrayList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(ConstantUtils.PERSONAL_SERVICES).child(ConstantUtils.BEAUTY);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    beautysubscriptionArrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                        ServiceClass serviceClass = dataSnapshot1.getValue(ServiceClass.class);
                        beautysubscriptionArrayList.add(serviceClass);


                    }

                    RecyclerView beautyRecyclerView = findViewById(R.id.beautyServicesRecyclerView);
                    beautyRecyclerView.setHasFixedSize(true);
                    CarouselAdapter beautycarouselAdapter = new CarouselAdapter(MainActivity.this, beautysubscriptionArrayList, MainActivity.this, ConstantUtils.BEAUTY_HEAD);
                    beautyRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    beautyRecyclerView.setAdapter(beautycarouselAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getPackersnMovers() {

        final ArrayList<ServiceClass> subscriptionArrayList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(ConstantUtils.PERSONAL_SERVICES).child(ConstantUtils.CATEGORY_PACKERS_MOVERS_SERVICE);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    subscriptionArrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            Log.d("Subs", dataSnapshot1.toString());
                            ServiceClass serviceClass = dataSnapshot2.getValue(ServiceClass.class);
                            subscriptionArrayList.add(serviceClass);
                        }
                    }

                    RecyclerView moversPackersRecyclerView = findViewById(R.id.packersRecyclerView);
                    moversPackersRecyclerView.setHasFixedSize(true);
                    CarouselAdapter packerscarouselAdapter = new CarouselAdapter(MainActivity.this, subscriptionArrayList, MainActivity.this, ConstantUtils.PERSONAL_SERVICE);
                    moversPackersRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    moversPackersRecyclerView.setAdapter(packerscarouselAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getPersonalServices() {
        final ArrayList<ServiceClass> subscriptionArrayList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(ConstantUtils.PERSONAL_SERVICES);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    subscriptionArrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            Log.d("Subs", dataSnapshot1.toString());
                            ServiceClass serviceClass = dataSnapshot2.getValue(ServiceClass.class);
                            subscriptionArrayList.add(serviceClass);
                        }
                    }

                    RecyclerView personalServiceRecyclerView = findViewById(R.id.personalServiceRecyclerView);
                    personalServiceRecyclerView.setHasFixedSize(true);
                    CarouselAdapter personalServiceAdapter = new CarouselAdapter(MainActivity.this, subscriptionArrayList, MainActivity.this, ConstantUtils.PERSONAL_SERVICE);
                    personalServiceRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    personalServiceRecyclerView.setAdapter(personalServiceAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void getHomeNeeds() {
        final ArrayList<ServiceClass> subscriptionArrayList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(ConstantUtils.HOME_NEEDS);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    subscriptionArrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            Log.d("Subs", dataSnapshot1.toString());
                            ServiceClass serviceClass = dataSnapshot2.getValue(ServiceClass.class);
                            subscriptionArrayList.add(serviceClass);
                        }
                    }

                    RecyclerView homeNeedRecyclerView = findViewById(R.id.homeNeedsRecyclerView);
                    homeNeedRecyclerView.setHasFixedSize(true);
                    CarouselAdapter homeNeedAdapter = new CarouselAdapter(MainActivity.this, subscriptionArrayList, MainActivity.this, ConstantUtils.HOME_SERVICES);
                    homeNeedRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    homeNeedRecyclerView.setAdapter(homeNeedAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    void setupCategoryAdapter() {
        int[] service = {
                R.drawable.air_conditioner,
                R.drawable.make_up,
                R.drawable.ladybug,
                R.drawable.flower,
                R.drawable.delivery,
                R.drawable.plumber,
                R.drawable.furniture,
                R.drawable.plug,
                R.drawable.paint_brush,
                R.drawable.computer,
                R.drawable.car,
                R.drawable.hospital,
                R.drawable.iron,
                R.drawable.mortarboard,
                R.drawable.streeing,
                R.drawable.security_camera,
                R.drawable.laundry,
                R.drawable.hairdryer,
                R.drawable.fan,
                R.drawable.leak,
                R.drawable.water_heater,
                R.drawable.powder
        };


        String serviceName[] = {"AirConditioner", "Make up", "Pest Control"
                , "Gardening", "Packers & Movers", "Plumber", "Carpenter", "Electrician"
                , "Painter", "Computer Service", "Car Services", "Medical", "Ironing",
                "Home Tutor", "Driving School", "Internet Of Things", "Laundry",
                "Saloon", "Cooler Repair", "Plumber", "Geyser", "Puja/Pandit"};

        String carouselImage[] = {"https://bbmlive.com/wp-content/uploads/2015/05/Carpenter.jpg"
                , "https://cdn.websites.hibu.com/2c80e0994f7f483dbc374e2ad384fe51/dms3rep/multi/tablet/plumbing2-780x350.jpg"};

        RecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        CategoryRecyclerAdapter categoryRecyclerAdapter = new CategoryRecyclerAdapter(this, service, serviceName, this);
        categoryRecyclerView.setAdapter(categoryRecyclerAdapter);

    }

    public void getLocationAccess() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("Location", location.toString());

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    {
                        if (addresses != null && addresses.size() > 0) {

                            SharedPreferences sharedpreferences = getSharedPreferences(ConstantUtils.MY_LOCATION, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            try {
                                Log.d("MY LOCATION", addresses.get(0).getAdminArea());
                                editor.putString("MY_LOCATION", addresses.get(0).getAdminArea());
                                editor.commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 * 24, 0, locationListener);

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

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }


    class BackgroundActivity extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... strings) {
            setupSlider();
            return null;
        }
    }
}


package com.example.hfilproject;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    //Play Service Location
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICE_RESULATION_REQUEST = 300193;
    LatLng dangerous_area;
    private Location mLastLocaiton;
    private static int UPDATE_INTERVAL = 60*3000;
    private static int FATEST_INTERVAL = 60*1500;
    private static int DISPLACEMENT = 10;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    Marker mCurrent;
    VerticalSeekBar mVerticalSeekBar;
    private static final String TAG = MapsActivity.class.getSimpleName();

    private GeoService geoService;
    private boolean serviceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        sharedPrefs = getSharedPreferences("app", MODE_PRIVATE);
        editor = sharedPrefs.edit();


        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.verticalSeekBar);
        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(progress), 1500, null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayService()) {
                        geoService.buildGoogleApiClient();
                        geoService.createLocationRequest();
                        geoService.displayLocation();
                        geoService.setLocationChangeListener(new GeoService.LocationChangeListener() {
                            @Override
                            public void onLocationChange(Location location) {
                                if (mCurrent != null)
                                    mCurrent.remove();
                                mCurrent = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .title("You"));
                                LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 17.77f);
                                mMap.animateCamera(yourLocation);
                            }
                        });
                    }
                }
                break;
        }
    }

    private void setUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayService()) {
                geoService.buildGoogleApiClient();
                geoService.createLocationRequest();
                geoService.displayLocation();
                geoService.setLocationChangeListener(new GeoService.LocationChangeListener() {
                    @Override
                    public void onLocationChange(Location location) {
                        if (mCurrent != null)
                            mCurrent.remove();
                        mCurrent = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .title("You"));
                        LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 17.7f);
                        mMap.animateCamera(yourLocation);
                    }
                });
            }
        }
    }


    private boolean checkPlayService() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, PLAY_SERVICE_RESULATION_REQUEST).show();
            } else {
                Toast.makeText(this, "This Device is not supported.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(MapsActivity.this,sharedPrefs.getFloat("lat",0.0f)+"",Toast.LENGTH_LONG).show();

        if(sharedPrefs.getFloat("lat",0.0f)!=0) {
            if (sharedPrefs.getBoolean("Home Quarantine", false) == true) {
                if (sharedPrefs.getBoolean("done", false) == true) {
                    dangerous_area = new LatLng(sharedPrefs.getFloat("lat", 0.0f), sharedPrefs.getFloat("long", 0.0f));
                    startGeofence();
                } else {
                    Toast.makeText(MapsActivity.this, "Please reach your destination first", Toast.LENGTH_LONG).show();
                }
            } else {
                dangerous_area = new LatLng(sharedPrefs.getFloat("lat", 0.0f), sharedPrefs.getFloat("long", 0.0f));
                startGeofence();
            }
        }
        else
        {
            Toast.makeText(MapsActivity.this, "It looks like you uninstalled our app .Please go back and click on update location . ", Toast.LENGTH_LONG).show();
        }


    }

    private void startGeofence() {
        mMap.addCircle(new CircleOptions()
                .center(dangerous_area)
                .radius(40)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f));

        geoService.startService(dangerous_area,40);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Starting and binding service");
        }
        Intent i = new Intent(this, GeoService.class);
        startService(i);
        bindService(i, mConnection, 0);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            // If a timer is active, foreground the service, otherwise kill the service
            if (geoService.isServiceRunning()) {
                geoService.foreground();
            } else {
                stopService(new Intent(this, GeoService.class));
            }
            // Unbind the service
            unbindService(mConnection);
            serviceBound = false;
        }
    }

    /**
     * Callback for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Service bound");
            }
            GeoService.RunServiceBinder binder = (GeoService.RunServiceBinder) service;
            geoService = binder.getService();
            serviceBound = true;
            // Ensure the service is not in the foreground when bound
            geoService.background();
            setUpdateLocation();
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Service disconnect");
            }
            serviceBound = false;
        }
    };


    public static class GeoService extends Service implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            LocationListener {
        private LocationRequest mLocationRequest;
        private GoogleApiClient mGoogleApiClient;
        private Location mLastLocation;
        private DatabaseReference ref;
        private GeoFire geoFire;
        private LocationChangeListener mLocationChangeListener;
        private static final String TAG = GeoService.class.getSimpleName();
        Retrofit retrofit;
        String token1;
        String sendTokenBle;

        SharedPreferences sharedPrefs;
        SharedPreferences.Editor editor;
        // Is the service tracking time?
        private boolean isServiceRunning;

        // Foreground notification id
        private static final int NOTIFICATION_ID = 1001;

        // Service binder
        private final IBinder serviceBinder = new RunServiceBinder();
        private GeoQuery geoQuery;

        private NotificationManager notificationManager;
        private static final String CHANNEL_ID = "geofencing_channel";

        public class RunServiceBinder extends Binder {
            GeoService getService() {
                return GeoService.this;
            }
        }

        @Override
        public void onCreate() {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Creating service");
            }
            ref = FirebaseDatabase.getInstance().getReference("MyLocation");
            geoFire = new GeoFire(ref);
            isServiceRunning = false;
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,getString(R.string.app_name),NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Starting service");
            }
            return Service.START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Binding service");
            }
            return serviceBinder;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Destroying service");
            }
        }

        /**
         * Starts the timer
         */
        public void startService(LatLng latLng, double radius) {
            if (!isServiceRunning) {
                isServiceRunning = true;

            } else {
                Log.e(TAG, "startService request for an already running Service");

            }
            if (geoQuery!=null){
                geoQuery.removeAllListeners();
            }
            geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.04f);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    sendNotification("Alert", String.format("%s  are inside your geofence ", key));
                }

                @Override
                public void onKeyExited(String key) {
                    sendNotification("Alert", String.format("%s  exited your geofence", key));

                    sendToServer();

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    Log.d("MOVE", String.format("%s move within the dangerous area [%f/%f]", key, location.latitude, location.longitude));
                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    Log.d("ERROR", "" + error);
                }
            });
        }

        private void sendToServer() {
            int categoryType = 0;
            sharedPrefs = getSharedPreferences("app", Context.MODE_PRIVATE);
            editor = sharedPrefs.edit();

            token1 = sharedPrefs.getString("token", "");


            String geofenceStatus = "Geo fence breached.";
            OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpbuilder.addInterceptor(logging);

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                    .addConverterFactory(GsonConverterFactory.create());

            retrofit = builder.build();
            for_login login = retrofit.create(for_login.class);
            Map<String, Object> params = new HashMap<>();
            params.put("notification", geofenceStatus);
            params.put("category", categoryType);
            Call<UserNotification> call = login.userNotify(token1, params);
            call.enqueue(new Callback<UserNotification>() {
                @Override
                public void onResponse(Call<UserNotification> call, Response<UserNotification> response) {
                    String error;
                    if (response.isSuccessful() && response.code() == 200) {
                        if (response.body().getErrorCode() != null) {
                            error = response.body().getErrorCode();
                            if (error.equals("2")) {
                                Toast.makeText(GeoService.this, "User not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        sendTokenBle = response.body().getToken();
                        Toast.makeText(GeoService.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserNotification> call, Throwable t) {
                    Toast.makeText(GeoService.this, "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GeofenceTransition", "" + t.getMessage());
                }
            });
        }


        /**
         * Stops the timer
         */
        public void stopService() {
            if (isServiceRunning) {
                isServiceRunning = false;
                geoQuery.removeAllListeners();
            } else {
                Log.e(TAG, "stopTimer request for a timer that isn't running");
            }
        }

        /**
         * @return whether the service is running
         */
        public boolean isServiceRunning() {
            return isServiceRunning;
        }


        /**
         * Place the service into the foreground
         */
        public void foreground()
        {
            startForeground(NOTIFICATION_ID, createNotification());

        }

        /**
         * Return the service to the background
         */
        public void background() {
            stopForeground(true);
        }

        /**
         * Creates a notification for placing the service into the foreground
         *
         * @return a notification for interacting with the service when in the foreground
         */
        private Notification createNotification() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle("Geofence is active")
                    .setContentText("Tap to return to the Map")
                    .setSmallIcon(R.mipmap.sqlogo);

            Intent resultIntent = new Intent(this, MapsActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(this, 0, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            return builder.build();
        }

        private void sendNotification(String title, String content) {
            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.sqlogo)
                    .setContentTitle(title)
                    .setContentText(content);

            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = new Intent(this, MapsActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(contentIntent);
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            manager.notify(new Random().nextInt(), notification);
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            displayLocation();
            startLocationUpdate();
        }

        private void startLocationUpdate() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            displayLocation();
        }

        interface LocationChangeListener {
            void onLocationChange(Location location);
        }

        private void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        }

        private void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
            mGoogleApiClient.connect();
        }

        private void displayLocation() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                final double latitude = mLastLocation.getLatitude();
                final double longitude = mLastLocation.getLongitude();

                geoFire.setLocation("You", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (mLocationChangeListener!=null) {
                            mLocationChangeListener.onLocationChange(mLastLocation);
                        }
                    }
                });

                Log.d("MRF", String.format("Your last location was chaged: %f / %f", latitude, longitude));
            } else {
                Log.d("MRF", "Can not get your location.");
            }
        }

        public void setLocationChangeListener(LocationChangeListener mLocationChangeListener) {
            this.mLocationChangeListener = mLocationChangeListener;
        }
    }

}
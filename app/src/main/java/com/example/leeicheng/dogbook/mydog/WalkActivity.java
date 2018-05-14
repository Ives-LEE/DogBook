package com.example.leeicheng.dogbook.mydog;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.articles.Article;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.CommonRemote;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class WalkActivity extends AppCompatActivity {
    TextView meterNumber;
    ImageView ivLeftToolbar;
    float result;
    GeneralTask generalTask;
    private final static int REQUEST_CODE_RESOLUTION = 1;
    GoogleApiClient googleApiClient;
    Location startLocation, lastLocation;

    String TAG = "GOOGLE";
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
            updateLastLocationInfo(location);
            result = updateMeter();

            meterNumber.setText(String.format("%.1f", result));
        }
    };

    GoogleApiClient.ConnectionCallbacks connectionCallbacks  = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(WalkActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                lastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(googleApiClient);

                startLocation = lastLocation;

                LocationRequest locationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10000)
                        .setSmallestDisplacement(100);
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient, locationRequest, locationListener);

            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            showToast(R.string.msg_GoogleApiClientConnectionSuspended);
        }
    };

    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener =
            new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult result) {
                    showToast(R.string.msg_GoogleApiClientConnectionFailed);
                    if (!result.hasResolution()) {
                        GoogleApiAvailability.getInstance().getErrorDialog(
                                WalkActivity.this,
                                result.getErrorCode(),
                                0
                        ).show();
                        return;
                    }
                    try {
                        result.startResolutionForResult(
                                WalkActivity.this,
                                REQUEST_CODE_RESOLUTION);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Exception while starting resolution activity");
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydog_walk_activity);
        meterNumber = findViewById(R.id.tvMeterNumber);
        ivLeftToolbar = findViewById(R.id.ivLeftToolbarWalk);

        ivLeftToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMeter(result);
                Log.d("我的位",String.format("%.1f", result));
                finish();
            }
        });
    }

    private boolean lastLocationFound() {
        if (lastLocation == null) {
            showToast(R.string.msg_LocationNotAvailable);
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();
    }

    @Override
    protected void onPause() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (connectionCallbacks != null){
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(connectionCallbacks)
                        .addOnConnectionFailedListener(onConnectionFailedListener)
                        .build();
            }
            googleApiClient.connect();
        }
    }

    @Override
    protected void onDestroy() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onDestroy();
    }

    float updateMeter() {
        if (!lastLocationFound()) {
            return -1;
        }
        float[] results = new float[1];

        if (startLocation != null && lastLocation != null) {
            Location.distanceBetween(startLocation.getLatitude(),
                    startLocation.getLongitude(), lastLocation.getLatitude(),
                    lastLocation.getLongitude(), results);
        }

        return results[0];
    }

    private void updateLastLocationInfo(Location lastLocation) {
        String message = "";
        message += "The Information of the Last Location \n";

        if (lastLocation == null) {
            showToast(R.string.msg_LastLocationNotAvailable);
            return;
        }

        Date date = new Date(lastLocation.getTime());
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String time = dateFormat.format(date);
        message += "fix time: " + time + "\n";

        message += "latitude: " + lastLocation.getLatitude() + "\n";
        message += "longitude: " + lastLocation.getLongitude() + "\n";
        message += "accuracy (meters): " + lastLocation.getAccuracy() + "\n";
        message += "altitude (meters): " + lastLocation.getAltitude() + "\n";
        message += "bearing (horizontal direction- in degrees): "
                + lastLocation.getBearing() + "\n";
        message += "speed (meters/second): " + lastLocation.getSpeed() + "\n";
        Log.d("我的位置", ""+message);
    }

    private void showToast(int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private static final int REQ_PERMISSIONS = 0;

    // New Permission see Appendix A
    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    REQ_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSIONS:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        String text = getString(R.string.text_ShouldGrant);
                        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                break;
        }
    }

    void sendMeter(float meter) {
        if (Common.isNetworkConnect(this)) {
            int dogId =Common.getPreferencesDogId(this);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            String url = Common.URL + "/DogServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.ADD_METER);
            jsonObject.addProperty("dogId", dogId);
            jsonObject.addProperty("meter", meter);

            generalTask = new GeneralTask(url, jsonObject.toString());
            try {
                generalTask.execute().get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }




}

package com.example.leeicheng.dogbook.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.activities.task.ImageTask;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActivityDetailFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "Detail is googlemap";
    private GoogleMap map;
    private Activity activity;
    private ImageTask activityImageTask;
    private FragmentActivity frgactivity;
    private double Distance;
    private LocationManager mLocationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frgactivity = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (Activity) (getArguments() != null ? getArguments().getSerializable("activity") : null);
        return inflater.inflate(R.layout.fragment_activity_detail, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /* the SupportMapFragment is a child fragment of ActivityDetailFragment;
        using getChildFragmentManager() instead of getFragmentManager() */
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().
                        findFragmentById(R.id.fmMap);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {

        this.map = map;
        if (ActivityCompat.checkSelfPermission(frgactivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        map.getUiSettings().setZoomControlsEnabled(true);
        if (activity == null) {
            Common.showToast(frgactivity, R.string.msg_NoSpotsFound);
        } else {
            showMap(activity);
        }
    }

    private void showMap(Activity activity) {
        LatLng position = new LatLng(activity.getLocation_latitude(), activity.getLocation_longitude());
        String snippet = "Name " + ":" + activity.getName() + "\n" +
                "\n" +  "Address" + ": " + activity.getLocation_address();
        mLocationManager = (LocationManager) this.frgactivity.getSystemService(Context.LOCATION_SERVICE);


        // focus on the activity
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(14)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);

        // ic_add activity on the map
        map.addMarker(new MarkerOptions()
                .position(position)
                .title(activity.getName())
                .snippet(snippet));



        map.setInfoWindowAdapter(new MyInfoWindowAdapter(this.frgactivity, activity));
    }

    public void setDistance(double distance){

        Distance = distance;
    }

    //取的店家距離
    public double getDistance(){

        return Distance;
    }

    private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View infoWindow;
        private Activity activity;
        private int imageSize;


        MyInfoWindowAdapter(Context context, Activity activity) {
            infoWindow = View.inflate(context, R.layout.infowindow_activity_detail, null);
            this.activity = activity;
            imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        }


        @Override
        public View getInfoWindow(Marker marker) {
            ImageView imageView = infoWindow.findViewById(R.id.imageView);
            String url = Common.URL + "/ActivitiesServlet";
            int id = activity.getId();
            Bitmap bitmap = null;
            try {
                activityImageTask = new ImageTask(url, id, imageSize);
                // passing null and calling get() means not to run FindImageByIdTask.onPostExecute()
                bitmap = activityImageTask.execute().get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.default_image);
            }
            TextView tvTitle = infoWindow.findViewById(R.id.tvTitle);
            tvTitle.setText(marker.getTitle());

            TextView tvSnippet = infoWindow.findViewById(R.id.tvSnippet);
            tvSnippet.setText(marker.getSnippet());
            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (activityImageTask != null) {
            activityImageTask.cancel(true);
        }
    }

}

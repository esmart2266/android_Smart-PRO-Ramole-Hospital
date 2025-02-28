package in.esmartsolution.shree.pro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.viewbinding.ViewBinding;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

import in.esmartsolution.shree.pro.utils.ConnectionDetector;
import in.esmartsolution.shree.pro.utils.GPSTracker;

public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {
    public ConnectionDetector cd;
    public App app;
    public Context mContext;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public GPSTracker gpsTracker;
    public LocationManager locationManager;
    private final static String TAG = "LoginActivity";
    private final static int REQUEST_CHECK_SETTINGS = 0;
    private Location mLastKnownLocation;
    private static final String KEY_LOCATION = "location";

    protected T binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = inflateBinding(getLayoutInflater());
        setContentView(binding.getRoot());

//        setContentView(getActivityLayout());
//        unbinder = ButterKnife.bind(this);

        mContext = BaseActivity.this;
        app = (App) getApplication();
        cd = new ConnectionDetector(mContext);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        gpsTracker = new GPSTracker(mContext);
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
    }

    public void getLocation() {
        if (Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            ask_permissions();
        } else {
            displayLocationSettingsRequest(BaseActivity.this);
        }
    }

//    protected abstract int getActivityLayout();

    protected abstract T inflateBinding(LayoutInflater inflater);

    public void ask_permissions() {
        ActivityCompat.requestPermissions(BaseActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CALL_PHONE},
                1);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayLocationSettingsRequest(BaseActivity.this);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
//permission is denied (and never ask again is  checked)
                    //shouldShowRequestPermissionRationale will return false
//                    Toast.makeText(SplashActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                            && !ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                        Log.e("in", "else");
//                        new MaterialDialog.Builder(this)
//                                .content("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
//                                .positiveText("Yes")
//                                .negativeText("Cancel")
//                                .onPositive((dialog, which) -> startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.app.we"))))
//                                .onNegative((dialog, which) -> finish())
//                                .show();
                    } else {
                        ask_permissions();
                    }
                }
                return;
            }
        }
    }

    public void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(locationSettingsResult -> {
            final Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
//                        Log.e(TAG, "All location settings are satisfied.");
                    saveCurrentLocation();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(BaseActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
//                            Log.e(TAG, "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                    break;
            }
        });
    }

    private void saveCurrentLocation() {
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsTracker = new GPSTracker(mContext);
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            app.getPreferences().setLat("" + latitude);
            app.getPreferences().setLng("" + longitude);
//            Log.e("location", latitude + "||" + longitude);
            try {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                mFusedLocationProviderClient.getLastLocation()
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location mLastLocation = task.getResult();
//                                    Log.e("this OnCompleteListener", mLastLocation.getLatitude() + "||"
//                                            + mLastLocation.getLongitude());
                            } else {
                                Log.w(TAG, "getLastLocation:exception", task.getException());
                            }
                        });

                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        task.addOnCompleteListener(task1 -> {
                            mLastKnownLocation = (Location) task1.getResult();
                            if (mLastKnownLocation != null) {
                                double latitude1 = mLastKnownLocation.getLatitude();
                                double longitude1 = mLastKnownLocation.getLongitude();
                                app.getPreferences().setLat("" + latitude1);
                                app.getPreferences().setLng("" + longitude1);
//                                        Log.e("addOnCompleteListener", latitude + "||" + longitude);
                                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(BaseActivity.this);
////                        // onReceive() will be called as a result of this call:
                                manager.sendBroadcast(new Intent("address-change"));
                            } else {
//                                        Log.e(TAG, "else");
                            }
                        });
                    } else {
//                            Log.e(TAG, "Current location is null. Using defaults.");
//                            Log.e(TAG, "Exception: %s", task.getException());
                    }
                });
            } catch (SecurityException e) {
//                Log.e("Exception: %s", e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        saveCurrentLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
//                        Toast.makeText(BaseActivity.this, R.string.gps_enable_msg, Toast.LENGTH_LONG).show();
                        displayLocationSettingsRequest(BaseActivity.this);
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
////        outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
//        super.onSaveInstanceState(outState);
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

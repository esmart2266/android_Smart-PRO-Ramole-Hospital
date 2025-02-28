package in.esmartsolution.shree.pro;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.BuildConfig;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.esmartsolution.shree.pro.api.ApiRequestHelper;
import in.esmartsolution.shree.pro.databinding.ActivityDailyReportingBinding;
import in.esmartsolution.shree.pro.model.Data;
import in.esmartsolution.shree.pro.model.GeoResponse;
import in.esmartsolution.shree.pro.model.SearchData;
import in.esmartsolution.shree.pro.model.UserData;
import in.esmartsolution.shree.pro.model.Visits;
import in.esmartsolution.shree.pro.utils.ConnectionDetector;
import in.esmartsolution.shree.pro.utils.Utils;
import in.esmartsolution.shree.pro.widget.materialprogress.CustomProgressDialog;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import retrofit2.Call;

public class DailyReportingActivity extends BaseActivity<ActivityDailyReportingBinding> {

    //    @BindView(R.id.et_doctorName)
//    MaterialEditText etDoctorName;
//    @BindView(R.id.et_speciality)
//    MaterialEditText etSpeciality;
//    @BindView(R.id.et_contactNo)
//    MaterialEditText etContactNo;
//    @BindView(R.id.et_telephoneNo)
//    MaterialEditText etTelephoneNo;
//    @BindView(R.id.et_birthdate)
//    MaterialEditText etBirthdate;
//    @BindView(R.id.et_area)
//    MaterialEditText etArea;
//    @BindView(R.id.et_cityTaluka)
//    MaterialEditText etCityTaluka;
//    @BindView(R.id.et_remark)
//    MaterialEditText etRemark;
//    @BindView(R.id.et_otherSpeciality)
//    MaterialEditText etOtherSpeciality;
//    @BindView(R.id.et_doctorSearch)
//    MaterialEditText etDoctorSearch;
    MaterialEditText etDoctorName;
    MaterialEditText etSpeciality;
    MaterialEditText etContactNo;
    MaterialEditText etTelephoneNo;
    MaterialEditText etBirthdate;
    MaterialEditText etArea;
    MaterialEditText etCityTaluka;
    MaterialEditText etRemark;
    MaterialEditText etOtherSpeciality;
    MaterialEditText etDoctorSearch;

    private String address = "";
    private Call<GeoResponse> geoResponseCall;
    private HashMap<String, String> params;
    public LocationService locationService;
    private BroadcastReceiver locationUpdateReceiver;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private ValueEventListener valueEventListener;
    private Intent intentLocationService;
    private String dob = "";

    protected ActivityDailyReportingBinding inflateBinding(LayoutInflater inflater) {
        return ActivityDailyReportingBinding.inflate(inflater);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize views using binding
        etDoctorName = binding.etDoctorName;
        etSpeciality = binding.etSpeciality;
        etContactNo = binding.etContactNo;
        etTelephoneNo = binding.etTelephoneNo;
        etBirthdate = binding.etBirthdate;
        etArea = binding.etArea;
        etCityTaluka = binding.etCityTaluka;
        etRemark = binding.etRemark;
        etOtherSpeciality = binding.etOtherSpeciality;
        etDoctorSearch = binding.etDoctorSearch;

        etOtherSpeciality.setVisibility(View.GONE);
//        etCityTaluka.setText("Kolhapur");
//        if (app.getPreferences().getLat() != null && app.getPreferences().getLng() != null)
//            getAddressFromLatLng(app.getPreferences().getLat(), app.getPreferences().getLng(), false);
        intentLocationService = new Intent(this.getApplication(), LocationService.class);
        this.getApplication().startService(intentLocationService);
        this.getApplication().bindService(intentLocationService, serviceConnection, Context.BIND_AUTO_CREATE);
        locationUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                Log.e("in", "locationUpdateReceiver");
                final Location newLocation = intent.getParcelableExtra("location");
                app.getPreferences().setLat("" + newLocation.getLatitude());
                app.getPreferences().setLng("" + newLocation.getLongitude());
                if (valueEventListener != null)
                    myRef.removeEventListener(valueEventListener);
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
//                            String value = dataSnapshot.getValue(String.class);
                        Data data = app.getPreferences().getLoggedInUser().getData();
                        data.setLat(newLocation.getLatitude() + "");
                        data.setLng(newLocation.getLongitude() + "");
                        myRef.child(Utils.HOSP_TABLE).child(data.getId()).setValue(data);
//                            Log.e("val", "Value is: " + value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
//                        Log.e("error", "Failed to read value.", error.toException());
                    }
                };
                myRef.addValueEventListener(valueEventListener);
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(locationUpdateReceiver, new IntentFilter("LocationUpdated"));
//        locationService.startLogging();
        getLocation();
        login();
        binding.rrSpeciality.setOnClickListener(this::onViewClicked);
        binding.etSpeciality.setOnClickListener(this::onViewClicked);
        binding.etBirthdate.setOnClickListener(this::onViewClicked);
        binding.rrSearch.setOnClickListener(this::onViewClicked);
        binding.btnSubmit.setOnClickListener(this::onViewClicked);
    }

//    @Override
//    protected int getActivityLayout() {
//        return R.layout.activity_daily_reporting;
//    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //    @OnClick({R.id.rr_speciality, R.id.et_speciality, R.id.et_birthdate, R.id.rr_search, R.id.btn_submit})
    public void onViewClicked(View view) {
        if (view == binding.rrSpeciality) {

        } else if (view == binding.etSpeciality) {
            showSpecialityDialog();
        } else if (view == binding.etBirthdate) {
            Calendar mMaxDate = Calendar.getInstance();
            int mMaxYear = mMaxDate.get(Calendar.YEAR);
            mMaxYear = mMaxYear - 18;
            mMaxDate.set(Calendar.YEAR, mMaxYear);
            DatePickerDialog dialog = new DatePickerDialog(mContext, (view1, year, month, dayOfMonth) -> {
                String date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;
                dob = year + "-" + (month + 1) + "-" + dayOfMonth;
                etBirthdate.setText(date);
            }, mMaxDate.get(Calendar.YEAR), mMaxDate.get(Calendar.MONTH), mMaxDate.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(mMaxDate.getTimeInMillis());
//                DatePickerDialog dpd = DatePickerDialog.newInstance((view12, year, monthOfYear, dayOfMonth) -> {
//                            String date = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                            dob = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
//                            etBirthdate.setText(date);
//                        },
//                        mMaxDate.get(Calendar.YEAR), mMaxDate.get(Calendar.MONTH), mMaxDate.get(Calendar.DAY_OF_MONTH)
//                );
//                dpd.setMaxDate(mMaxDate);
//                dpd.show(getFragmentManager(), "Datepickerdialog");
            dialog.show();
        } else if (view == binding.rrSearch) {
            String doctorMobile = etDoctorSearch.getText().toString().trim();
            if (TextUtils.isEmpty(doctorMobile)) {
                etDoctorSearch.setError("Enter doctor's mobile(follow-up)");
                etDoctorSearch.requestFocus();
                return;
            }
            if (doctorMobile.length() < 10) {
                etDoctorSearch.setError("Enter 10 digit mobile number");
                etDoctorSearch.requestFocus();
                return;
            }
            findvisit(doctorMobile);
        } else if (view == binding.btnSubmit) {
            String doctorName = etDoctorName.getText().toString().trim();
            String speciality = etSpeciality.getText().toString().trim();
            String otherSpeciality = etOtherSpeciality.getText().toString().trim();
            String contactNo = etContactNo.getText().toString().trim();
            String telephoneNo = etTelephoneNo.getText().toString().trim();
            String area = etArea.getText().toString().trim();
            String cityTaluka = etCityTaluka.getText().toString().trim();
            String remark = etRemark.getText().toString().trim();
            if (TextUtils.isEmpty(doctorName)) {
                etDoctorName.setError("Enter Name of Doctor");
                etDoctorName.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(speciality)) {
                etSpeciality.setError("Select Speciality");
                etSpeciality.requestFocus();
                return;
            }
            if (speciality.equalsIgnoreCase("Other") && TextUtils.isEmpty(otherSpeciality)) {
                etOtherSpeciality.setError("Enter Other Speciality");
                etOtherSpeciality.requestFocus();
                return;
            }
            if (speciality.equalsIgnoreCase("Other"))
                speciality = otherSpeciality;
            if (TextUtils.isEmpty(contactNo)) {
                etContactNo.setError("Enter Mobile Number");
                etContactNo.requestFocus();
                return;
            }
            if (contactNo.length() < 10) {
                etContactNo.setError("Enter 10 digit mobile number");
                etContactNo.requestFocus();
                return;
            }
//                if (TextUtils.isEmpty(telephoneNo)) {
//                    etTelephoneNo.setError("Enter Landline Number");
//                    etTelephoneNo.requestFocus();
//                    return;
//                }
//                if (TextUtils.isEmpty(dob)) {
//                    etBirthdate.setError("Select Birthdate");
//                    etBirthdate.requestFocus();
//                    return;
//                }
            if (TextUtils.isEmpty(area)) {
                etArea.setError("Enter Area");
                etArea.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(cityTaluka)) {
                etCityTaluka.setError("Enter City/Taluka");
                etCityTaluka.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(remark)) {
                etRemark.setError("Enter Remark");
                etRemark.requestFocus();
                return;
            }
            //if('userid', 'doctorname', 'speciality','mobile', 'area', 'city', 'remarks',
            // 'geoaddress', 'landline'
            params = new HashMap<>();
            params.put("userid", app.getPreferences().getLoggedInUser().getData().getId());
            params.put("doctorname", doctorName);
            params.put("speciality", speciality);
            params.put("mobile", contactNo);
            params.put("area", area);
            params.put("birthdate", this.dob);
            params.put("city", cityTaluka);
            params.put("remarks", remark);
            params.put("landline", telephoneNo);
            LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (app.getPreferences().getLat() != null && !TextUtils.isEmpty(app.getPreferences().getLat()) &&
                        app.getPreferences().getLng() != null && !TextUtils.isEmpty(app.getPreferences().getLng())
                        && Double.parseDouble(app.getPreferences().getLat()) != 0 && Double.parseDouble(app.getPreferences().getLng()) != 0)
                    getAddressFromGeocoder(app.getPreferences().getLat(), app.getPreferences().getLng(), true);
                else
                    getCurrentLatLong();
            } else {
                Utils.showLongToast(mContext, "Please switch on GPS.");
            }
        }


//        switch (view.getId()) {
//            case R.id.rr_speciality:
//            case R.id.et_speciality:
//                showSpecialityDialog();
//                break;
//            case R.id.et_birthdate:
//                Calendar mMaxDate = Calendar.getInstance();
//                int mMaxYear = mMaxDate.get(Calendar.YEAR);
//                mMaxYear = mMaxYear - 18;
//                mMaxDate.set(Calendar.YEAR, mMaxYear);
//                DatePickerDialog dialog = new DatePickerDialog(mContext, (view1, year, month, dayOfMonth) -> {
//                    String date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;
//                    dob = year + "-" + (month + 1) + "-" + dayOfMonth;
//                    etBirthdate.setText(date);
//                }, mMaxDate.get(Calendar.YEAR), mMaxDate.get(Calendar.MONTH), mMaxDate.get(Calendar.DAY_OF_MONTH));
//                dialog.getDatePicker().setMaxDate(mMaxDate.getTimeInMillis());
////                DatePickerDialog dpd = DatePickerDialog.newInstance((view12, year, monthOfYear, dayOfMonth) -> {
////                            String date = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
////                            dob = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
////                            etBirthdate.setText(date);
////                        },
////                        mMaxDate.get(Calendar.YEAR), mMaxDate.get(Calendar.MONTH), mMaxDate.get(Calendar.DAY_OF_MONTH)
////                );
////                dpd.setMaxDate(mMaxDate);
////                dpd.show(getFragmentManager(), "Datepickerdialog");
//                dialog.show();
//                break;
//            case R.id.rr_search:
//                String doctorMobile = etDoctorSearch.getText().toString().trim();
//                if (TextUtils.isEmpty(doctorMobile)) {
//                    etDoctorSearch.setError("Enter doctor's mobile(follow-up)");
//                    etDoctorSearch.requestFocus();
//                    return;
//                }
//                if (doctorMobile.length() < 10) {
//                    etDoctorSearch.setError("Enter 10 digit mobile number");
//                    etDoctorSearch.requestFocus();
//                    return;
//                }
//                findvisit(doctorMobile);
//                break;
//            case R.id.btn_submit:
//                String doctorName = etDoctorName.getText().toString().trim();
//                String speciality = etSpeciality.getText().toString().trim();
//                String otherSpeciality = etOtherSpeciality.getText().toString().trim();
//                String contactNo = etContactNo.getText().toString().trim();
//                String telephoneNo = etTelephoneNo.getText().toString().trim();
//                String area = etArea.getText().toString().trim();
//                String cityTaluka = etCityTaluka.getText().toString().trim();
//                String remark = etRemark.getText().toString().trim();
//                if (TextUtils.isEmpty(doctorName)) {
//                    etDoctorName.setError("Enter Name of Doctor");
//                    etDoctorName.requestFocus();
//                    return;
//                }
//                if (TextUtils.isEmpty(speciality)) {
//                    etSpeciality.setError("Select Speciality");
//                    etSpeciality.requestFocus();
//                    return;
//                }
//                if (speciality.equalsIgnoreCase("Other") && TextUtils.isEmpty(otherSpeciality)) {
//                    etOtherSpeciality.setError("Enter Other Speciality");
//                    etOtherSpeciality.requestFocus();
//                    return;
//                }
//                if (speciality.equalsIgnoreCase("Other"))
//                    speciality = otherSpeciality;
//                if (TextUtils.isEmpty(contactNo)) {
//                    etContactNo.setError("Enter Mobile Number");
//                    etContactNo.requestFocus();
//                    return;
//                }
//                if (contactNo.length() < 10) {
//                    etContactNo.setError("Enter 10 digit mobile number");
//                    etContactNo.requestFocus();
//                    return;
//                }
////                if (TextUtils.isEmpty(telephoneNo)) {
////                    etTelephoneNo.setError("Enter Landline Number");
////                    etTelephoneNo.requestFocus();
////                    return;
////                }
////                if (TextUtils.isEmpty(dob)) {
////                    etBirthdate.setError("Select Birthdate");
////                    etBirthdate.requestFocus();
////                    return;
////                }
//                if (TextUtils.isEmpty(area)) {
//                    etArea.setError("Enter Area");
//                    etArea.requestFocus();
//                    return;
//                }
//                if (TextUtils.isEmpty(cityTaluka)) {
//                    etCityTaluka.setError("Enter City/Taluka");
//                    etCityTaluka.requestFocus();
//                    return;
//                }
//                if (TextUtils.isEmpty(remark)) {
//                    etRemark.setError("Enter Remark");
//                    etRemark.requestFocus();
//                    return;
//                }
//                //if('userid', 'doctorname', 'speciality','mobile', 'area', 'city', 'remarks',
//                // 'geoaddress', 'landline'
//                params = new HashMap<>();
//                params.put("userid", app.getPreferences().getLoggedInUser().getData().getId());
//                params.put("doctorname", doctorName);
//                params.put("speciality", speciality);
//                params.put("mobile", contactNo);
//                params.put("area", area);
//                params.put("birthdate", this.dob);
//                params.put("city", cityTaluka);
//                params.put("remarks", remark);
//                params.put("landline", telephoneNo);
//                LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
//                if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    if (app.getPreferences().getLat() != null && !TextUtils.isEmpty(app.getPreferences().getLat()) &&
//                            app.getPreferences().getLng() != null && !TextUtils.isEmpty(app.getPreferences().getLng())
//                            && Double.parseDouble(app.getPreferences().getLat()) != 0 && Double.parseDouble(app.getPreferences().getLng()) != 0)
//                        getAddressFromGeocoder(app.getPreferences().getLat(), app.getPreferences().getLng(), true);
//                    else
//                        getCurrentLatLong();
//                } else {
//                    Utils.showLongToast(mContext, "Please switch on GPS.");
//                }
//                break;
//        }
    }

    private void getCurrentLatLong() {
        try {
            CustomProgressDialog pd = new CustomProgressDialog(mContext);
            pd.show();
            LocationGooglePlayServicesProvider provider = new LocationGooglePlayServicesProvider();
            provider.setCheckLocationSettings(true);
            SmartLocation smartLocation = new SmartLocation.Builder(mContext).logging(true).build();
            smartLocation.location(provider).start(location -> {
                if (pd != null && pd.isShowing())
                    pd.dismiss();
                if (location != null) {
                    app.getPreferences().setLat("" + location.getLatitude());
                    app.getPreferences().setLng("" + location.getLongitude());
                    getAddressFromGeocoder(app.getPreferences().getLat(), app.getPreferences().getLng(), true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSpecialityDialog() {
        List<String> specialities = new ArrayList<>();
        specialities.add("Surgeon");
        specialities.add("Physician");
        specialities.add("Neurosurgeon");
        specialities.add("General Practitioner");
        specialities.add("Gynaecologist");
        specialities.add("Dentist");
        specialities.add("ENT");
        specialities.add("Other");
        new MaterialDialog.Builder(mContext)
                .title("Select Speciality")
                .items(specialities)
                .itemsCallback((dialog, view1, which, text) -> {
                    etSpeciality.setText(text.toString());
                    if (which < specialities.size() - 1) {
                        etOtherSpeciality.setVisibility(View.GONE);
                    } else {
                        etOtherSpeciality.setVisibility(View.VISIBLE);
                        etOtherSpeciality.requestFocus();
                    }
                })
                .show();
    }

    private void findvisit(String mobile) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        ConnectionDetector cd = new ConnectionDetector(mContext);
        if (cd.isConnectingToInternet()) {
            final CustomProgressDialog pd = new CustomProgressDialog(mContext);
            pd.setTitle("Loading...");
            pd.show();
            app.getApiRequestHelper().findvisit(params, new ApiRequestHelper.OnRequestComplete() {
                @Override
                public void onSuccess(Object object) {
                    pd.dismiss();
                    SearchData response = (SearchData) object;
                    if (response != null) {
                        if (response.getResponsecode() == 200 && response.getVisits() != null) {
//                            if (response.getMessage() != null && !TextUtils.isEmpty(response.getMessage()))
//                                Utils.showShortToast(mContext, response.getMessage());
                            Visits visits = response.getVisits();
                            etDoctorName.setText(Utils.checkNotEmpty(visits.getDoctorName()));
                            etSpeciality.setText(Utils.checkNotEmpty(visits.getSpeciality()));
                            etContactNo.setText(Utils.checkNotEmpty(visits.getMobile()));
                            etTelephoneNo.setText(Utils.checkNotEmpty(visits.getLandline()));
                            if (visits.getBirthDate() != null && !TextUtils.isEmpty(visits.getBirthDate()) && !visits.getBirthDate().equals("0000-00-00")) {
                                dob = visits.getBirthDate();
                                etBirthdate.setText(Utils.ymdTodmy(visits.getBirthDate()));
                            }
                            etArea.setText(Utils.checkNotEmpty(visits.getArea()));
                            etCityTaluka.setText(Utils.checkNotEmpty(visits.getCity()));
                            etRemark.setText(Utils.checkNotEmpty(visits.getRemarks()));
                            etOtherSpeciality.setText("");
                            etOtherSpeciality.setVisibility(View.GONE);
                            etDoctorSearch.setText("");
                        } else {
                            if (response.getMessage() != null && !TextUtils.isEmpty(response.getMessage()))
                                Utils.showLongToast(mContext, response.getMessage());
                        }
                    } else {
                        Utils.showLongToast(mContext, Utils.UNPROPER_RESPONSE);
                    }
                }

                @Override
                public void onFailure(String apiResponse) {
                    pd.dismiss();
                    Utils.showLongToast(mContext, apiResponse);
                }
            });
        } else {
            Utils.alert_dialog(mContext);
        }
    }

    private void markvisit(Map<String, String> params) {
        params.put("geoaddress", address);
        ConnectionDetector cd = new ConnectionDetector(mContext);
        if (cd.isConnectingToInternet()) {
            final CustomProgressDialog pd = new CustomProgressDialog(mContext);
            pd.setTitle("Loading...");
            pd.show();
            app.getApiRequestHelper().markvisit(params, new ApiRequestHelper.OnRequestComplete() {
                @Override
                public void onSuccess(Object object) {
                    pd.dismiss();
                    UserData response = (UserData) object;
                    if (response != null) {
                        if (response.getResponsecode() == 200) {
                            if (response.getMessage() != null && !TextUtils.isEmpty(response.getMessage()))
                                Utils.showShortToast(mContext, response.getMessage());
                            Data data = app.getPreferences().getLoggedInUser().getData();
                            data.setLat(data.getLat() + "");
                            data.setLng(data.getLng() + "");
                            myRef.child(Utils.HOSP_TABLE).child(data.getId()).setValue(data);
                            etDoctorName.setText("");
                            etSpeciality.setText("");
                            etContactNo.setText("");
                            etTelephoneNo.setText("");
                            dob = "";
                            etBirthdate.setText("");
                            etArea.setText("");
                            etCityTaluka.setText("");
                            etRemark.setText("");
                            etOtherSpeciality.setText("");
                            etOtherSpeciality.setVisibility(View.GONE);
                        } else {
                            if (response.getMessage() != null && !TextUtils.isEmpty(response.getMessage()))
                                Utils.showLongToast(mContext, response.getMessage());
                        }
                    } else {
                        Utils.showLongToast(mContext, Utils.UNPROPER_RESPONSE);
                    }
                }

                @Override
                public void onFailure(String apiResponse) {
                    pd.dismiss();
                    Utils.showLongToast(mContext, apiResponse);
                }
            });
        } else {
            Utils.alert_dialog(mContext);
        }
    }

    private void login() {
        ConnectionDetector cd = new ConnectionDetector(mContext);
        if (cd.isConnectingToInternet()) {
            final CustomProgressDialog pd = new CustomProgressDialog(mContext);
            pd.setTitle("Loading...");
            pd.show();
            Map<String, String> params = new HashMap<>();
            params.put("usrname", app.getPreferences().getLoggedInUser().getData().getEmail());
            params.put("passwrd", app.getPreferences().getLoggedInUser().getData().getPassword());

            app.getApiRequestHelper().login(params, new ApiRequestHelper.OnRequestComplete() {
                @Override
                public void onSuccess(Object object) {
                    pd.dismiss();
                    UserData response = (UserData) object;
                    if (response != null) {
                        if (response.getResponsecode() != 200) {
                            logout();
                        } else {
                            if (response.getData() != null) {
                                Data data = response.getData();
                                data.setEmail(app.getPreferences().getLoggedInUser().getData().getEmail());
                                data.setPassword(app.getPreferences().getLoggedInUser().getData().getPassword());
                                response.setData(data);
                                app.getPreferences().setLoggedInUser(response);
//                                if (response.getData().getStatus().equalsIgnoreCase("0"))
//                                    logout();
                            }
                        }
                    } else {
                        logout();
                    }
                }

                @Override
                public void onFailure(String apiResponse) {
                    pd.dismiss();
                    Utils.showLongToast(mContext, apiResponse);
                }
            });
        } else {
            Utils.alert_dialog(mContext);
        }
    }

    private void logout() {
        app.getPreferences().logOutUser();
        startActivity(new Intent(mContext, LoginActivity.class));
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    public void getAddressFromGeocoder(String lat, String lng, boolean isMarkVisit) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(Double.parseDouble(lat));
        location.setLongitude(Double.parseDouble(lng));
        new AsyncTask<Void, Integer, List<Address>>() {
            CustomProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new CustomProgressDialog(mContext);
                pd.show();
            }

            @Override
            public List<Address> doInBackground(Void... arg0) {
                List<Address> results = null;
                if (mContext != null) {
                    Geocoder coder = new Geocoder(mContext.getApplicationContext(), Locale.ENGLISH);
                    try {
                        results = coder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return results;
            }

            @Override
            protected void onPostExecute(List<Address> results) {
                if (pd != null && pd.isShowing())
                    pd.dismiss();
                if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng) && Double.parseDouble(lat) != 0 &&
                        Double.parseDouble(lng) != 0) {
                    params.put("latitude", lat);
                    params.put("longitude", lng);
                    if (results != null && results.size() > 0) {
//                    String cityName = results.get(0).getAddressLine(0);
//                    String locality = results.get(0).getLocality();
                        address = results.get(0).getAddressLine(0);
                    }
                    markvisit(params);
                } else {
                    new MaterialDialog.Builder(mContext)
                            .content("Failed to fetch current latitude & longitude. Make sure you have turn on GPS. and try again.")
                            .positiveText("Ok")
                            .show();
                }
            }
        }.execute();
    }

//    public void getAddressFromLatLng(String lat, String lng, boolean isMarkVisit) {
////        String address = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
////                        + Locale.getDefault().getCountry(), lat, lng);
//        Map<String, String> map = new HashMap<>();
//        map.put("latlng", lat + "," + lng);
//        map.put("key", "AIzaSyDJhz6Mamf1ak_NpuXm1STAaNpeRsaSPsQ");
//        map.put("sensor", "true");
//        final CustomProgressDialog pd = new CustomProgressDialog(mContext);
//        pd.setTitle("Loading...");
//        pd.show();
//        geoResponseCall = app.getApiRequestHelper().getAddressFromLatLng(map, new ApiRequestHelper.OnRequestComplete() {
//            @Override
//            public void onSuccess(Object object) {
//                pd.dismiss();
//                GeoResponse response = (GeoResponse) object;
//                if (response != null) {
//                    List<Result> results = response.getResults();
//                    if (results != null && results.size() > 0) {
//                        if (results.get(0).getFormattedAddress() != null)
//                            address = results.get(0).getFormattedAddress();
//                        else if (results.size() > 1 && results.get(1).getFormattedAddress() != null)
//                            address = results.get(1).getFormattedAddress();
//                        if (isMarkVisit && address != null && !TextUtils.isEmpty(address)) {
//                            markvisit(params);
//                        } else if (isMarkVisit) {
//                            Utils.showLongToast(mContext, "Please turn on your location service. or restart the application");
//                        }
//                    } else if (isMarkVisit) {
//                        Utils.showLongToast(mContext, "Please turn on your location service. or restart the application");
//                    }
//                } else if (isMarkVisit) {
//                    Utils.showLongToast(mContext, "Please turn on your location service. or restart the application");
//                }
//            }
//
//            @Override
//            public void onFailure(String apiResponse) {
//                pd.dismiss();
//                Log.e("in", "error " + apiResponse);
//                Utils.showLongToast(mContext, apiResponse);
//            }
//        });
//    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            String name = className.getClassName();
            if (name.endsWith("LocationService")) {
                locationService = ((LocationService.LocationServiceBinder) service).getService();
                //Start Tracking
                locationService.startLogging();
                locationService.startUpdatingLocation();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            if (className.getClassName().equals("LocationService")) {
                locationService.stopUpdatingLocation();
                locationService = null;
            }
        }
    };

    @Override
    public void onDestroy() {
        if (geoResponseCall != null && geoResponseCall.isExecuted())
            geoResponseCall.cancel();
        if (myRef != null && valueEventListener != null)
            myRef.removeEventListener(valueEventListener);
        try {
            if (locationUpdateReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdateReceiver);
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_myvisits) {
            startActivity(new Intent(mContext, MyVisitsActivity.class));

        } else if (id == R.id.action_logout) {
            logout();

        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

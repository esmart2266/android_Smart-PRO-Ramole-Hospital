package in.esmartsolution.shree.pro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.esmartsolution.shree.pro.adapter.MyVisitsAdapter;
import in.esmartsolution.shree.pro.api.ApiRequestHelper;
import in.esmartsolution.shree.pro.databinding.ActivityVisitsBinding;
import in.esmartsolution.shree.pro.model.Visit;
import in.esmartsolution.shree.pro.model.VisitData;
import in.esmartsolution.shree.pro.utils.ConnectionDetector;
import in.esmartsolution.shree.pro.utils.Utils;
import in.esmartsolution.shree.pro.widget.materialprogress.CustomProgressDialog;

public class MyVisitsActivity extends BaseActivity<ActivityVisitsBinding> {
    Toolbar toolbar;
    TextView toolbarTitle;
    TextView tv_error;
    RecyclerView rv_visits;
    DatePickerDialog dpd;
    private MyVisitsAdapter myVisitsAdapter;
    private Filter filter;
    List<Visit> multiselect_list = new ArrayList<>();
    private List<Visit> visitList;
    private MenuItem action_message;

    protected ActivityVisitsBinding inflateBinding(LayoutInflater inflater) {
        return ActivityVisitsBinding.inflate(inflater);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = binding.toolbar;
        toolbarTitle = binding.toolbarTitle;
        tv_error = binding.tvError;
        rv_visits = binding.rvVisits;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText("My Visits");
        rv_visits.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        myvisits();
    }

//    @Override
//    protected int getActivityLayout() {
//        return R.layout.activity_visits;
//    }

    private void myvisits() {
        Map<String, String> map = new HashMap<>();
        map.put("userid", app.getPreferences().getLoggedInUser().getData().getId());
        ConnectionDetector cd = new ConnectionDetector(mContext);
        if (cd.isConnectingToInternet()) {
            final CustomProgressDialog pd = new CustomProgressDialog(mContext);
            pd.setTitle("Loading...");
            pd.show();
            app.getApiRequestHelper().myvisits(map, new ApiRequestHelper.OnRequestComplete() {
                @Override
                public void onSuccess(Object object) {
                    pd.dismiss();
                    VisitData response = (VisitData) object;
                    if (response != null) {
                        if (response.getResponsecode() == 200) {
                            if (response.getVisits() != null && response.getVisits().size() > 0) {
                                visitList = response.getVisits();
                                String maxDateTime = visitList.get(0).getVisitDateTime(); //2017-02-05 23:44:33
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date maxDate = null;
                                try {
                                    maxDate = format.parse(maxDateTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Calendar maxCalendar = Calendar.getInstance();
                                maxCalendar.setTime(maxDate);

                                String minDateTime = visitList.get(visitList.size() - 1).getVisitDateTime();
                                Date minDate = null;
                                try {
                                    minDate = format.parse(minDateTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Calendar minCalendar = Calendar.getInstance();
                                minCalendar.setTime(minDate);
                                dpd = DatePickerDialog.newInstance(
                                        (view, year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd) -> {
                                            String selectedMinDate = year + "-" + (++monthOfYear) + "-" + dayOfMonth;
                                            String selectedMaxDate = yearEnd + "- " + (++monthOfYearEnd) + "-" + dayOfMonthEnd;
                                            try {
                                                List<Visit> newList = getNewList(visitList, selectedMinDate, selectedMaxDate);
                                                Collections.sort(newList, Collections.reverseOrder());
                                                myVisitsAdapter = new MyVisitsAdapter(mContext, newList);
                                                rv_visits.setAdapter(myVisitsAdapter);
                                                toolbarTitle.setText("My Visits(" + newList.size() + ")");
                                                if (newList.size() > 0)
                                                    tv_error.setVisibility(View.GONE);
                                                else {
                                                    tv_error.setText("No Records available.");
                                                    tv_error.setVisibility(View.VISIBLE);
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        },
                                        minCalendar.get(Calendar.YEAR), minCalendar.get(Calendar.MONTH),
                                        minCalendar.get(Calendar.DAY_OF_MONTH), maxCalendar.get(Calendar.YEAR),
                                        maxCalendar.get(Calendar.MONTH), maxCalendar.get(Calendar.DAY_OF_MONTH));
                                dpd.setThemeDark(false);
                                dpd.setMaxDate(maxCalendar);
                                dpd.setMinDate(minCalendar);
                                myVisitsAdapter = new MyVisitsAdapter(mContext, response.getVisits());
                                rv_visits.setAdapter(myVisitsAdapter);
                                toolbarTitle.setText("My Visits(" + visitList.size() + ")");
                                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                                showCurrentDayData(currentDate, visitList);
                            } else {
                                tv_error.setText("No Records available.");
                                tv_error.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (response.getMessage() != null && !TextUtils.isEmpty(response.getMessage()))
                                Utils.showLongToast(mContext, response.getMessage());
                            tv_error.setText("No Records available.");
                            tv_error.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Utils.showLongToast(mContext, Utils.UNPROPER_RESPONSE);
                        tv_error.setText("No Records available.");
                        tv_error.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(String apiResponse) {
                    pd.dismiss();
                    Utils.showLongToast(mContext, apiResponse);
                    tv_error.setText("No Records available.");
                    tv_error.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Utils.alert_dialog(mContext);
        }
    }

    public void multi_select(int position) {
        if (multiselect_list.contains(visitList.get(position))) {
            String id = myVisitsAdapter.visitList.get(position).getVisitId();
            for (int i = 0; i < visitList.size(); i++) {
                if (visitList.get(i).getVisitId().equals(id)) {
                    visitList.get(i).setSelected(false);
                    multiselect_list.remove(visitList.get(i));
                }
            }

        } else {
            String id = myVisitsAdapter.visitList.get(position).getVisitId();
            for (int i = 0; i < visitList.size(); i++) {
                if (visitList.get(i).getVisitId().equals(id)) {
                    visitList.get(i).setSelected(true);
                    multiselect_list.add(visitList.get(i));
                }
            }
        }
        if (multiselect_list.size() > 0 && action_message != null) {
            action_message.setVisible(true);
        } else if (action_message != null) {
            action_message.setVisible(false);
        }
        myVisitsAdapter.notifyDataSetChanged();
    }

    private void showCurrentDayData(String currentDate, List<Visit> visitList) {
        try {
            List<Visit> newList = getNewList(visitList, currentDate, currentDate);
            Collections.sort(newList, Collections.reverseOrder());
            myVisitsAdapter = new MyVisitsAdapter(mContext, newList);
            rv_visits.setAdapter(myVisitsAdapter);
            toolbarTitle.setText("My Visits(" + newList.size() + ")");
            if (newList.size() > 0)
                tv_error.setVisibility(View.GONE);
            else {
                tv_error.setText("No Records available. Please Select FROM - TO date");
                tv_error.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private List<Visit> getNewList(List<Visit> oldList, String selectedMinDate,
                                   String selectedMaxDate) throws ParseException {
        Date d1 = null, d2 = null, d3 = null, d4 = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        d3 = f.parse(selectedMinDate);
        d4 = f.parse(selectedMaxDate);
        List<Visit> visitList = new ArrayList<>();
        for (int i = 0; i < oldList.size(); i++) {
            String b = oldList.get(i).getVisitDateTime();
            d2 = f.parse(b);
            if (d2.compareTo(d3) >= 0 && d2.compareTo(d4) <= 0) {
                visitList.add(oldList.get(i));
            }
        }
        return visitList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        action_message = menu.findItem(R.id.action_message);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Text Here to Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (myVisitsAdapter != null)
                    myVisitsAdapter.filter();

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        else if (id == R.id.action_filter) {
            if (dpd != null) {
                dpd.show(getFragmentManager(), "DatePickerDialog");
            }
            return true;
        }
        else if (id == R.id.action_message) {
            if (multiselect_list.size() > 0) {
                List<String> mobileList = new ArrayList<>();
                for (int i = 0; i < multiselect_list.size(); i++) {
                    if (multiselect_list.get(i).getMobile() != null && !TextUtils.isEmpty(multiselect_list.get(i).getMobile())) {
                        mobileList.add(multiselect_list.get(i).getMobile());
                    }
                }
                if (mobileList.size() > 0) {
                    String replace = mobileList.toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", ";");
                    sendsms(replace);
                }
            }
            return true;
        }

//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//            case R.id.action_filter:
//                if (dpd != null)
//                    dpd.show(getFragmentManager(), "DatePickerDialog");
//                return true;
//            case R.id.action_message: {
//                if (multiselect_list.size() > 0) {
//                    List<String> mobileList = new ArrayList<>();
//                    for (int i = 0; i < multiselect_list.size(); i++) {
//                        if (multiselect_list.get(i).getMobile() != null && !TextUtils.isEmpty(multiselect_list.get(i).getMobile()))
//                            mobileList.add(multiselect_list.get(i).getMobile());
//                    }
//                    if (mobileList.size() > 0) {
//                        String replace = mobileList.toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", ";");
//                        sendsms(replace);
//                    }
//                }
//                return true;
//            }
//        }
        return super.onOptionsItemSelected(item);
    }

    private void sendsms(String mobile) {
        Uri uri = Uri.parse("smsto:" + mobile);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", "");
        mContext.startActivity(it);
    }
}

package in.esmartsolution.shree.pro;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import in.esmartsolution.shree.pro.api.ApiRequestHelper;
import in.esmartsolution.shree.pro.databinding.ActivityForgotPasswordBinding;
import in.esmartsolution.shree.pro.model.UserData;
import in.esmartsolution.shree.pro.utils.Utils;
import in.esmartsolution.shree.pro.widget.materialprogress.CustomProgressDialog;

public class ForgotPasswordActivity extends BaseActivity<ActivityForgotPasswordBinding> {
    MaterialEditText etEmail;
    TextView toolbarTitle;
    Toolbar toolbar;

    TextView textView;

    protected ActivityForgotPasswordBinding inflateBinding(LayoutInflater inflater) {
        return ActivityForgotPasswordBinding.inflate(inflater);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etEmail = binding.etEmail;
        toolbarTitle = binding.toolbarcontainer.toolbarTitle;
        toolbar = binding.toolbarcontainer.toolbar;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText("Forgot Password");

        binding.btnSubmit.setOnClickListener(this::onViewClicked);
    }


//    @Override
//    protected int getActivityLayout() {
//        return R.layout.activity_forgot_password;
//    }

    public void onViewClicked(View view) {

        if (view == binding.btnSubmit) {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter Mobile Number or Email");
                etEmail.requestFocus();
                return;
            }
            Map<String, String> map = new HashMap<>();
            map.put("username", email);
            forgetpassword(map);
        }


//        switch (view.getId()) {
//            case R.id.btn_submit: {
//                String email = etEmail.getText().toString().trim();
//                if (TextUtils.isEmpty(email)) {
//                    etEmail.setError("Enter Mobile Number or Email");
//                    etEmail.requestFocus();
//                    return;
//                }
//                Map<String, String> map = new HashMap<>();
//                map.put("username", email);
//                forgetpassword(map);
//                break;
//            }
//        }
    }

    private void forgetpassword(Map<String, String> map) {
        if (cd.isConnectingToInternet()) {
            CustomProgressDialog pd = new CustomProgressDialog(mContext);
            pd.show();
            app.getApiRequestHelper().forgetpassword(map, new ApiRequestHelper.OnRequestComplete() {
                @Override
                public void onSuccess(Object object) {
                    if (pd.isShowing()) pd.dismiss();
                    UserData userData = (UserData) object;
//                    Log.e("in", "success");
                    if (userData != null) {
                        if (userData.getResponsecode() == 200) {
                            if (userData.getMessage() != null && !TextUtils.isEmpty(userData.getMessage()))
                                Utils.showShortToast(mContext, userData.getMessage());
                            finish();
                        } else {
                            if (userData.getMessage() != null && !TextUtils.isEmpty(userData.getMessage()))
                                Utils.showLongToast(mContext, userData.getMessage());
                        }
                    } else {
                        Utils.showLongToast(mContext, Utils.UNPROPER_RESPONSE);
                    }
                }

                @Override
                public void onFailure(String apiResponse) {
                    if (pd.isShowing()) pd.dismiss();
//                    Log.e("in", "error " + apiResponse);
                    Utils.showLongToast(mContext, apiResponse);
                }
            });
        } else {
            Utils.alert_dialog(mContext);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

package in.esmartsolution.shree.pro.api;


import androidx.annotation.NonNull;
import android.text.Html;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import in.esmartsolution.shree.pro.App;
import in.esmartsolution.shree.pro.BuildConfig;
import in.esmartsolution.shree.pro.model.GeoResponse;
import in.esmartsolution.shree.pro.model.SearchData;
import in.esmartsolution.shree.pro.model.UserData;
import in.esmartsolution.shree.pro.model.VisitData;
import in.esmartsolution.shree.pro.utils.MySSLSocketFactory;
import in.esmartsolution.shree.pro.utils.Utils;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRequestHelper {

    public static interface OnRequestComplete {
        public void onSuccess(Object object);

        public void onFailure(String apiResponse);
    }

    private static ApiRequestHelper instance;
    private App app;
    private AppService appService;
    static Gson gson;
    private AppService appRxService;

    public static synchronized ApiRequestHelper init(App app) {
        if (null == instance) {
            instance = new ApiRequestHelper();
            instance.setApplication(app);
            gson = new GsonBuilder()
                    .setLenient()
                    .create();
            instance.createRestAdapter();
        }
        return instance;
    }

    public synchronized AppService getGoogleApiService() {
        return instance.createRetrofitBuilderGoogleAPI();
    }

    private AppService createRetrofitBuilderGoogleAPI() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
//        Retrofit retrofit = builder.build();
        Retrofit retrofit = builder.client(getClient().build()).build();
        appRxService = retrofit.create(AppService.class);
        return appRxService;
    }

    private void handle_fail_response(Throwable t, OnRequestComplete onRequestComplete) {
        if (t != null && t.getMessage() != null)
//            Log.e("server msg", Html.fromHtml(t.getMessage()) + "");
        if (t != null && t.getMessage() != null) {
            if (t.getMessage().contains("Unable to resolve host")) {
                onRequestComplete.onFailure(Utils.NO_INTERNET_MSG);
            } else {
                onRequestComplete.onFailure(Html.fromHtml(t.getMessage()) + "");
            }
        } else {
            onRequestComplete.onFailure(Utils.UNPROPER_RESPONSE);
        }
    }

    /**
     * API Calls
     */
    public Call<UserData> login(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<UserData> call = appService.login(params);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    onRequestComplete.onFailure(Html.fromHtml(response.message()) + "");
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
        return call;
    }

    public Call<UserData> markvisit(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<UserData> call = appService.markvisit(params);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    onRequestComplete.onFailure(Html.fromHtml(response.message()) + "");
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
        return call;
    }

    public Call<UserData> forgetpassword(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<UserData> call = appService.forgetpassword(params);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    onRequestComplete.onFailure(Html.fromHtml(response.message()) + "");
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
        return call;
    }

    public Call<VisitData> myvisits(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<VisitData> call = appService.myvisits(params);
        call.enqueue(new Callback<VisitData>() {
            @Override
            public void onResponse(Call<VisitData> call, Response<VisitData> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    onRequestComplete.onFailure(Html.fromHtml(response.message()) + "");
                }
            }

            @Override
            public void onFailure(Call<VisitData> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
        return call;
    }

    public Call<SearchData> findvisit(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<SearchData> call = appService.findvisit(params);
        call.enqueue(new Callback<SearchData>() {
            @Override
            public void onResponse(Call<SearchData> call, Response<SearchData> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    onRequestComplete.onFailure(Html.fromHtml(response.message()) + "");
                }
            }

            @Override
            public void onFailure(Call<SearchData> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
        return call;
    }

    public Call<GeoResponse> getAddressFromLatLng(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GeoResponse> call = getGoogleApiService().getAddressFromLatLng(params);
        call.enqueue(new Callback<GeoResponse>() {
            @Override
            public void onResponse(Call<GeoResponse> call, Response<GeoResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    onRequestComplete.onFailure(Html.fromHtml(response.message()) + "");
                }
            }

            @Override
            public void onFailure(Call<GeoResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
        return call;
    }
    /**
     * End API Calls
     */

    /**
     * REST Adapter Configuration
     */
    private void createRestAdapter() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(getClient().build()).build();
        appService = retrofit.create(AppService.class);
    }

    @NonNull
    public OkHttpClient.Builder getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            httpClient.sslSocketFactory(new MySSLSocketFactory(sslSocketFactory), (X509TrustManager) trustAllCerts[0]);
            httpClient.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
// add your other interceptors …

// add logging as last interceptor
        httpClient.interceptors().add(logging);
        return httpClient;
    }

    /**
     * End REST Adapter Configuration
     */

    public App getApplication() {
        return app;
    }

    public void setApplication(App app) {
        this.app = app;
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MultipartBody.FORM, descriptionString);
    }
}

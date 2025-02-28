package in.esmartsolution.shree.pro.api;


import java.util.Map;

import in.esmartsolution.shree.pro.model.GeoResponse;
import in.esmartsolution.shree.pro.model.SearchData;
import in.esmartsolution.shree.pro.model.UserData;
import in.esmartsolution.shree.pro.model.VisitData;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;


public interface AppService {
    @FormUrlEncoded
    @POST("/smartpro/ramolehospital/user/login.php")
    Call<UserData> login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/smartpro/ramolehospital/user/markvisit.php")
    Call<UserData> markvisit(@FieldMap Map<String, String> params);

    @GET("/smartpro/ramolehospital/user/forgetpasswordadmin.php")
    Call<UserData> forgetpassword(@QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/smartpro/ramolehospital/user/myvisits.php")
    Call<VisitData> myvisits(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/smartpro/ramolehospital/user/findvisit.php")
    Call<SearchData> findvisit(@FieldMap Map<String, String> params);

    @GET("/maps/api/geocode/json")
    Call<GeoResponse> getAddressFromLatLng(@QueryMap Map<String, String> options);
}

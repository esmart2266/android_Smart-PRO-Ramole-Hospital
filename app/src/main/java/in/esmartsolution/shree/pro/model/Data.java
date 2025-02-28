package in.esmartsolution.shree.pro.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Keep;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mayur on 3/19/2017.
 */
@IgnoreExtraProperties
@Keep
public class Data implements Parcelable {
    @SerializedName("userId")
    @Expose
    public String id;
    @SerializedName("firstName")
    @Expose
    public String firstName;
    @SerializedName("middleName")
    @Expose
    public String middleName;
    @SerializedName("lastName")
    @Expose
    public String lastName;
    @SerializedName("mobile1")
    @Expose
    public String mobile1;
    @SerializedName("mobile2")
    @Expose
    public String mobile2;
    @SerializedName("dob")
    @Expose
    public String dob;
    @SerializedName("doj")
    @Expose
    public String doj;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("isactive")
    @Expose
    public String isactive;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("lat")
    @Expose
    public String lat = "";
    @SerializedName("lng")
    @Expose
    public String lng = "";

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDoj() {
        return doj;
    }

    public void setDoj(String doj) {
        this.doj = doj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Data() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.firstName);
        dest.writeString(this.middleName);
        dest.writeString(this.lastName);
        dest.writeString(this.mobile1);
        dest.writeString(this.mobile2);
        dest.writeString(this.dob);
        dest.writeString(this.doj);
        dest.writeString(this.email);
        dest.writeString(this.address);
        dest.writeString(this.isactive);
        dest.writeString(this.password);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
    }

    protected Data(Parcel in) {
        this.id = in.readString();
        this.firstName = in.readString();
        this.middleName = in.readString();
        this.lastName = in.readString();
        this.mobile1 = in.readString();
        this.mobile2 = in.readString();
        this.dob = in.readString();
        this.doj = in.readString();
        this.email = in.readString();
        this.address = in.readString();
        this.isactive = in.readString();
        this.password = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel source) {
            return new Data(source);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}

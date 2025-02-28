package in.esmartsolution.shree.pro.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Northeast_ implements Parcelable {

    @SerializedName("lat")
    @Expose
    public double lat;
    @SerializedName("lng")
    @Expose
    public double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
    }

    public Northeast_() {
    }

    protected Northeast_(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
    }

    public static final Creator<Northeast_> CREATOR = new Creator<Northeast_>() {
        @Override
        public Northeast_ createFromParcel(Parcel source) {
            return new Northeast_(source);
        }

        @Override
        public Northeast_[] newArray(int size) {
            return new Northeast_[size];
        }
    };
}
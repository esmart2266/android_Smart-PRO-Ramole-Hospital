package in.esmartsolution.shree.pro.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Visit implements Parcelable, Comparable<Visit> {
    @SerializedName("visitId")
    @Expose
    public String visitId;
    @SerializedName("userId")
    @Expose
    public String userId;
    @SerializedName("doctorName")
    @Expose
    public String doctorName;
    @SerializedName("speciality")
    @Expose
    public String speciality;
    @SerializedName("mobile")
    @Expose
    public String mobile;
    @SerializedName("landline")
    @Expose
    public String landline;
    @SerializedName("area")
    @Expose
    public String area;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("remarks")
    @Expose
    public String remarks;
    @SerializedName("geoAddress")
    @Expose
    public String geoAddress;
    @SerializedName("visitDateTime")
    @Expose
    public String visitDateTime;
    @SerializedName("birthDate")
    @Expose
    public String birthDate;
    boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getGeoAddress() {
        return geoAddress;
    }

    public void setGeoAddress(String geoAddress) {
        this.geoAddress = geoAddress;
    }

    public String getVisitDateTime() {
        return visitDateTime;
    }

    public void setVisitDateTime(String visitDateTime) {
        this.visitDateTime = visitDateTime;
    }

    public Visit() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.visitId);
        dest.writeString(this.userId);
        dest.writeString(this.doctorName);
        dest.writeString(this.speciality);
        dest.writeString(this.mobile);
        dest.writeString(this.landline);
        dest.writeString(this.area);
        dest.writeString(this.city);
        dest.writeString(this.remarks);
        dest.writeString(this.geoAddress);
        dest.writeString(this.visitDateTime);
        dest.writeString(this.birthDate);
    }

    protected Visit(Parcel in) {
        this.visitId = in.readString();
        this.userId = in.readString();
        this.doctorName = in.readString();
        this.speciality = in.readString();
        this.mobile = in.readString();
        this.landline = in.readString();
        this.area = in.readString();
        this.city = in.readString();
        this.remarks = in.readString();
        this.geoAddress = in.readString();
        this.visitDateTime = in.readString();
        this.birthDate = in.readString();
    }

    public static final Creator<Visit> CREATOR = new Creator<Visit>() {
        @Override
        public Visit createFromParcel(Parcel source) {
            return new Visit(source);
        }

        @Override
        public Visit[] newArray(int size) {
            return new Visit[size];
        }
    };

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public int compareTo(@NonNull Visit o) {
        try {
            return dateFormat.parse(getVisitDateTime()).compareTo(dateFormat.parse(o.getVisitDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
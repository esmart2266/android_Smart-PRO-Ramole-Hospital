package in.esmartsolution.shree.pro.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressComponent implements Parcelable {
    @SerializedName("long_name")
    @Expose
    public String longName;
    @SerializedName("short_name")
    @Expose
    public String shortName;
    @SerializedName("types")
    @Expose
    public List<String> types = null;

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.longName);
        dest.writeString(this.shortName);
        dest.writeStringList(this.types);
    }

    public AddressComponent() {
    }

    protected AddressComponent(Parcel in) {
        this.longName = in.readString();
        this.shortName = in.readString();
        this.types = in.createStringArrayList();
    }

    public static final Creator<AddressComponent> CREATOR = new Creator<AddressComponent>() {
        @Override
        public AddressComponent createFromParcel(Parcel source) {
            return new AddressComponent(source);
        }

        @Override
        public AddressComponent[] newArray(int size) {
            return new AddressComponent[size];
        }
    };
}
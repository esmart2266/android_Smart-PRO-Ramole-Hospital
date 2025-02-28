package in.esmartsolution.shree.pro.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeoResponse {
    @SerializedName("results")
    @Expose
    public List<Result> results = null;
    @SerializedName("status")
    @Expose
    public String status;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

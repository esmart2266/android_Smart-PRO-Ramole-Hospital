package in.esmartsolution.shree.pro.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import in.esmartsolution.shree.pro.App;
import in.esmartsolution.shree.pro.MyVisitsActivity;
import in.esmartsolution.shree.pro.databinding.ListItemBinding;
import in.esmartsolution.shree.pro.model.Visit;
import in.esmartsolution.shree.pro.utils.Utils;

public class MyVisitsAdapter extends RecyclerView.Adapter<MyVisitsAdapter.RecyclerViewHolder> {
    public List<Visit> visitList;
    List<Visit> list_search = new ArrayList<>();
    Context mContext;
    App app;

    public MyVisitsAdapter(Context mContext, List<Visit> visitList) {
        this.visitList = visitList;
        this.mContext = mContext;
        list_search.addAll(visitList);
        app = (App) mContext.getApplicationContext();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
//        return new RecyclerViewHolder(view);

        ListItemBinding binding = ListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RecyclerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Visit visit = visitList.get(position);
        holder.tvDoctorName.setText(visit.getDoctorName());
        holder.tvSpeciality.setText(visit.getSpeciality());
        if (visit.getMobile() != null) {
            holder.tvMobile.setText(visit.getMobile());
            holder.ivMobile.setVisibility(View.VISIBLE);
        } else {
            holder.ivMobile.setVisibility(View.GONE);
        }
        holder.tvLandline.setText(visit.getLandline());
        holder.tvArea.setText(visit.getArea());
        if (visit.getBirthDate() != null && !visit.getBirthDate().equals("0000-00-00"))
            holder.tvBirthdate.setText(Utils.ymdTodmy(visit.getBirthDate()));
        holder.tvCity.setText(visit.getCity());
        holder.tvRemark.setText(visit.getRemarks());
        holder.tvDateTime.setText(Utils.ymdHmsTodmyHms(visit.getVisitDateTime()));
        holder.tvGeoAddress.setText(visit.getGeoAddress());
        holder.tvMobile.setPaintFlags(holder.tvMobile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.tvLandline.setPaintFlags(holder.tvLandline.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.tvMobile.setOnClickListener(v -> callPhone(visitList.get(holder.getAdapterPosition()).getMobile()));
        holder.ivMobile.setOnClickListener(v -> sendsms(visitList.get(holder.getAdapterPosition()).getMobile()));
        holder.tvLandline.setOnClickListener(v -> callPhone(visitList.get(holder.getAdapterPosition()).getLandline()));
        holder.iv_check.setVisibility(visit.isSelected() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> ((MyVisitsActivity) mContext).multi_select(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return visitList != null && visitList.size() > 0 ? visitList.size() : 0;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding binding;

        TextView tvDoctorName;
        TextView tvSpeciality;
        TextView tvMobile;
        ImageView ivMobile;
        TextView tvLandline;
        TextView tvBirthdate;
        TextView tvArea;
        TextView tvCity;
        TextView tvRemark;
        TextView tvDateTime;
        TextView tvGeoAddress;
        ImageView iv_check;

        public RecyclerViewHolder(ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            tvDoctorName = binding.tvDoctorName;
            tvSpeciality = binding.tvSpeciality;
            tvMobile = binding.tvMobile;
            ivMobile = binding.ivMobile;
            tvLandline = binding.tvLandline;
            tvBirthdate = binding.tvBirthdate;
            tvArea = binding.tvArea;
            tvCity = binding.tvCity;
            tvRemark = binding.tvRemark;
            tvDateTime = binding.tvDateTime;
            tvGeoAddress = binding.tvGeoAddress;
            iv_check = binding.ivCheck;
        }
    }

    public void callPhone(String phoneNo) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            final MaterialDialog ok = new MaterialDialog.Builder(mContext)
                    .content("Do you want to call this phone number?")
                    .positiveText("Yes")
                    .negativeText("No")
                    .show();
            ok.getActionButton(DialogAction.POSITIVE).setOnClickListener(view -> {
                ok.dismiss();
                mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo)));
            });
            ok.getActionButton(DialogAction.NEGATIVE).setOnClickListener(view -> ok.dismiss());
        }
    }

    private void sendsms(String mobile) {
        Uri uri = Uri.parse("smsto:" + mobile);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", "");
        mContext.startActivity(it);
    }

    // Filter Class
    public Filter filter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<Visit> results = new ArrayList<>();
                if (constraint != null) {
                    if (list_search != null && list_search.size() > 0) {
                        for (final Visit g : list_search) {
                            if (g.getDoctorName().toLowerCase().contains(constraint.toString()) ||
                                    g.getCity().toLowerCase().contains(constraint.toString()) ||
                                    g.getSpeciality().toLowerCase().contains(constraint.toString()) ||
                                    g.getMobile().toLowerCase().contains(constraint.toString()) ||
                                    (g.getLandline() != null && !TextUtils.isEmpty(g.getLandline())
                                            && g.getLandline().toLowerCase().contains(constraint.toString())) ||
                                    g.getArea().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                visitList = (List<Visit>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
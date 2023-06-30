package com.example.kidszone.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kidszone.R;
import com.example.kidszone.app_model.AppModel;
import com.example.kidszone.shared.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

public class AllAppsAdapter extends RecyclerView.Adapter<AllAppsAdapter.adapter_design_backend> implements Filterable {
    List<AppModel> apps;
    List<AppModel> appsFullList;
    Context ctx;

    public AllAppsAdapter(List<AppModel> apps, Context ctx) {
        this.apps = apps;
        this.ctx = ctx;
        appsFullList = apps;
    }
    private final Filter appListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AppModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(appsFullList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (AppModel app : appsFullList) {
                    if (app.getAppName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(app);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            apps.clear();
            apps.addAll((List) results.values);
            notifyDataSetChanged(); // At any change it is called
        }
    };

    @NonNull
    @Override
    public adapter_design_backend onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.all_apps_adapter_design, parent, false);
        return new adapter_design_backend(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_design_backend holder, int position) {
        AppModel app = apps.get(position);
        holder.appName.setText(app.getAppName());
        holder.appIcon.setImageDrawable(app.getIcon());
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        holder.appIcon.setColorFilter(filter);

        if (app.getStatus() == 1) { // UNBLOCKED APP
            holder.appStatus.setImageResource(0); // TODO REMOVE THE LOCKED ICON
            holder.appIcon.clearColorFilter();
        }
        else { // BLOCKED APP
            holder.appStatus.setImageResource(R.drawable.locked_icon); // TODO ADD THE LOCKED ICON
            holder.appIcon.setColorFilter(filter);
        }

        holder.appIcon.setOnClickListener(v -> {
            if (app.getStatus() == 1) { // TODO BLOCK THIS APP
                app.setStatus(0);
                holder.appStatus.setImageResource(R.drawable.locked_icon);
                holder.appIcon.setColorFilter(filter);

                // TODO update data
                List<String> unblockedPackages = SharedPrefUtil.getInstance(ctx).getUnblockedAppsList();
                unblockedPackages.remove(app.getPackageName());
                SharedPrefUtil.getInstance(ctx).createUnblockedAppsList(unblockedPackages);
            } else { // TODO UNBLOCK THIS APP
                app.setStatus(1);
                holder.appStatus.setImageResource(0);
                holder.appIcon.clearColorFilter();

                // TODO update data
                List<String> unblockedPackages = SharedPrefUtil.getInstance(ctx).getUnblockedAppsList();
                unblockedPackages.add(app.getPackageName());
                SharedPrefUtil.getInstance(ctx).createUnblockedAppsList(unblockedPackages);
            }
        });
    }
    @Override
    public int getItemCount() {
        return apps.size();
    }
    @Override
    public Filter getFilter() {
        return appListFilter;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<AppModel> newList) {
        apps = new ArrayList<>();
        apps.addAll(newList);
        notifyDataSetChanged();
    }
    public static class adapter_design_backend extends RecyclerView.ViewHolder {
        TextView appName;
        ImageView appIcon, appStatus;

        public adapter_design_backend(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.appname);
            appIcon = itemView.findViewById(R.id.appicon);
            appStatus = itemView.findViewById(R.id.appstatus);
        }
    }
}

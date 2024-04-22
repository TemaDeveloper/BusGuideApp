package com.bus_tours_ex.apps.bustours.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bus_tours_ex.apps.bustours.R;
import com.bus_tours_ex.apps.bustours.models.FilterItem;

import java.util.ArrayList;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.FilterViewHolder>{

    private ArrayList<FilterItem> filterList;
    private Context context;

    public FiltersAdapter(ArrayList<FilterItem> filterList, Context context) {
        this.filterList = filterList;
        this.context = context;
    }

    @NonNull
    @Override
    public FiltersAdapter.FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_item, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FiltersAdapter.FilterViewHolder holder, int position) {
        FilterItem item = filterList.get(position);
        holder.filterName.setText(item.getFilterName());
        holder.filterImage.setImageResource(item.getImage());
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {
        private TextView filterName;
        private ImageView filterImage;
        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);

            filterName = itemView.findViewById(R.id.filter_name);
            filterImage = itemView.findViewById(R.id.filter_image);


        }
    }
}

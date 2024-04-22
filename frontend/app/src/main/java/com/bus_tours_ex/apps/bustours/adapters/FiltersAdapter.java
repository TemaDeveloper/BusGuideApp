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
import com.bus_tours_ex.apps.bustours.models.Trip;
import com.bus_tours_ex.apps.bustours.ui.trips.UpdateTripCategory;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.FilterViewHolder>{

    private ArrayList<FilterItem> filterList;
    private Context context;
    private boolean check = true;
    private boolean select = true;
    private int rowIndex = -1;
    private UpdateTripCategory updateTripCategory;

    public FiltersAdapter(UpdateTripCategory updateTripCategory, ArrayList<FilterItem> filterList, Context context) {
        this.filterList = filterList;
        this.context = context;
        this.updateTripCategory = updateTripCategory;
    }

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

        switch(item.getFilterName()){
            case "New":
                holder.filterImage.setImageDrawable(context.getResources().getDrawable(R.drawable.new_tours_img));
                break;
            case "Popular":
                holder.filterImage.setImageDrawable(context.getResources().getDrawable(R.drawable.popular_tours_img));
                break;
            case "Europe":
                holder.filterImage.setImageDrawable(context.getResources().getDrawable(R.drawable.europe_tours_img));
                break;
            case "For Students":
                holder.filterImage.setImageDrawable(context.getResources().getDrawable(R.drawable.student_tours_img));
                break;
        }

        if(check){
            ArrayList<Trip> trips = new ArrayList<>();
            requestAPI(position, "New", trips);
            check = false;
        }

        holder.categoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowIndex = position;
                notifyDataSetChanged();
                if (position == 0) {
                    ArrayList<Trip> trips = new ArrayList<>();
                    requestAPI(position, "New", trips);
                } else if (position == 1) {
                    ArrayList<Trip> trips = new ArrayList<>();
                    requestAPI(position, "Popular", trips);
                }else if (position == 2) {
                    ArrayList<Trip> trips = new ArrayList<>();
                    requestAPI(position, "Europe", trips);
                }else if (position == 3) {
                    ArrayList<Trip> trips = new ArrayList<>();
                    requestAPI(position, "For Students", trips);
                }
            }
        });
        if (select) {
            if (position == 0) {
                holder.categoryCard.setStrokeColor(context.getResources().getColor(R.color.blue));
                holder.filterName.setTextColor(context.getResources().getColor(R.color.purple_200));
                holder.filterImage.setAlpha(1.f);
                select = false;
            }
        } else {
            if (rowIndex == position) {
                holder.categoryCard.setStrokeColor(context.getResources().getColor(R.color.blue));
                holder.filterName.setTextColor(context.getResources().getColor(R.color.purple_200));
                holder.filterImage.setAlpha(1.f);
            } else {
                holder.categoryCard.setStrokeColor(context.getResources().getColor(android.R.color.darker_gray));
                holder.filterName.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                holder.filterImage.setAlpha(0.5f);
            }
        }

    }

    private void requestAPI(int position, String category, ArrayList<Trip> trips) {
        trips.add(new Trip("Trip to Paris", "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg", 100));
        trips.add(new Trip("Trip to Paris", "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg", 100));
        trips.add(new Trip("Trip to Paris", "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg", 100));
        trips.add(new Trip("Trip to Paris", "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg", 100));
        trips.add(new Trip("Trip to Paris", "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg", 100));
        trips.add(new Trip("Trip to Paris", "https://media.tacdn.com/media/attractions-splice-spp-674x446/07/03/1c/9c.jpg", 100));
        updateTripCategory.callBack(position, trips, category);
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {
        private TextView filterName;
        private ImageView filterImage;
        private MaterialCardView categoryCard;
        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);

            filterName = itemView.findViewById(R.id.filter_name);
            filterImage = itemView.findViewById(R.id.filter_image);
            categoryCard = itemView.findViewById(R.id.category_card);


        }
    }
}
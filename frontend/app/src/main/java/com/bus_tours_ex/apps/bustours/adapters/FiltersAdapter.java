package com.bus_tours_ex.apps.bustours.adapters;

import android.content.Context;
import android.util.Log;
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
import com.bus_tours_ex.apps.bustours.rest.APIClient;
import com.bus_tours_ex.apps.bustours.rest.AllTripResponse;
import com.bus_tours_ex.apps.bustours.rest.ApiInterface;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.FilterViewHolder> {

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

        switch (item.getFilterName()) {
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
                holder.filterImage.setImageDrawable(context.getResources().getDrawable(R.drawable.students_tour_img));
                break;
        }

        if (check) {
            ArrayList<Trip> trips = new ArrayList<>();
            requestAPI("New", trips);
            check = false;
        }

        holder.categoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowIndex = position;
                notifyDataSetChanged();
                if (position == 0) {
                    ArrayList<Trip> trips = new ArrayList<>();
                    requestAPI("New", trips);
                } else if (position == 1) {
                    ArrayList<Trip> trips = new ArrayList<>();
                    requestAPI( "Popular", trips);
                } else if (position == 2) {
                    ArrayList<Trip> trips = new ArrayList<>();
                    requestAPI( "Europe", trips);
                } else if (position == 3) {
                    ArrayList<Trip> trips = new ArrayList<>();
                    requestAPI( "For Students", trips);
                }
            }
        });
        if (select) {
            if (position == 0) {
                holder.categoryCard.setStrokeColor(context.getResources().getColor(R.color.blue));
                holder.filterName.setTextColor(context.getResources().getColor(R.color.blue));
                holder.filterImage.setAlpha(1.f);
                select = false;
            }
        } else {
            if (rowIndex == position) {
                holder.categoryCard.setStrokeColor(context.getResources().getColor(R.color.blue));
                holder.filterName.setTextColor(context.getResources().getColor(R.color.blue));
                holder.filterImage.setAlpha(1.f);
            } else {
                holder.categoryCard.setStrokeColor(context.getResources().getColor(android.R.color.darker_gray));
                holder.filterName.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                holder.filterImage.setAlpha(0.5f);
            }
        }

    }

    private void requestAPI(String category, ArrayList<Trip> trips) {
        ApiInterface apiInterface = APIClient.getApiService();
        Call<AllTripResponse> callIdTrips = apiInterface.getAllTrips();
        callIdTrips.enqueue(new Callback<AllTripResponse>() {
            @Override
            public void onResponse(Call<AllTripResponse> call, Response<AllTripResponse> response) {

                if (response.isSuccessful()) {
                    for (Integer id : response.body().getIds()) {
                        apiInterface
                                .getTrip(id)
                                .enqueue(new Callback<Trip>() {
                                    @Override
                                    public void onResponse(Call<Trip> call, Response<Trip> response) {
                                        synchronized (trips) {
                                            if (response.body().getCategory().equals(category)) {
                                                trips.add(response.body());
                                                updateTripCategory.callBack(trips, category);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Trip> call, Throwable throwable) {

                                    }
                                });
                    }


                }
            }

            @Override
            public void onFailure(Call<AllTripResponse> call, Throwable t) {
                Log.d("FAIL_GETTING_U", t.getMessage());
            }
        });

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
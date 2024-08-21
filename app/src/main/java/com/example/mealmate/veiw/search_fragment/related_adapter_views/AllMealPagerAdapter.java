package com.example.mealmate.veiw.search_fragment.related_adapter_views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.veiw.search_fragment.search_fragment_veiw_interface.HandelSeeMoreClick;

import java.util.List;

public class AllMealPagerAdapter extends RecyclerView.Adapter<AllMealPagerAdapter.MealViewHolder> {
    private List<MealDTO> mealList;
    private Context context;
    private OnMealClickListener onMealClickListener;
    private HandelSeeMoreClick handelSeeMoreClick;
    private static final String TAG = "AllMealPagerAdapter";
    // Constructor
    public AllMealPagerAdapter(Context context, List<MealDTO> mealList, OnMealClickListener onMealClickListener,HandelSeeMoreClick handelSeeMoreClick) {
        this.context = context;
        this.mealList = mealList;
        this.onMealClickListener = onMealClickListener;
        this.handelSeeMoreClick = handelSeeMoreClick;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_recylerview_layout, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealDTO meal = mealList.get(position);
        holder.mealName.setText(meal.getStrMeal());
        Glide.with(context)
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.nophotosign)
                .error(R.drawable.errorloadingimag)
                .centerCrop()
                .into(holder.mealImage);
        holder.itemView.setOnClickListener(v -> {
            if (onMealClickListener != null) {
                onMealClickListener.onMealClick(meal);
            }
        });

        holder.seeMore.setOnClickListener(v -> {
            if (handelSeeMoreClick != null) {
                handelSeeMoreClick.onSeeMoreClick(meal.getIdMeal());
                Log.i(TAG, "onBindViewHolder: "+meal.getIdMeal());

            }
        });

    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    // ViewHolder class
    public static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView mealName;
        Button seeMore;
        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
            mealName = itemView.findViewById(R.id.meal_name);
            seeMore = itemView.findViewById(R.id.seeMorebutton);
        }
    }

    // Custom listener interface
    public interface OnMealClickListener {
        void onMealClick(MealDTO mealDTO);
    }
}

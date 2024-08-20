package com.example.mealmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;

import java.util.List;

public class AllMealPagerAdapter extends RecyclerView.Adapter<AllMealPagerAdapter.MealViewHolder> {
    private List<MealDTO> mealList;
    private Context context;
    private OnMealClickListener onMealClickListener;

    // Constructor
    public AllMealPagerAdapter(Context context, List<MealDTO> mealList, OnMealClickListener onMealClickListener) {
        this.context = context;
        this.mealList = mealList;
        this.onMealClickListener = onMealClickListener;
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
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    // ViewHolder class
    public static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
        }
    }

    // Custom listener interface
    public interface OnMealClickListener {
        void onMealClick(MealDTO mealDTO);
    }
}

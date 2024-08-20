package com.example.mealmate.veiw.home_fragment_veiw.related_adapter_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.model.MealCategory;

import java.util.List;

public class MealCategoriesPagerAdapter extends RecyclerView.Adapter<MealCategoriesPagerAdapter.MealViewHolder> {
    private List<MealCategory> mealList;
    private Context context;
    private OnMealCategoryClickListener onMealCategoryClickListener;

    // Constructor
    public MealCategoriesPagerAdapter(Context context, List<MealCategory> mealList, OnMealCategoryClickListener onMealCategoryClickListener) {
        this.context = context;
        this.mealList = mealList;
        this.onMealCategoryClickListener = onMealCategoryClickListener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_category, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealCategory meal = mealList.get(position);
        Glide.with(context)
                .load(meal.getStrCategoryThumb())
                .placeholder(R.drawable.nophotosign)
                .error(R.drawable.errorloadingimag)
                .centerCrop()
                .into(holder.mealImage);
        holder.itemView.setOnClickListener(v -> {
            if (onMealCategoryClickListener != null) {
                onMealCategoryClickListener.onMealCategoryClick(meal);
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
    public interface OnMealCategoryClickListener {
        void onMealCategoryClick(MealCategory mealCategory);
    }
}

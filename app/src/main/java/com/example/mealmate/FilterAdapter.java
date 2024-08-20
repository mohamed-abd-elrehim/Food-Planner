package com.example.mealmate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.MealIngredient;

import java.util.ArrayList;
import java.util.List;
public class FilterAdapter<T> extends RecyclerView.Adapter<FilterAdapter.MealViewHolder> {
    private List<T> mealList;
    private List<T> filterList;
    private Context context;
    private OnMealClickListener<T> onMealClickListener;
    private static final String TAG = "FilterAdapter";

    public FilterAdapter(Context context, List<T> mealList, OnMealClickListener<T> onMealClickListener) {
        this.context = context;
        this.mealList = mealList;
        this.filterList = new ArrayList<>(mealList);
        this.onMealClickListener = onMealClickListener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_filter_layout, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        T meal = filterList.get(position);
        Log.i(TAG, "onBindViewHolder11: ");
        if (meal instanceof MealCategory) {
            bindMealCategory(holder, (MealCategory) meal);
            Log.i(TAG, "onBindViewHolder: ");
        } else if (meal instanceof MealIngredient) {
            bindMealIngredient(holder, (MealIngredient) meal);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onMealClickListener != null) {
                onMealClickListener.onMealClick(meal);
            }
        });
    }

    private void bindMealCategory(MealViewHolder holder, MealCategory mealCategory) {
        holder.mealName.setText(mealCategory.getStrCategory());
        Glide.with(context)
                .load(mealCategory.getStrCategoryThumb())
                .placeholder(R.drawable.nophotosign)
                .error(R.drawable.errorloadingimag)
                .centerCrop()
                .into(holder.mealImage);
    }

    private void bindMealIngredient(MealViewHolder holder, MealIngredient mealIngredient) {
        holder.mealName.setText(mealIngredient.getStrIngredient());
        Glide.with(context)
                .load("https://www.themealdb.com/images/ingredients/" + mealIngredient.getStrIngredient() + "-Small.png")
                .placeholder(R.drawable.nophotosign)
                .error(R.drawable.errorloadingimag)
                .centerCrop()
                .into(holder.mealImage);
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public void updateFilterList(List<T> newFilterList) {
        Log.i(TAG, "Old filterList size: " + filterList.size());
        Log.i(TAG, "New filterList size: " + newFilterList.size());

        filterList.clear();
        filterList.addAll(newFilterList);

        Log.i(TAG, "Updated filterList size: " + filterList.size());

        // Use notifyDataSetChanged to force rebind all items
        notifyDataSetChanged();
    }


    public static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView mealName;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
            mealName = itemView.findViewById(R.id.mealName);
        }
    }

    public interface OnMealClickListener<T> {
        void onMealClick(T mealDTO);
    }
}

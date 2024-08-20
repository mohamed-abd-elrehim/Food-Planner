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
import com.example.mealmate.model.MealIngredient;

import java.util.List;

public class MealIngredientesPagerAdapter extends RecyclerView.Adapter<MealIngredientesPagerAdapter.MealViewHolder> {
    private List<MealIngredient> mealList;
    private Context context;
    private OnMealIngredientClickListener onMealIngredientClickListener;

    // Constructor
    public MealIngredientesPagerAdapter(Context context, List<MealIngredient> mealList, OnMealIngredientClickListener onMealIngredientClickListener) {
        this.context = context;
        this.mealList = mealList;
        this.onMealIngredientClickListener = onMealIngredientClickListener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_category, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealIngredient meal = mealList.get(position);
        Glide.with(context)
                .load("https://www.themealdb.com/images/ingredients/"+meal.getStrIngredient()+"-Small.png")
                .placeholder(R.drawable.nophotosign)
                .error(R.drawable.errorloadingimag)
                .centerCrop()
                .into(holder.mealImage);
        holder.itemView.setOnClickListener(v -> {
            if (onMealIngredientClickListener != null) {
                onMealIngredientClickListener.onMealIngredientClick(meal);
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
    public interface OnMealIngredientClickListener {
        void onMealIngredientClick(MealIngredient mealIngredient);
    }
}

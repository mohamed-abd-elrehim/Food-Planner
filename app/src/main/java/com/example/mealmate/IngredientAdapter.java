package com.example.mealmate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealMeasureIngredient;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class IngredientAdapter  extends RecyclerView.Adapter<IngredientAdapter.MealViewHolder> {
private  static final String TAG = "IngredientAdapter";
    private Context context;
    private List<MealMeasureIngredient> mealMeasureIngredients;

    public IngredientAdapter(Context context, List<MealMeasureIngredient> mealMeasureIngredients) {
        this.context = context;
        this.mealMeasureIngredients = mealMeasureIngredients;
        Log.i(TAG, "getItemCount: "+ mealMeasureIngredients.size());

    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_meal_detil_mealingredientlayout, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealMeasureIngredient item = mealMeasureIngredients.get(position);
        holder.measureTextView.setText(item.getMeasure());
        holder.ingredientTextView.setText(item.getIngredientName());
        Glide.with(context)
                .load("https://www.themealdb.com/images/ingredients/" + item.getIngredientName() + "-Small.png")
                .placeholder(R.drawable.nophotosign)
                .error(R.drawable.errorloadingimag)
                .centerCrop()
                .into(holder.mealImageView);
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: "+ mealMeasureIngredients.size());

        return mealMeasureIngredients.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mealImageView;
        TextView measureTextView;
        TextView ingredientTextView;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImageView = itemView.findViewById(R.id.mealImage);
            measureTextView = itemView.findViewById(R.id.measureTextView);
            ingredientTextView = itemView.findViewById(R.id.IngredientTextVeiw);
        }
    }
}

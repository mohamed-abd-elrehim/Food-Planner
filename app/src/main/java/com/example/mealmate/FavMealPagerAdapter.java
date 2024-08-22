package com.example.mealmate;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mealmate.model.mealDTOs.CustomMeal;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.favorite_meals.FavoriteMeal;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FavMealPagerAdapter extends RecyclerView.Adapter<FavMealPagerAdapter.MealViewHolder> {
    private List<MealDTO> mealList;
    private Context context;
    private Handel_Delete_Favorites handelDeleteFavorites;
    private HandelSeeMoreClick handelSeeMoreClick;


    public FavMealPagerAdapter(Context context, List<MealDTO> mealList,Handel_Delete_Favorites handelDeleteFavorites,HandelSeeMoreClick handelSeeMoreClick) {

        this.context = context;
        this.mealList = mealList;
        this.handelDeleteFavorites = handelDeleteFavorites;
        this.handelSeeMoreClick = handelSeeMoreClick;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_meals_layout, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealDTO meal = mealList.get(position);
        holder.mealName.setText(meal.getStrMeal());
        holder.mealCategory.setText("Category: " + meal.getStrCategory());
        holder.mealArea.setText("Area: " + meal.getStrArea());

        Glide.with(context)
                .load(meal.getStrMealThumb())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mealImage);


      FavoriteMeal favoriteMeal = new FavoriteMeal(meal.getIdMeal(), FirebaseAuth.getInstance().getCurrentUser().getEmail(),null);
      holder.deleteButton.setOnClickListener(V->handelDeleteFavorites.onDeleteFavoritesClick(favoriteMeal));
      holder.seeMoreButton.setOnClickListener(V->handelSeeMoreClick.onSeeMoreClick(meal));

    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView mealName;

        TextView mealCategory;
        TextView mealArea;

        Button deleteButton;
        Button seeMoreButton;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
            mealName = itemView.findViewById(R.id.meal_name);
            mealCategory = itemView.findViewById(R.id.meal_category);
            mealArea = itemView.findViewById(R.id.meal_area);
            deleteButton = itemView.findViewById(R.id.deletebutton);
            seeMoreButton = itemView.findViewById(R.id.seeMorebutton);

        }
    }

    public void updateMealList(List<MealDTO> newMealList) {
        this.mealList = newMealList;
        notifyDataSetChanged();
    }
}

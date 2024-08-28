package com.example.mealmate.veiw.plan_of_the_week_fragment.related_adpter;

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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mealmate.R;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;
import com.example.mealmate.model.mealDTOs.meal_plan.MealPlan;
import com.example.mealmate.veiw.plan_of_the_week_fragment.plan_of_the_week_fragment_interface.Handel_Delete_Plans;
import com.example.mealmate.veiw.plan_of_the_week_fragment.plan_of_the_week_fragment_interface.PlanHandelSeeMoreClick;
import com.example.mealmate.veiw.search_fragment.search_fragment_veiw_interface.HandelSeeMoreClick;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlanMealPagerAdapter extends RecyclerView.Adapter<PlanMealPagerAdapter.MealViewHolder> {

    private static final String TAG = "PlanMealPagerAdapter";
    private final Context context;
    private final Handel_Delete_Plans handleDeletePlans;
    private final PlanHandelSeeMoreClick handleSeeMoreClick;

    private List<MealDTO> mealList;
    private List<MealPlan> mealPlans;



    public PlanMealPagerAdapter(Context context, List<MealDTO> mealList, List<MealPlan> mealPlans,
                                Handel_Delete_Plans handleDeletePlans, PlanHandelSeeMoreClick handleSeeMoreClick) {
        this.context = context;
        this.mealList = mealList;
        this.mealPlans = mealPlans;
        this.handleDeletePlans = handleDeletePlans;
        this.handleSeeMoreClick = handleSeeMoreClick;





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
        MealPlan mealPlan = mealPlans.get(position);

        holder.mealName.setText(meal.getStrMeal());
        holder.mealCategory.setText("Day: " + mealPlan.getDayOfWeek());
        holder.mealArea.setText("Time: " + mealPlan.getMealType());

        Glide.with(context)
                .load(meal.getStrMealThumb())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mealImage);

        holder.deleteButton.setOnClickListener(v -> handleDeletePlans.onDeletePlansClick(mealPlan));
        holder.seeMoreButton.setOnClickListener(v -> handleSeeMoreClick.onSeeMoreClick(meal, mealPlan));
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


}

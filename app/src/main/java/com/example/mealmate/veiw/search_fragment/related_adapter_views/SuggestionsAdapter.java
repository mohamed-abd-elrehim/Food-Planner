package com.example.mealmate.veiw.search_fragment.related_adapter_views;

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
import com.example.mealmate.R;
import com.example.mealmate.model.MealArea;
import com.example.mealmate.model.MealCategory;
import com.example.mealmate.model.MealIngredient;
import com.example.mealmate.model.mealDTOs.all_meal_details.MealDTO;

import java.util.ArrayList;
import java.util.List;

public class SuggestionsAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SuggestionsAdapter";
    private static final int VIEW_TYPE_SUGGESTION = 0;
    private static final int VIEW_TYPE_HEADER = 1;


    private List<Object> items;
    private Context context;
    private OnMealClickListener<T> onMealClickListener;

    public SuggestionsAdapter(Context context, List<T> items, OnMealClickListener<T> onMealClickListener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.onMealClickListener = onMealClickListener;
        addHeadersAndItems(items);
    }

    private void addHeadersAndItems(List<T> items) {
        String currentHeader = null;
        for (T item : items) {
            String header = getHeaderForItem(item);
            if (header != null && !header.equals(currentHeader)) {
                this.items.add(header); // Add header
                currentHeader = header;
            }
            this.items.add(item); // Add item
        }
    }

    private String getHeaderForItem(T item) {
        if (item instanceof MealDTO) {
            Log.i(TAG, "getHeaderForItem: ");
            return "Meals";
        } else if (item instanceof MealCategory) {
            return "Categories";
        } else if (item instanceof MealIngredient) {
            return "Ingredients";
        } else if (item instanceof MealArea) {
            return "Areas";
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof String) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_SUGGESTION;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SUGGESTION) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
            return new MealViewHolder(view);
        } else { // VIEW_TYPE_HEADER
            View view = LayoutInflater.from(context).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        Log.i(TAG, "onBindViewHolder: position=" + position);

        if (getItemViewType(position) == VIEW_TYPE_SUGGESTION) {
            MealViewHolder mealViewHolder = (MealViewHolder) holder;
            if (item instanceof MealDTO) {
                bindMealDTO(mealViewHolder, (MealDTO) item);
            } else if (item instanceof MealCategory) {
                bindMealCategory(mealViewHolder, (MealCategory) item);
            } else if (item instanceof MealIngredient) {
                bindMealIngredient(mealViewHolder, (MealIngredient) item);
            } else if (item instanceof MealArea) {
                bindMealArea(mealViewHolder, (MealArea) item);
            }

            holder.itemView.setOnClickListener(v -> {
                if (onMealClickListener != null) {
                    onMealClickListener.onMealClick((T) item);
                }
            });

        } else { // VIEW_TYPE_HEADER
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            bindHeader(headerViewHolder, (String) item);
        }
    }

    private void bindMealDTO(MealViewHolder holder, MealDTO mealDTO) {
        holder.titleTextView.setText(mealDTO.getStrMeal());
        Glide.with(context)
                .load(mealDTO.getStrMealThumb())
                .placeholder(R.drawable.nophotosign)
                .error(R.drawable.errorloadingimag)
                .centerCrop()
                .into(holder.iconImageView);
    }

    private void bindMealCategory(MealViewHolder holder, MealCategory mealCategory) {
        holder.titleTextView.setText(mealCategory.getStrCategory());
        Glide.with(context)
                .load(mealCategory.getStrCategoryThumb())
                .placeholder(R.drawable.nophotosign)
                .error(R.drawable.errorloadingimag)
                .centerCrop()
                .into(holder.iconImageView);
    }

    private void bindMealIngredient(MealViewHolder holder, MealIngredient mealIngredient) {
        holder.titleTextView.setText(mealIngredient.getStrIngredient());
        Glide.with(context)
                .load("https://www.themealdb.com/images/ingredients/" + mealIngredient.getStrIngredient() + "-Small.png")
                .placeholder(R.drawable.nophotosign)
                .error(R.drawable.errorloadingimag)
                .centerCrop()
                .into(holder.iconImageView);
    }

    private void bindMealArea(MealViewHolder holder, MealArea mealArea) {
        holder.titleTextView.setText(mealArea.getStrArea());
        String areaName = mealArea.getStrArea().toLowerCase();
        int resourceId = context.getResources().getIdentifier(areaName, "drawable", context.getPackageName());

        Glide.with(context)
                .load(resourceId != 0 ? resourceId : R.drawable.nophotosign)
                .placeholder(R.drawable.nophotosign)
                .error(R.drawable.errorloadingimag)
                .centerCrop()
                .into(holder.iconImageView);
    }

    private void bindHeader(HeaderViewHolder holder, String header) {
        holder.headerTextView.setText(header);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateSuggestions(List<T> newItems) {
        Log.i(TAG, "Old items size: " + items.size());
        Log.i(TAG, "New items size: " + newItems.size());

        items.clear();
        addHeadersAndItems(newItems);

        Log.i(TAG, "Updated items size: " + items.size());

        notifyDataSetChanged();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.headerTitle);
        }
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView iconImageView;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }
    }

    public interface OnMealClickListener<T> {
        void onMealClick(T item);
    }
}

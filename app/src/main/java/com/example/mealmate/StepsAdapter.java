package com.example.mealmate;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.model.Step;

import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {

    private Context context;
    private List<Step> stepsList;

    public StepsAdapter(Context context, List<Step> stepsList) {
        this.context = context;
        this.stepsList = stepsList;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(context).inflate(R.layout.all_meal_detil_steps_layout, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        // Get the current step
        Step step = stepsList.get(position);

        // Bind the data to the views
        holder.stepTitleTextView.setText(step.getTitle());
        holder.stepBodyTextView.setText(step.getInstruction());
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView stepTitleTextView;
        TextView stepBodyTextView;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            stepTitleTextView = itemView.findViewById(R.id.stepTitilTextView);
            stepBodyTextView = itemView.findViewById(R.id.step_body_textView8);
        }
    }
}

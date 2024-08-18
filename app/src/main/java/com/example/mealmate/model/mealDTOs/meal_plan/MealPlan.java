package com.example.mealmate.model.mealDTOs.meal_plan;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ColumnInfo;

@Entity(primaryKeys = {"meal_id", "client_email"})
public class MealPlan {
    @NonNull
    @ColumnInfo(name = "meal_id")
    private String mealId;
    @NonNull
    @ColumnInfo(name = "client_email")
    private String clientEmail;

    @ColumnInfo(name = "day_of_week")
    private String dayOfWeek;

    @ColumnInfo(name = "meal_type")
    private String mealType;

    @ColumnInfo(name = "date")
    private String date;

    public MealPlan(String mealId, String clientEmail, String dayOfWeek, String mealType, String date) {
        this.mealId = mealId;
        this.clientEmail = clientEmail;
        this.dayOfWeek = dayOfWeek;
        this.mealType = mealType;
        this.date = date;
    }
// Getters and setters...

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

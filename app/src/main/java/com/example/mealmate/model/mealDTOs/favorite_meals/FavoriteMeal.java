package com.example.mealmate.model.mealDTOs.favorite_meals;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ColumnInfo;

@Entity(primaryKeys = {"meal_id", "client_email"})
public class FavoriteMeal {
    @NonNull
    @ColumnInfo(name = "meal_id")
    private String mealId;
    @NonNull
    @ColumnInfo(name = "client_email")
    private String clientEmail;

    @ColumnInfo(name = "added_date")
    private String addedDate;

    public FavoriteMeal(String mealId, String clientEmail, String addedDate) {
        this.mealId = mealId;
        this.clientEmail = clientEmail;
        this.addedDate = addedDate;
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

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }
}

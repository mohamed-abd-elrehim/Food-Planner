package com.example.mealmate.model.mealDTOs.meal_plan;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ColumnInfo;

@Entity(primaryKeys = {"meal_id", "client_email"})
public class MealPlan implements Parcelable {
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

    // Constructor
    public MealPlan(String mealId, String clientEmail, String dayOfWeek, String mealType, String date) {
        this.mealId = mealId;
        this.clientEmail = clientEmail;
        this.dayOfWeek = dayOfWeek;
        this.mealType = mealType;
        this.date = date;
    }

    // Parcelable implementation
    protected MealPlan(Parcel in) {
        mealId = in.readString();
        clientEmail = in.readString();
        dayOfWeek = in.readString();
        mealType = in.readString();
        date = in.readString();
    }

    public static final Creator<MealPlan> CREATOR = new Creator<MealPlan>() {
        @Override
        public MealPlan createFromParcel(Parcel in) {
            return new MealPlan(in);
        }

        @Override
        public MealPlan[] newArray(int size) {
            return new MealPlan[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mealId);
        dest.writeString(clientEmail);
        dest.writeString(dayOfWeek);
        dest.writeString(mealType);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
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

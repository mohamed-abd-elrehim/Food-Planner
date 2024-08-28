package com.example.mealmate.model.mealDTOs.all_meal_details;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Relation;

import java.io.Serializable;

@Entity
public class MealDTO implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;


    public String getIdMeal() {
        return idMeal;
    }

    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }

    @ColumnInfo(name = "meal_Type")
    public String strType;

    @ColumnInfo(name = "meal_category")
    public String strCategory;
    @ColumnInfo(name = "str_imageSource")
    public String strImageSource;
    @ColumnInfo(name = "str_creativeCommonsConfirmed")
    public String strCreativeCommonsConfirmed;
    @ColumnInfo(name = "date_modified")
    public String dateModified;
    @ColumnInfo(name = "meal_id")
    private String idMeal;

    @ColumnInfo(name = "str_meal")
    private String strMeal;

    @ColumnInfo(name = "str_drink_alternate")
    private String strDrinkAlternate;

    @ColumnInfo(name = "str_area")
    private String strArea;

    @ColumnInfo(name = "str_instructions")
    private String strInstructions;

    @ColumnInfo(name = "str_meal_thumb")
    private String strMealThumb;

    @ColumnInfo(name = "str_tags")
    private String strTags;

    @ColumnInfo(name = "str_youtube")
    private String strYoutube;

    @ColumnInfo(name = "str_source")
    private String strSource;

    public MealDTO(String strType,String strCategory, String strImageSource, String strCreativeCommonsConfirmed,
                   String dateModified, String idMeal, String strMeal, String strDrinkAlternate,
                   String strArea, String strInstructions, String strMealThumb, String strTags,
                   String strYoutube, String strSource) {

        this.strCategory = strCategory;
        this.strType = strType;

        this.strImageSource = strImageSource;
        this.strCreativeCommonsConfirmed = strCreativeCommonsConfirmed;
        this.dateModified = dateModified;
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strDrinkAlternate = strDrinkAlternate;
        this.strArea = strArea;
        this.strInstructions = strInstructions;
        this.strMealThumb = strMealThumb;
        this.strTags = strTags;
        this.strYoutube = strYoutube;
        this.strSource = strSource;
    }
// Getters and setters...

    public String getStrType() {
        return strType;
    }

    public void setStrType(String strType) {
        this.strType = strType;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public String getStrImageSource() {
        return strImageSource;
    }

    public void setStrImageSource(String strImageSource) {
        this.strImageSource = strImageSource;
    }

    public String getStrCreativeCommonsConfirmed() {
        return strCreativeCommonsConfirmed;
    }

    public void setStrCreativeCommonsConfirmed(String strCreativeCommonsConfirmed) {
        this.strCreativeCommonsConfirmed = strCreativeCommonsConfirmed;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getStrMeal() {
        return strMeal;
    }

    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }

    public String getStrDrinkAlternate() {
        return strDrinkAlternate;
    }

    public void setStrDrinkAlternate(String strDrinkAlternate) {
        this.strDrinkAlternate = strDrinkAlternate;
    }

    public String getStrArea() {
        return strArea;
    }

    public void setStrArea(String strArea) {
        this.strArea = strArea;
    }

    public String getStrInstructions() {
        return strInstructions;
    }

    public void setStrInstructions(String strInstructions) {
        this.strInstructions = strInstructions;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }

    public String getStrTags() {
        return strTags;
    }

    public void setStrTags(String strTags) {
        this.strTags = strTags;
    }

    public String getStrYoutube() {
        return strYoutube;
    }

    public void setStrYoutube(String strYoutube) {
        this.strYoutube = strYoutube;
    }

    public String getStrSource() {
        return strSource;
    }

    public void setStrSource(String strSource) {
        this.strSource = strSource;
    }
}

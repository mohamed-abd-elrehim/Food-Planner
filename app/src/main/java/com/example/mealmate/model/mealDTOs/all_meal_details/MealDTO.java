package com.example.mealmate.model.mealDTOs.all_meal_details;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Relation;

@Entity
public class MealDTO {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    public String getIdMeal() {
        return idMeal;
    }

    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }

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

    public MealDTO(int id, String idMeal, String strMeal, String strDrinkAlternate, String strArea, String strInstructions, String strMealThumb, String strTags, String strYoutube, String strSource) {
        this.id = id;
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

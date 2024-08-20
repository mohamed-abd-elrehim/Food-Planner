package com.example.mealmate;

public interface Search_Fragment_Presenter_Interface {
    void loadAllCategoriess();
    void loadAllIngredient();
//    void loadAllArea();

    void loadFilteredCategoriess(String categoryName);
    void loadFilteredIngredient(String ingredientName);
    //void loadFilteredArea(String areaName);
  }

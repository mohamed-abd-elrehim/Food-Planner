package com.example.mealmate.presenter.search_fragment_presenter.search_fragment_presenter_interface;

public interface Search_Fragment_Presenter_Interface {
    void loadAllCategoriess();
    void loadAllIngredient();
    void loadAllArea();

    void loadFilteredCategoriess(String categoryName);
    void loadFilteredIngredient(String ingredientName);
    void loadFilteredArea(String areaName);

    void loadFilteredByName(String name);
    void seeMore(String Id);
  }

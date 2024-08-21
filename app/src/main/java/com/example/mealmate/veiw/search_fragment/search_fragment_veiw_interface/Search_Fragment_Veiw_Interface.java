package com.example.mealmate.veiw.search_fragment.search_fragment_veiw_interface;

import java.util.List;

public interface Search_Fragment_Veiw_Interface<T> {
    void showData(List<T> data);
    void showError(String errorMessage);
}

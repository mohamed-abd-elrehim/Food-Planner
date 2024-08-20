package com.example.mealmate.veiw.home_fragment_veiw.home_fragment_veiw_interface;

import java.util.List;

public interface HomeFragmentView<T> {
    void showData(List<T> data);
    void showError(String errorMessage);
}

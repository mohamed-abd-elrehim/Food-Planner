package com.example.mealmate.veiw.home_activity.home_activity_interface;

public interface Home_Activity_Interface {
    void updateProfile(String name, String email);
    void onSuccess(String message);
    void onFailure(String message);


}

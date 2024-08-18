package com.example.mealmate.model.network.network_Interface;

public interface RemoteDataSource {
    <T> void makeNetworkCallback(NetworkCallback<T> networkCallback, String endpoint, String... params);
    void updateBaseUrl(String newBaseUrl);
}

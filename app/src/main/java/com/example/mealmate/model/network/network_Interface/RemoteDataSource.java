package com.example.mealmate.model.network.network_Interface;

public interface RemoteDataSource {
    void makeNetworkCallback(NetworkCallback networkCallback, String endpoint, String... params);
    void updateBaseUrl(String newBaseUrl);
}

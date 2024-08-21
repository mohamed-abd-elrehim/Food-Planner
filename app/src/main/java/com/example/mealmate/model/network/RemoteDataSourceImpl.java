package com.example.mealmate.model.network;

import android.util.Log;

import com.example.mealmate.model.network.network_Interface.MealService;
import com.example.mealmate.model.network.network_Interface.NetworkCallback;
import com.example.mealmate.model.network.network_Interface.RemoteDataSource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RemoteDataSourceImpl implements RemoteDataSource {
    private static RemoteDataSourceImpl instance;
    private MealService mealService;
    private String BASE_URL;
    private static final String TAG = "RemoteDataSourceImpl";

    // Private constructor
    private RemoteDataSourceImpl() {
        // Default constructor
    }

    // Singleton pattern without BASE_URL in getInstance
    public static RemoteDataSourceImpl getInstance() {
        if (instance == null) {
            instance = new RemoteDataSourceImpl();
        }
        return instance;
    }

    // Initialize Retrofit with the provided BASE_URL
    private void createRetrofit() {
        if (BASE_URL == null || BASE_URL.isEmpty()) {
            throw new IllegalStateException("BASE_URL must be set before initializing Retrofit");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mealService = retrofit.create(MealService.class);
    }

    // Method to set the BASE_URL and initialize Retrofit
    public void setBaseUrl(String baseUrl) {
        if (this.BASE_URL == null || !this.BASE_URL.equals(baseUrl)) {
            this.BASE_URL = baseUrl;
            createRetrofit();
        }
    }


    // Method to update the BASE_URL (if needed)
    @Override
    public void updateBaseUrl(String newBaseUrl) {
        setBaseUrl(newBaseUrl);
    }

    @Override
    public <T> void makeNetworkCallback(NetworkCallback<T> networkCallback, String endpoint, String... params) {
        // Ensure Retrofit is initialized
        if (mealService == null) {
            throw new IllegalStateException("Retrofit has not been initialized. Please set the BASE_URL first.");
        }

        Call<T> call = null;

        switch (endpoint) {
            case "randomMeal":
                call = (Call<T>) mealService.getRandomMeal();
                break;
            case "searchMealByName":
                call = (Call<T>) mealService.searchMealByName(params[0]);
                break;
            case "listMealsByFirstLetter":
                call = (Call<T>) mealService.listMealsByFirstLetter(params[0]);
                break;
            case "lookupMealById":
                call = (Call<T>) mealService.lookupMealById(params[0]);
                break;
            case "listAllCategories":
                call = (Call<T>) mealService.listAllCategories();
                break;
            case "listAll":
                call = (Call<T>) mealService.listAll(params[0]);
                break;
            case "filterByIngredient":
                call = (Call<T>) mealService.filterByIngredient(params[0]);
                break;
            case "filterByCategory":
                call = (Call<T>) mealService.filterByCategory(params[0]);
                break;
            case "filterByArea":
                call = (Call<T>) mealService.filterByArea(params[0]);
                break;
            case "listAllIngredients":
                call = (Call<T>) mealService.listAllIngredients();
                break;
            case "listAllAreas":
                call = (Call<T>) mealService.listAllAreas();
                break;
            case "lookupAllMealDitailsById":
                call = (Call<T>) mealService.lookupAllMealDitailsById(params[0]);
                break;
            case "lookupAllMealDitailsByName":
                call = (Call<T>) mealService.lookupAllMealDitailsByName(params[0]);
                break;

            default:
                throw new IllegalArgumentException("Invalid endpoint: " + endpoint);
        }

        if (call != null) {
            Log.i(TAG, "makeNetworkCallback: "+call);
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        networkCallback.onSuccessResult(response);
                    } else {
                        networkCallback.onFailureResult("Error: Response is unsuccessful or empty");
                    }
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    Log.i(TAG, "onFailure: CallBack");
                    networkCallback.onFailureResult(t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

}
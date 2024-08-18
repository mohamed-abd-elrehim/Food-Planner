package com.example.mealmate.model.MealRepository;

import com.example.mealmate.model.database.local_data_source.local_data_source_interface.LocalDataSource;
import com.example.mealmate.model.network.network_Interface.RemoteDataSource;

public interface MealRepositoryInterface extends LocalDataSource, RemoteDataSource {

}
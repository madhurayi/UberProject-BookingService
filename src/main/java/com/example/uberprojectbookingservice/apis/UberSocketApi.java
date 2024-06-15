package com.example.uberprojectbookingservice.apis;

import com.example.uberprojectbookingservice.dto.DriverLocationDto;
import com.example.uberprojectbookingservice.dto.NearbyDriversRequestDto;
import com.example.uberprojectbookingservice.dto.RideRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UberSocketApi {
    @POST("/api/socket/newride")
    Call<Boolean> raiseRideRequest(@Body RideRequestDto requestDto);

}

package com.example.uberprojectbookingservice.apis;

import com.example.uberprojectbookingservice.dto.DriverLocationDto;
import com.example.uberprojectbookingservice.dto.NearbyDriversRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LocationServiceApi {

    @POST("/api/v1/location/nearby/drivers")
    Call<DriverLocationDto[]> getNearbyDrivers(@Body NearbyDriversRequestDto requestDto);

}

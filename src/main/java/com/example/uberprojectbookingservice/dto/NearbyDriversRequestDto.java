package com.example.uberprojectbookingservice.dto;

import com.example.uberprojectentityservice.models.CarType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NearbyDriversRequestDto {
    Double latitude;
    Double longitude;
    CarType vehicleType;
}

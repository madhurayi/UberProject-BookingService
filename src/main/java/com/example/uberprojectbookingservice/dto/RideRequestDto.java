package com.example.uberprojectbookingservice.dto;

import com.example.uberprojectentityservice.models.ExactLocation;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestDto {
    private Long passengerId;
    private List<Long> driverIds;
    private Long bookingId;
    private ExactLocation startLocation;
    private ExactLocation endLocation;
}

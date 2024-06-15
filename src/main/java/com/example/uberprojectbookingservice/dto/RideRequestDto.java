package com.example.uberprojectbookingservice.dto;

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

}

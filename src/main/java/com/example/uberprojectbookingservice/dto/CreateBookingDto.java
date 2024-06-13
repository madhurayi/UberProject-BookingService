package com.example.uberprojectbookingservice.dto;

import com.example.uberprojectentityservice.models.ExactLocation;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {
    private long passengerId;
    private ExactLocation startLocation;
    private ExactLocation endLocation;
}

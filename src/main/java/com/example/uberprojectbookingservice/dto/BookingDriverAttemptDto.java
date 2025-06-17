package com.example.uberprojectbookingservice.dto;

import com.example.uberprojectentityservice.models.AttemptStatus;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDriverAttemptDto {
    private String bookingId;
    private Long driverId;
    private AttemptStatus status;
    private Date timestamp;
    private String reason;
}

package com.example.uberprojectbookingservice.dto;

import com.example.uberprojectentityservice.models.Driver;
import lombok.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingResponseDto {
    private String bookingId;
    private String bookingStatus;
    private Optional<Driver> driver;
}

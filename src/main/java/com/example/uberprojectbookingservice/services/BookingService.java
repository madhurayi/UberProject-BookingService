package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.dto.CreateBookingDto;
import com.example.uberprojectbookingservice.dto.CreateBookingResponseDto;
import com.example.uberprojectbookingservice.dto.UpdateBookingRequestDto;
import com.example.uberprojectbookingservice.dto.UpdateBookingResponseDto;

public interface BookingService {
    public CreateBookingResponseDto createBooking(CreateBookingDto requestDto);

    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto requestDto,Long BookingId);
}

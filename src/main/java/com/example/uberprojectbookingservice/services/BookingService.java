package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.dto.*;

public interface BookingService {
    public CreateBookingResponseDto createBooking(CreateBookingDto requestDto);

    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto requestDto,Long BookingId);

    public void saveBookingAttempt(BookingDriverAttemptDto attemptDto);
}

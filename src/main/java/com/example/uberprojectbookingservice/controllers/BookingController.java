package com.example.uberprojectbookingservice.controllers;

import com.example.uberprojectbookingservice.dto.CreateBookingDto;
import com.example.uberprojectbookingservice.dto.CreateBookingResponseDto;
import com.example.uberprojectbookingservice.dto.UpdateBookingRequestDto;
import com.example.uberprojectbookingservice.dto.UpdateBookingResponseDto;
import com.example.uberprojectbookingservice.services.BookingService;
import com.example.uberprojectentityservice.models.Booking;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("/api/v1/booking")
public class BookingController  {
    private BookingService  bookingService;
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponseDto> createBooking(@RequestBody CreateBookingDto createBookingDto) {
        System.out.println("controller eneterd");
        CreateBookingResponseDto res=bookingService.createBooking(createBookingDto);
        System.out.println("response dto");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
    @PatchMapping("/{bookingId}")
    public ResponseEntity<UpdateBookingResponseDto> updateBooking(@RequestBody UpdateBookingRequestDto updateBookingRequestDto,@PathVariable long bookingId) {
        return ResponseEntity.ok(bookingService.updateBooking(updateBookingRequestDto,bookingId));
    }

}

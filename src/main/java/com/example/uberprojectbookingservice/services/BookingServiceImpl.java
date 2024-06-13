package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.dto.CreateBookingDto;
import com.example.uberprojectbookingservice.dto.CreateBookingResponseDto;
import com.example.uberprojectbookingservice.dto.DriverLocationDto;
import com.example.uberprojectbookingservice.dto.NearbyDriversRequestDto;
import com.example.uberprojectbookingservice.repositories.BookingRepository;
import com.example.uberprojectbookingservice.repositories.PassengerRepository;
import com.example.uberprojectentityservice.models.Booking;
import com.example.uberprojectentityservice.models.BookingStatus;
import com.example.uberprojectentityservice.models.Passenger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class BookingServiceImpl implements BookingService{
        private BookingRepository bookingRepository;
        private PassengerRepository passengerRepository;
        private RestTemplate restTemplate;
        private static final String LOCATION_SERVICE="http://localhost:3005/api/v1/location/nearbydrivers";

        public BookingServiceImpl(BookingRepository bookingRepository, PassengerRepository passengerRepository) {
            this.bookingRepository = bookingRepository;
            this.passengerRepository = passengerRepository;
            this.restTemplate = new RestTemplate();

        }


        @Override
        public CreateBookingResponseDto createBooking(CreateBookingDto createBookingDto) {
            System.out.println("service entered");
                Optional<Passenger> passenger=passengerRepository.findById(createBookingDto.getPassengerId());
                Booking booking = Booking.builder()
                                .passenger(passenger.get())
                                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                                .startLocation(createBookingDto.getStartLocation())
                                .endLocation(createBookingDto.getEndLocation())
                                .build();

               Booking newBooking= bookingRepository.save(booking);
            System.out.println("nearby drivers created");
               //make an api call to location service to fetch nearby drivers , we can do this using retrofit or resttemplate

                NearbyDriversRequestDto dto=NearbyDriversRequestDto.builder()
                        .latitude(booking.getStartLocation().getLatitude())
                        .longitude(booking.getStartLocation().getLongitude())
                        .build();

            System.out.println("nearby drivers created2");
                ResponseEntity<DriverLocationDto[]> result= restTemplate.postForEntity(LOCATION_SERVICE,dto,DriverLocationDto[].class);
                if(result.getStatusCode().is2xxSuccessful() && result.getBody().length>0){
                        List<DriverLocationDto> driverLocations= Arrays.asList(result.getBody());
                        driverLocations.forEach(DriverLocationDto->{
                                System.out.println(DriverLocationDto.getDriverId()+"-"+DriverLocationDto.getLatitude()+"-"+DriverLocationDto.getLongitude());
                        });
                }


                return CreateBookingResponseDto.builder()
                        .bookingId(String.valueOf(newBooking.getId()))
                        .bookingStatus(String.valueOf(newBooking.getBookingStatus()))
//                        .driver(Optional.ofNullable(newBooking.getDriver()))
                        .build();
        }
}

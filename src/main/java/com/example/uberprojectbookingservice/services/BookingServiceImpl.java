package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.apis.LocationServiceApi;
import com.example.uberprojectbookingservice.dto.*;
import com.example.uberprojectbookingservice.repositories.BookingRepository;
import com.example.uberprojectbookingservice.repositories.DriverRepository;
import com.example.uberprojectbookingservice.repositories.PassengerRepository;
import com.example.uberprojectentityservice.models.Booking;
import com.example.uberprojectentityservice.models.BookingStatus;
import com.example.uberprojectentityservice.models.Driver;
import com.example.uberprojectentityservice.models.Passenger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

@Service
public class BookingServiceImpl implements BookingService{
    private final DriverRepository driverRepository;
    private BookingRepository bookingRepository;
        private PassengerRepository passengerRepository;
        private RestTemplate restTemplate;
//        private static final String LOCATION_SERVICE="http://localhost:3005/api/v1/location/nearbydrivers";
        private LocationServiceApi locationServiceApi;
        private DriverRepository DriverRepository;

        public BookingServiceImpl(BookingRepository bookingRepository, PassengerRepository passengerRepository,
                                  LocationServiceApi locationServiceApi, DriverRepository driverRepository) {
            this.bookingRepository = bookingRepository;
            this.passengerRepository = passengerRepository;
            this.restTemplate = new RestTemplate();
            this.locationServiceApi = locationServiceApi;
            this.DriverRepository = driverRepository;
            this.driverRepository = driverRepository;
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
//            System.out.println("nearby drivers created");
               //make an api call to location service to fetch nearby drivers , we can do this using retrofit or resttemplate

                NearbyDriversRequestDto dto=NearbyDriversRequestDto.builder()
                        .latitude(booking.getStartLocation().getLatitude())
                        .longitude(booking.getStartLocation().getLongitude())
                        .build();

//            System.out.println(dto.getLatitude());
//                ResponseEntity<DriverLocationDto[]> result= restTemplate.postForEntity(LOCATION_SERVICE,dto,DriverLocationDto[].class);
//                if(result.getStatusCode().is2xxSuccessful() && result.getBody().length>0){
//                        List<DriverLocationDto> driverLocations= Arrays.asList(result.getBody());
//                        driverLocations.forEach(DriverLocationDto->{
//                                System.out.println(DriverLocationDto.getDriverId()+"-"+DriverLocationDto.getLatitude()+"-"+DriverLocationDto.getLongitude());
//                        });
//                }

                processNearbyDriverAsync(dto);
                return CreateBookingResponseDto.builder()
                        .bookingId(String.valueOf(newBooking.getId()))
                        .bookingStatus(String.valueOf(newBooking.getBookingStatus()))
//                        .driver(Optional.ofNullable(newBooking.getDriver()))
                        .build();
        }

    @Override
    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto requestDto, Long BookingId) {
       Optional<Driver> driver=driverRepository.findById(requestDto.getDriverId().get());
       bookingRepository.updateBookingStatusAndDriverById(BookingId,BookingStatus.SCHEDULED,driver.get());
       Optional<Booking> booking=bookingRepository.findById(BookingId);
        System.out.println(BookingId);
        return UpdateBookingResponseDto.builder()
                .bookingId(BookingId)
                .status(booking.get().getBookingStatus())
                .driver(Optional.ofNullable(booking.get().getDriver()))
                .build();
    }


    public void processNearbyDriverAsync(NearbyDriversRequestDto nearbyDriversRequestDto) {
//            System.out.println("service entered"+nearbyDriversRequestDto.getLatitude());
            Call<DriverLocationDto[]> call= locationServiceApi.getNearbyDrivers(nearbyDriversRequestDto);
            System.out.println(call.clone());
            call.enqueue(new Callback<DriverLocationDto[]>() {

                @Override
                public void onResponse(Call<DriverLocationDto[]> call, Response<DriverLocationDto[]> response) {
//                    System.out.println("response"+response.body());
                if(response.isSuccessful() && response.body()!=null){
//                    System.out.println("onresponse entered");
                        List<DriverLocationDto> driverLocations= Arrays.asList(response.body());
                        driverLocations.forEach(DriverLocationDto->{
                                System.out.println(DriverLocationDto.getDriverId()+"-"+DriverLocationDto.getLatitude()+"-"+DriverLocationDto.getLongitude());
                        });
                }
                }

                @Override
                public void onFailure(Call<DriverLocationDto[]> call, Throwable throwable) {
                    System.out.println("hiii");
                        throwable.printStackTrace();
                }
            });
        }
}

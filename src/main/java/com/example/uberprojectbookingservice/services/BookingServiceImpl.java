package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.apis.LocationServiceApi;
import com.example.uberprojectbookingservice.apis.UberSocketApi;
import com.example.uberprojectbookingservice.dto.*;
import com.example.uberprojectbookingservice.repositories.*;
import com.example.uberprojectentityservice.models.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.awt.print.Book;
import java.io.IOException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService{
    private final DriverRepository driverRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final BookingDriverAttemptRepository attemptRepository;

    private RestTemplate restTemplate;
//        private static final String LOCATION_SERVICE="http://localhost:3005/api/v1/location/nearbydrivers";
        private LocationServiceApi locationServiceApi;
        private DriverRepository DriverRepository;
        private UberSocketApi uberSocketApi;
        public BookingServiceImpl(BookingRepository bookingRepository, PassengerRepository passengerRepository,
                                  LocationServiceApi locationServiceApi, DriverRepository driverRepository,
                                  UberSocketApi uberSocketApi, UserRepository userRepository,BookingDriverAttemptRepository attemptRepository) {
            this.bookingRepository = bookingRepository;
            this.passengerRepository = passengerRepository;
            this.restTemplate = new RestTemplate();
            this.locationServiceApi = locationServiceApi;
            this.DriverRepository = driverRepository;
            this.driverRepository = driverRepository;
            this.uberSocketApi = uberSocketApi;
            this.userRepository = userRepository;
            this.attemptRepository = attemptRepository;
        }
    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto createBookingDto) {
        System.out.println("service entered");
            Optional<User> user=userRepository.findById(createBookingDto.getPassengerId());
            Optional<Passenger> passenger= passengerRepository.findByUser(user);
            Booking booking = Booking.builder()
                            .passenger(passenger.get())
                            .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                            .startLocation(createBookingDto.getStartLocation())
                            .endLocation(createBookingDto.getEndLocation())
                            .vehicleType(createBookingDto.getVehicleType())
                            .totalDistance(Long.valueOf(createBookingDto.getTotalDistance()))
                            .build();

           Booking newBooking= bookingRepository.save(booking);
//            System.out.println("nearby drivers created");
           //make an api call to location service to fetch nearby drivers , we can do this using retrofit or resttemplate

            NearbyDriversRequestDto dto=NearbyDriversRequestDto.builder()
                    .latitude(booking.getStartLocation().getLatitude())
                    .longitude(booking.getStartLocation().getLongitude())
                    .vehicleType(booking.getVehicleType())
                    .build();

//            System.out.println(dto.getLatitude());
//                ResponseEntity<DriverLocationDto[]> result= restTemplate.postForEntity(LOCATION_SERVICE,dto,DriverLocationDto[].class);
//                if(result.getStatusCode().is2xxSuccessful() && result.getBody().length>0){
//                        List<DriverLocationDto> driverLocations= Arrays.asList(result.getBody());
//                        driverLocations.forEach(DriverLocationDto->{
//                                System.out.println(DriverLocationDto.getDriverId()+"-"+DriverLocationDto.getLatitude()+"-"+DriverLocationDto.getLongitude());
//                        });
//                }

            processNearbyDriverAsync(dto,createBookingDto.getPassengerId(),newBooking.getId(),createBookingDto);
            return CreateBookingResponseDto.builder()
                    .bookingId(String.valueOf(newBooking.getId()))
                    .bookingStatus(String.valueOf(newBooking.getBookingStatus()))
//                        .driver(Optional.ofNullable(newBooking.getDriver()))
                    .build();
    }

    @Override
    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto requestDto, Long bookingId) {
        Optional<User> user = userRepository.findById(requestDto.getDriverId());

        if (user.isEmpty()) {
            throw new RuntimeException("User not found for driverId: " + requestDto.getDriverId());
        }

        Optional<Driver> driver = driverRepository.findByUser(user);
        if (driver.isEmpty()) {
            throw new RuntimeException("Driver not found for userId: " + user.get().getId());
        }

        System.out.println("service entered " + bookingId + " - " + BookingStatus.valueOf(requestDto.getStatus()) + " - " + driver.get().getUser().getId());

        bookingRepository.updateBookingStatusAndDriverById(bookingId, BookingStatus.valueOf(requestDto.getStatus()), driver.get());

        System.out.println("done");

        Optional<Booking> booking = bookingRepository.findById(bookingId);

        return UpdateBookingResponseDto.builder()
                .bookingId(bookingId)
                .status(booking.get().getBookingStatus())
                .driver(Optional.ofNullable(booking.get().getDriver()))
                .build();
    }

    @Override
    public void saveBookingAttempt(BookingDriverAttemptDto attemptDto) {
            BookingDriverAttempt bookingDriverAttempt = new BookingDriverAttempt();
            Booking booking=bookingRepository.getBookingsById(Long.valueOf(attemptDto.getBookingId()));
            Optional<User> user= userRepository.findById(attemptDto.getDriverId());
            Optional<Driver> driver=driverRepository.findByUser(user);
            bookingDriverAttempt.setBooking(booking);
            bookingDriverAttempt.setDriver(driver.get());
            bookingDriverAttempt.setStatus(attemptDto.getStatus());
            attemptRepository.save(bookingDriverAttempt);
    }


    public void processNearbyDriverAsync(NearbyDriversRequestDto nearbyDriversRequestDto,Long passengerId,Long bookingId,CreateBookingDto createBookingDto) {
            System.out.println("service entered"+nearbyDriversRequestDto.getLatitude());
            Call<DriverLocationDto[]> call= locationServiceApi.getNearbyDrivers(nearbyDriversRequestDto);
            System.out.println(call.clone()+"called locationserviceapi");
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
                        try{
                            System.out.println("calling socket server");
                            List<Long> driverIds= driverLocations.stream()
                                    .map(dto -> Long.parseLong(dto.getDriverId())).collect(Collectors.toList());
                            System.out.println("calling socket server"+"-"+driverIds.stream().map(dto->dto).collect(Collectors.toList()));
                            raiseRaidRequestAsync(RideRequestDto.builder().passengerId(passengerId).driverIds(driverIds).bookingId(bookingId).startLocation(createBookingDto.getStartLocation()).endLocation(createBookingDto.getEndLocation()).build());
                        }catch (IOException e){
                            throw new RuntimeException(e);
                        }
                }
                }

                @Override
                public void onFailure(Call<DriverLocationDto[]> call, Throwable throwable) {
                    System.out.println("hiii");
                        throwable.printStackTrace();
                }
            });
        }

        public void raiseRaidRequestAsync(RideRequestDto rideRequestDto) throws IOException{
            Call<Boolean> call=uberSocketApi.raiseRideRequest(rideRequestDto);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if(response.isSuccessful() && response.body()!=null){
                        Boolean status=response.body();
                        System.out.println("Driver response is"+status.toString());
                    }else{
                        System.out.println("Request for ride failed"+response.message());
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable throwable) {
                        throwable.printStackTrace();
                }
            });
        }
}

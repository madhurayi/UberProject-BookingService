package com.example.uberprojectbookingservice.repositories;

import com.example.uberprojectentityservice.models.Booking;
import com.example.uberprojectentityservice.models.BookingStatus;
import com.example.uberprojectentityservice.models.Driver;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

        Booking getBookingsById(Long bookingId);

        @Modifying
        @Transactional
        @Query("UPDATE Booking b SET b.bookingStatus= :status, b.driver= :driver WHERE b.id=:BookingId")
        void updateBookingStatusAndDriverById(@Param("BookingId") Long BookingId, @Param("status") BookingStatus status, @Param("driver") Driver driver);
}
package com.example.uberprojectbookingservice.repositories;

import com.example.uberprojectentityservice.models.Passenger;
import com.example.uberprojectentityservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByUser(Optional<User> user);

}

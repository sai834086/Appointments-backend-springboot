package com.appointments.booking.appointments.repository.user;

import com.appointments.booking.appointments.model.appuser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser,Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<AppUser> findByEmailOrPhoneNumber(String email, String phoneNumber);
    Optional<AppUser> findByEmail(String email);

}

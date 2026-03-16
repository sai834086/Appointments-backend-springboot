package com.appointments.booking.appointments.repository.patner;

import com.appointments.booking.appointments.model.patner.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    Property findByPropertyName(String propertyName);

    // Path: Property -> partnerUser -> appUser -> userId
    List<Property> findByPartnerUser_AppUser_UserId(Long ownerId);

    // Path: Property -> partnerUser -> appUser -> userId
    Optional<Property> findByPropertyIdAndPartnerUser_AppUser_UserId(Long propertyId, Long userId);
}
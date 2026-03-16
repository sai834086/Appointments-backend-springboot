package com.appointments.booking.appointments.serviceimpl.patner;

import com.appointments.booking.appointments.exception.UnauthorizedAccessOrUnknownException;
import com.appointments.booking.appointments.mapStruct.patner.AvailabilityMapStruct;
import com.appointments.booking.appointments.model.patner.Availability;
import com.appointments.booking.appointments.payload.request.patner.availabilityRequests.AvailabilityUpdateRequest;
import com.appointments.booking.appointments.payload.response.patner.availabilityResponse.AvailabilityResponseWithOffTime;
import com.appointments.booking.appointments.repository.patner.AvailabilityRepository;
import com.appointments.booking.appointments.service.patner.AvailabilityService;
import com.appointments.booking.appointments.service.patner.StatusUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityMapStruct availabilityMapStruct;
    private final StatusUpdateService statusUpdateService;

    @Autowired
    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository,
                                   AvailabilityMapStruct availabilityMapStruct,
                                   StatusUpdateService statusUpdateService) {
        this.availabilityRepository = availabilityRepository;
        this.availabilityMapStruct = availabilityMapStruct;
        this.statusUpdateService = statusUpdateService;
    }

    @Override
    @Transactional
    public void updateAvailability(AvailabilityUpdateRequest dto, Long userId, Long availabilityId) {

        // UPDATED: Path now includes partnerUser
        Availability existing = availabilityRepository.findByAvailabilityIdAndEmployee_Property_partnerUser_appUser_UserId(availabilityId, userId)
                .orElseThrow(() -> new UnauthorizedAccessOrUnknownException("Unauthorized or availability not found"));

        availabilityMapStruct.updateEntity(dto, existing);
        availabilityRepository.save(existing);

        // Notify status service using the AppUser ID
        statusUpdateService.updateStatuses(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponseWithOffTime> getAllAvailabilityWithOffTimes(Long userId, Long employeeId) {

        // UPDATED: Path now includes partnerUser
        List<Availability> availabilities = availabilityRepository
                .findByEmployee_EmployeeIdAndEmployee_Property_partnerUser_appUser_UserId(employeeId, userId)
                .orElseThrow(()-> new UnauthorizedAccessOrUnknownException("Unauthorized or no availabilities found"));

        return availabilityMapStruct.toDTOList(availabilities);
    }
}
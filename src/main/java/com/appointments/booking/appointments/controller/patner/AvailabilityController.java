package com.appointments.booking.appointments.controller.patner;

import com.appointments.booking.appointments.payload.request.patner.availabilityRequests.AvailabilityUpdateRequest;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.payload.response.patner.availabilityResponse.AvailabilityResponseWithOffTime;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.service.patner.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }


    @PatchMapping("partnerUser/updateAvailability/{availabilityId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAvailability(@RequestBody AvailabilityUpdateRequest dto,
                                                             @PathVariable("availabilityId") Long availabilityId){
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = jwtUserDetails.getId();

        availabilityService.updateAvailability(dto, partnerId, availabilityId);

        return ResponseEntity.ok(new ApiResponse<>(true, "availability added success"));
    }


    @GetMapping("partnerUser/getAvailabilityWithOffTime/{employeeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailabilityWithOffTime(@PathVariable("employeeId") Long employeeId){
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = jwtUserDetails.getId();

        List<AvailabilityResponseWithOffTime> availabilityResponseWithOffTimes = availabilityService.getAllAvailabilityWithOffTimes(partnerId, employeeId);

        Map<String, Object> payload = new HashMap<>();

        payload.put("Availability with Off Time", availabilityResponseWithOffTimes);

        return ResponseEntity.ok(new ApiResponse<>(true, "success", payload));
    }
}

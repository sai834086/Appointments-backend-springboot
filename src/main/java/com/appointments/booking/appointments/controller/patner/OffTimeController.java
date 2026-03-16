package com.appointments.booking.appointments.controller.patner;

import com.appointments.booking.appointments.payload.request.patner.offTimeRequest.OffTimeRegisterRequest;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.payload.response.patner.offTimeResponse.GetAllOffTimeResponse;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.serviceimpl.patner.OffTimeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/appointments")
public class OffTimeController {

    private final OffTimeServiceImpl offTimeService;

    @Autowired
    public OffTimeController(OffTimeServiceImpl offTimeService) {
        this.offTimeService = offTimeService;
    }


    @PostMapping("/partnerUser/offTimeRequest/{availabilityId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addOffTimeRequest(@RequestBody OffTimeRegisterRequest dto, @PathVariable("availabilityId") Long availabilityId){

        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = jwtUserDetails.getId();

        offTimeService.registerOffTimeRequest(dto, partnerId, availabilityId);

        offTimeService.getAllOffTime(availabilityId,partnerId);

        return ResponseEntity.ok(new ApiResponse<>(true, "off time added successfully"));
    }

    @GetMapping("/partnerUser/getAllOffTime/{availabilityId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllOffTime(@PathVariable("availabilityId") Long availabilityId){

        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = jwtUserDetails.getId();

        Map<String, Object> payload = new HashMap<>();

        List<GetAllOffTimeResponse> getAllOffTimeResponse = offTimeService.getAllOffTime(availabilityId, partnerId);

        payload.put("all off time details",getAllOffTimeResponse);

        return ResponseEntity.ok(new ApiResponse<>(true, "successfully sent off times", payload));
    }
    @DeleteMapping("/partnerUser/deleteOffTime/{offTimeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteOffTimeById( @PathVariable("offTimeId") Long offTimeId){
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = jwtUserDetails.getId();

        offTimeService.deleteOffTime(partnerId,offTimeId);

        return ResponseEntity.ok(new ApiResponse<>(true, "OffTime deleted"));
    }
}

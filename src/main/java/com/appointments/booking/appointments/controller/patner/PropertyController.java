package com.appointments.booking.appointments.controller.patner;

import com.appointments.booking.appointments.payload.request.patner.propertyRequests.PropertyRegisterRequest;
import com.appointments.booking.appointments.payload.request.patner.propertyRequests.PropertyUpdateRequest;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.payload.response.patner.propertyResponse.PropertyDetailsResponse;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.service.patner.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class PropertyController {

    private final PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping("partnerUser/registerProperty")
    public ResponseEntity<ApiResponse<Map<String,Object>>> registerProperty(@AuthenticationPrincipal JwtUserDetails jwtUserDetails, @Valid @RequestBody PropertyRegisterRequest dto){

        propertyService.addProperty(dto,jwtUserDetails.getId());
        List<PropertyDetailsResponse> allPropertyDetails = propertyService.allPropertyDetails(jwtUserDetails.getId());


        Map<String,Object> payload = new HashMap<>();
        payload.put("partnerAllProperties", allPropertyDetails);


        return ResponseEntity.ok(new ApiResponse<>(true, "Property added success", payload));
    }

    @PatchMapping("partnerUser/updateProperty/{propertyId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> updateProperty(@PathVariable("propertyId") Long propertyId, @Valid @RequestBody PropertyUpdateRequest dto){


        JwtUserDetails user = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = user.getId();

        PropertyDetailsResponse propertyDetails = propertyService.updateProperty(dto, propertyId, partnerId);

        Map<String,Object> payload = new HashMap<>();
        payload.put("propertyDetails", propertyDetails);

        return ResponseEntity.ok(new ApiResponse<>(true, "Property added success", payload));
    }
    @GetMapping("partnerUser/getAllProperties")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getAllProperties(@AuthenticationPrincipal JwtUserDetails jwtUserDetails){



        List<PropertyDetailsResponse> allPropertyDetails = propertyService.allPropertyDetails(jwtUserDetails.getId());

        Map<String,Object> payload = new HashMap<>();
        payload.put("partnerAllProperties", allPropertyDetails);

        return ResponseEntity.ok(new ApiResponse<>(true, "success", payload));

    }
}

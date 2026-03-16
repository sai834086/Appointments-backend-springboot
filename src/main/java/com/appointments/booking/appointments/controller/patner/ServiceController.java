package com.appointments.booking.appointments.controller.patner;

import com.appointments.booking.appointments.payload.request.patner.serviceRequest.ServicesDTO;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.payload.response.patner.serviceResponse.ServicesResponse;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.service.patner.ServicesService;
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
public class ServiceController {

    private final ServicesService servicesService;

    @Autowired
    public ServiceController(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    @PostMapping("/partnerUser/addService/{propertyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addService(@RequestBody ServicesDTO dto,
                                                                       @PathVariable("propertyId") Long propertyId,
                                                                       @AuthenticationPrincipal JwtUserDetails jwtUserDetails){



        servicesService.addService(dto, propertyId, jwtUserDetails.getId());
        List<ServicesResponse> dtoList = servicesService.getServices(propertyId, jwtUserDetails.getId());

        Map<String, Object> payload = new HashMap<>();

        payload.put("Services", dtoList);

        return ResponseEntity.ok(new ApiResponse<>(true, "Service added successfully",payload));
    }

    @GetMapping("/partnerUser/getServices/{propertyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getService(@PathVariable("propertyId") Long propertyId,
                                                                       @AuthenticationPrincipal JwtUserDetails jwtUserDetails){

        List<ServicesResponse> dtoList = servicesService.getServices(propertyId, jwtUserDetails.getId());

        Map<String, Object> payload = new HashMap<>();

        payload.put("Services", dtoList);

        return ResponseEntity.ok(new ApiResponse<>(true, "Services Response", payload));
    }

    @DeleteMapping("/partnerUser/deleteService/{propertyId}/{serviceId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteService(@PathVariable("serviceId") Long serviceId,
    @PathVariable("propertyId") Long propertyId){
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = jwtUserDetails.getId();

        servicesService.deleteService(serviceId, propertyId, partnerId);

        Map<String, Object> payload = new HashMap<>();

        List<ServicesResponse> dtoList = servicesService.getServices(propertyId,partnerId);

        payload.put("Services", dtoList);

        return ResponseEntity.ok(new ApiResponse<>(true, "Service deleted success", payload));

    }

    @PatchMapping("/partnerUser/updateService/{propertyId}/{serviceId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateService(@RequestBody ServicesDTO dto,@PathVariable("serviceId") Long serviceId,
                                                                          @PathVariable("propertyId") Long propertyId,
                                                                          @AuthenticationPrincipal JwtUserDetails userDetails){


        servicesService.updateService(dto, serviceId, userDetails.getId());

        Map<String, Object> payload = new HashMap<>();

        List<ServicesResponse> dtoList = servicesService.getServices(propertyId, userDetails.getId());

        payload.put("Services", dtoList);

        return ResponseEntity.ok(new ApiResponse<>(true, "Service updated success", payload));

    }

    @GetMapping("PartnerUser/getServicesToEmployees/{employeeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getServicesToEmployee(@PathVariable Long employeeId,
                                                                                  @AuthenticationPrincipal JwtUserDetails userDetails){


        List<ServicesResponse> servicesResponses = servicesService.getEmployeeServices(employeeId, userDetails.getId());

        Map<String, Object> payload = new HashMap<>();

        payload.put("Employee Services", servicesResponses);

        return ResponseEntity.ok(new ApiResponse<>(true, "successfully", payload));


    }

}

package com.appointments.booking.appointments.controller.user;

import com.appointments.booking.appointments.payload.response.user.BookingsResponse;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.payload.response.patner.availabilityResponse.AvailabilitySlotsResponse;
import com.appointments.booking.appointments.payload.response.user.*;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.service.user.AppUserDataFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppUserDataFetchController {

    private final AppUserDataFetchService appUserDataFetchService;

    @Autowired
    public AppUserDataFetchController(AppUserDataFetchService appUserDataFetchService) {
        this.appUserDataFetchService = appUserDataFetchService;
    }


    @GetMapping("/appUser/getAllPartnerBusinesses/{country}/{state}/{city}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllPartnerBusinessesWithAddress(@PathVariable("country") String country,
                                                                                               @PathVariable("state")String state,
                                                                                               @PathVariable("city") String city
                                                                                               ){

        List<AllPartnerUsersToAppUsers> allPartnerUsers = appUserDataFetchService.getAllPartners(country,state,city);

        Map<String, Object> payload = new HashMap<>();
        payload.put("allPartnerUsers", allPartnerUsers);

        return ResponseEntity.ok(new ApiResponse<>(true,"success", payload));
    }
    @GetMapping("/appUser/getAllProperties/{partnerId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllPropertiesWithPartnerId(@PathVariable("partnerId") Long partnerId){

        List<AllPropertiesToAppUsers> properties = appUserDataFetchService.getAllProperties(partnerId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("allProperties", properties);

        return ResponseEntity.ok(new ApiResponse<>(true, "success", payload));
    }


    @GetMapping("/appUser/getAllEmployees/{propertyId}/{serviceId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllEmployeesForService(@PathVariable("propertyId") Long propertyId,
                                                                                          @PathVariable("serviceId") Long serviceId){
        List<EmployeeDTO> employees = appUserDataFetchService.getEmployeesForService(propertyId,serviceId);
        for (EmployeeDTO employeeDTO : employees){
            System.out.println(employeeDTO.getFirstName());
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("allEmployees", employees);

        return ResponseEntity.ok(new ApiResponse<>(true, "success", payload));
    }

    @GetMapping("/appUser/getAllServices/{partnerId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getAllServices(@AuthenticationPrincipal JwtUserDetails jwtUserDetails, @PathVariable("partnerId") Long partnerId){

        List<PropertyWithServicesResponse> propertyWithServicesResponses = appUserDataFetchService.getAllServicesByPartner(partnerId);

        Map<String, Object> payload = new HashMap<>();

        payload.put("property with services", propertyWithServicesResponses);

        return ResponseEntity.ok(new ApiResponse<>(true,"success", payload));
    }


    @GetMapping("/appUser/getAvailability/{employeeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailabilityWithEmployeeId(@PathVariable("employeeId") Long employeeId){

        EmployeeAvailabilityResponse employeeAvailability = appUserDataFetchService.getEmployeeAvailabilityWithEmployeeId(employeeId);

        Map<String, Object> payload = new HashMap<>();
        payload.put("employee availability", employeeAvailability);


        return ResponseEntity.ok(new ApiResponse<>(true, "success", payload));
    }

    @GetMapping("/appUser/getAvailability/{serviceId}/{employeeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailabileSlots(@PathVariable("serviceId") Long serviceId,
                                                                                 @PathVariable("employeeId") Long employeeId,
                                                                                 @RequestParam("date")LocalDate date){
        AvailabilitySlotsResponse response = appUserDataFetchService.getAvailabileSlots(serviceId,employeeId,date);

        Map<String, Object> payload = new HashMap<>();

        payload.put("Availabile Slots", response);
        return ResponseEntity.ok(new ApiResponse<>(true,"success",payload));
    }

    @GetMapping("/appUser/getBookings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailabileSlots(@AuthenticationPrincipal JwtUserDetails jwtUserDetails){

        List<BookingsResponse> response = appUserDataFetchService.getAllBookings(jwtUserDetails.getId());

        Map<String, Object> payload = new HashMap<>();

        payload.put("Bookings", response);
        return ResponseEntity.ok(new ApiResponse<>(true,"success",payload));

    }

    @PatchMapping("/appUser/cancelBooking/{appointmentId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cancelBooking(@AuthenticationPrincipal JwtUserDetails jwtUserDetails,
                                                                          @PathVariable("appointmentId") Long appointmentId) {

        appUserDataFetchService.cancelBooking(jwtUserDetails.getId(),appointmentId);

        return ResponseEntity.ok(new ApiResponse<>(true,"canceled"));
    }

}

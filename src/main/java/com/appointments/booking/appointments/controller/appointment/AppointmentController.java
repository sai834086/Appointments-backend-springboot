package com.appointments.booking.appointments.controller.appointment;

import com.appointments.booking.appointments.payload.request.appointments.AppointmentRequest;
import com.appointments.booking.appointments.payload.request.appointments.RescheduleRequest;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.payload.response.appointments.GetAppointmentsWithPropertyID;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.service.appointment.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appUser/bookAppointment")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bookAppointment(@RequestBody AppointmentRequest appointmentRequest,
                                                                            @AuthenticationPrincipal JwtUserDetails jwtUserDetails){

        String confirmationNumber = appointmentService.bookAppointment(appointmentRequest, jwtUserDetails.getId());

        Map<String, Object> payload = new HashMap<>();
        payload.put("confirmation number",confirmationNumber);

        return ResponseEntity.ok(new ApiResponse<>(true, "Booking Confirmed",payload));
    }

    @PutMapping("/appUser/reschedule")
    public ResponseEntity<ApiResponse<Map<String, Object>>> rescheduleAppointment(@RequestBody RescheduleRequest rescheduleRequest,
                                                                            @AuthenticationPrincipal JwtUserDetails jwtUserDetails) {
        appointmentService.rescheduleAppointment(rescheduleRequest,jwtUserDetails.getId());

        return ResponseEntity.ok(new ApiResponse<>(true, "Rescheduled"));

    }

    @PreAuthorize("hasAnyRole('MANAGER','PARTNER')")
    @GetMapping("/partnerUser/getAppointments/{propertyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAppointmentsWithPropertyId(@AuthenticationPrincipal JwtUserDetails jwtUserDetails,
                                                                                          @PathVariable("propertyId") Long propertyId,
                                                                                          @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        System.out.println("Property ID requested: " + propertyId);
        System.out.println("Date requested: " + date);
        List<GetAppointmentsWithPropertyID> dto = appointmentService.getAppointments(propertyId,date);

        Map<String, Object> payload = new HashMap<>();

        payload.put("appointments", dto);

        return ResponseEntity.ok(new ApiResponse<>(true,"success", payload));
    }

}

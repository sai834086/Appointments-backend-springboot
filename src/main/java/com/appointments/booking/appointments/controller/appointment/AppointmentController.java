package com.appointments.booking.appointments.controller.appointment;

import com.appointments.booking.appointments.payload.request.appointments.AppointmentRequest;
import com.appointments.booking.appointments.payload.request.appointments.RescheduleRequest;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.service.appointment.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
}

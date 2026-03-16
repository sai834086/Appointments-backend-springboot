package com.appointments.booking.appointments.service.appointment;

import com.appointments.booking.appointments.payload.request.appointments.AppointmentRequest;
import com.appointments.booking.appointments.payload.request.appointments.RescheduleRequest;


public interface AppointmentService {

     String bookAppointment(AppointmentRequest request, Long appUserId);

     void rescheduleAppointment(RescheduleRequest rescheduleRequest, Long id);
}

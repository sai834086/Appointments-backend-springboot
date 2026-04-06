package com.appointments.booking.appointments.repository.appointments;

import com.appointments.booking.appointments.model.appointment.Appointments;
import com.appointments.booking.appointments.payload.response.appointments.GetAppointmentsWithPropertyID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointments, Long> {


    List<Appointments> findByAppointmentDate(LocalDate date);

    @Query("SELECT COUNT(a) > 0 FROM Appointments a " +
            "WHERE a.employee.employeeId = :employeeId " +
            "AND a.appointmentDate = :date " +
            "AND a.status != 'Cancelled' " +
            "AND a.startTime < :newEndTime " +
            "AND a.endTime > :newStartTime")
    boolean existsByOverlap(@Param("employeeId") Long employeeId,
                            @Param("date") LocalDate date,
                            @Param("newStartTime") LocalTime newStartTime,
                            @Param("newEndTime") LocalTime newEndTime);


    List<Appointments> findByCustomer_UserId(Long userId);

    @Query("SELECT COUNT(a) > 0 FROM Appointments a " +
            "WHERE a.employee.employeeId = :employeeId " +
            "AND a.appointmentDate = :date " +
            "AND a.status != 'Cancelled' " +
            "AND a.id != :currentAppointmentId " + // <--- CRITICAL: Ignore itself
            "AND a.startTime < :newEndTime " +
            "AND a.endTime > :newStartTime")
    boolean existsByOverlapExcludingCurrent(@Param("employeeId") Long employeeId,
                                            @Param("date") LocalDate date,
                                            @Param("newStartTime") LocalTime newStartTime,
                                            @Param("newEndTime") LocalTime newEndTime,
                                            @Param("currentAppointmentId") Long currentAppointmentId);

    @Query(value = """
        SELECT\s
            a.appointment_id AS appointmentId,\s
            a.booked_at AS bookedAt,\s
            a.updated_at AS updatedAt,\s
            a.confirmation_number AS confirmationNumber,\s
            a.appointment_date AS appointmentDate,\s
            a.start_time AS startTime,\s
            a.end_time AS endTime,\s
            a.status AS status,\s
            CONCAT(e.first_name, ' ', e.last_name) AS employeeName,\s
            p.property_name AS propertyName,\s
            s.service_name AS serviceName,\s
            CONCAT(u.first_name, ' ', u.last_name) AS customerName
        FROM appointments a
        JOIN employee e ON a.employee_id = e.employee_id
        JOIN property p ON a.property_id = p.property_id
        JOIN services s ON a.service_id = s.service_id
        JOIN app_user u ON a.user_id = u.user_id
        WHERE p.property_id = :propertyId\s
        AND a.appointment_date = :date
       \s""", nativeQuery = true)
    List<GetAppointmentsWithPropertyID> findAllAppointmentsWithPropertyIdAndDate(
            @Param("propertyId") Long propertyId,
            @Param("date") String date);


}

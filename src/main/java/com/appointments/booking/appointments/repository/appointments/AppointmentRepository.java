package com.appointments.booking.appointments.repository.appointments;

import com.appointments.booking.appointments.model.appointment.Appointments;
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
}

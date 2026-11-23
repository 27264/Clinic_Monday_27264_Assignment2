package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Quiz 1
    List<Appointment> findByStatusOrderByAppointmentDateAsc(
            AppointmentStatus status
    );

    // Quiz 1
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDate start,
            LocalDate end
    );

    // Quiz 1 + Quiz 2 B3
    boolean existsByDoctor_IdAndAppointmentDateAndStatusNot(
            Long doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status
    );

    // Quiz 1
    @Query("""
            SELECT a.status, COUNT(a)
            FROM Appointment a
            GROUP BY a.status
            """)
    List<Object[]> countAppointmentsByStatus();

    // Quiz 1
    @Modifying
    @Query("""
            UPDATE Appointment a
            SET a.status = :cancelled
            WHERE a.doctor.id = :doctorId
              AND a.appointmentDate = :date
              AND a.status <> :completed
            """)
    int cancelDoctorAppointmentsForDay(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("cancelled") AppointmentStatus cancelled,
            @Param("completed") AppointmentStatus completed
    );

    // Quiz 1 bonus
    @Modifying
    @Query("""
            DELETE FROM Appointment a
            WHERE a.status = :status
              AND a.appointmentDate < :date
            """)
    int deleteByStatusAndAppointmentDateBefore(
            @Param("status") AppointmentStatus status,
            @Param("date") LocalDate date
    );
  // =========================================================
// QUIZ 2 - D1
// Doctor agenda for one date excluding CANCELLED
// =========================================================
List<Appointment>
findByDoctor_IdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
        Long doctorId,
        LocalDate appointmentDate,
        AppointmentStatus status
);


// =========================================================
// QUIZ 2 - D2
// Patient upcoming appointments
// =========================================================
List<Appointment>
findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
        String ssNumber,
        LocalDate date,
        List<AppointmentStatus> statuses
);


// =========================================================
// QUIZ 2 - D4
// Dashboard counts
// =========================================================
long countByAppointmentDate(
        LocalDate appointmentDate
);

long countByStatus(
        AppointmentStatus status
);  
}
package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.Prescription;

@Repository
public interface PrescriptionRepository
        extends JpaRepository<Prescription, UUID> {


    // =========================================================
    // C2 - Prescriptions of an appointment
    // DERIVED
    // =========================================================
    List<Prescription> findByAppointment(
            Appointment appointment
    );


    // =========================================================
    // C3 - Prescription history of a patient
    // JPQL
    // =========================================================
    @Query("""
            SELECT p
            FROM Prescription p
            WHERE p.appointment.patient.ssNumber = :ssNumber
            ORDER BY p.issuedDate DESC
            """)
    List<Prescription> findByPatientSsNumber(
            @Param("ssNumber") String ssNumber
    );


    // =========================================================
    // C4 - Protect appointment deletion
    // DERIVED
    // =========================================================
    boolean existsByAppointment_Id(
            Long appointmentId
    );

// =========================================================
// QUIZ 2 BONUS
// TOP PRESCRIBED MEDICINES
// =========================================================
@Query("""
        SELECT p.medicineName, COUNT(p)
        FROM Prescription p
        GROUP BY p.medicineName
        ORDER BY COUNT(p) DESC
        """)
List<Object[]> findTopMedicines();

}
package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

        // Quiz 1 - Doctors by specialization
        @Query("""
                        SELECT d
                        FROM Doctor d
                        JOIN d.specializations s
                        WHERE LOWER(s.name) = LOWER(:name)
                        """)
        List<Doctor> findBySpecializationName(
                        @Param("name") String name);

        // Quiz 1 - Doctors without office
        @Query("""
                        SELECT d
                        FROM Doctor d
                        WHERE d.office IS NULL
                        ORDER BY d.lastName ASC
                        """)
        List<Doctor> findDoctorsWithoutOffice();

        // =========================================================
        // QUIZ 2 - A4
        // Check if another doctor already occupies an office
        // DERIVED QUERY
        // =========================================================
        boolean existsByOffice_Id(Long officeId);

        // =========================================================
        // QUIZ 2 - D3
        // Search doctor by first name OR last name
        // =========================================================
        @Query("""
                        SELECT d
                        FROM Doctor d
                        WHERE LOWER(d.firstName)
                              LIKE LOWER(CONCAT('%', :name, '%'))
                           OR LOWER(d.lastName)
                              LIKE LOWER(CONCAT('%', :name, '%'))
                        ORDER BY d.lastName ASC
                        """)
        List<Doctor> searchByName(
                        @Param("name") String name);

        // =========================================================
        // QUIZ 2 BONUS
        // DOCTOR WORKLOAD
        // =========================================================
        @Query("""
                        SELECT d.firstName,
                               d.lastName,
                               COUNT(a)
                        FROM Doctor d
                        JOIN d.appointments a
                        WHERE a.status = :status
                          AND a.appointmentDate BETWEEN :start AND :end
                        GROUP BY d.id,
                                 d.firstName,
                                 d.lastName
                        ORDER BY COUNT(a) DESC
                        """)
        List<Object[]> doctorWorkload(
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end,
                        @Param("status") AppointmentStatus status);

}
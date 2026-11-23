package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

        @Autowired
        private PrescriptionRepository prescriptionRepo;

        @Autowired
        private AppointmentRepository appointmentRepo;


        // =========================================================
        // C1 - WRITE PRESCRIPTION
        // =========================================================
        public String savePrescription(
                        Prescription prescription,
                        Long appointmentId) {

                Optional<Appointment> appointmentOpt = appointmentRepo.findById(
                                appointmentId);

                if (appointmentOpt.isEmpty()) {

                        return "The appointment with that id does not exist";
                }

                Appointment appointment = appointmentOpt.get();

                if (appointment.getStatus() != AppointmentStatus.COMPLETED) {

                        return "Prescriptions are only for COMPLETED appointments";
                }

                prescription.setAppointment(
                                appointment);

                prescription.setIssuedDate(
                                LocalDate.now());

                prescriptionRepo.save(
                                prescription);

                return "Prescription saved successfully";
        }

        // =========================================================
        // C2 - PRESCRIPTIONS OF APPOINTMENT
        // =========================================================
        public Optional<List<Prescription>> findByAppointment(Long appointmentId) {

                Optional<Appointment> appointmentOpt = appointmentRepo.findById(
                                appointmentId);

                if (appointmentOpt.isEmpty()) {

                        return Optional.empty();
                }

                return Optional.of(
                                prescriptionRepo.findByAppointment(
                                                appointmentOpt.get()));
        }

        // =========================================================
        // C3 - PRESCRIPTION HISTORY OF PATIENT
        // =========================================================
        public List<Prescription> findPatientHistory(String ssNumber) {

                return prescriptionRepo
                                .findByPatientSsNumber(
                                                ssNumber);
        }

        // =========================================================
        // C4 - UPDATE
        // =========================================================
        public String updatePrescription(
                        UUID id,
                        Prescription prescription) {

                Optional<Prescription> existing = prescriptionRepo.findById(id);

                if (existing.isEmpty()) {

                        return "The prescription with that id does not exist";
                }

                Prescription dbPrescription = existing.get();

                // C4 says ONLY these 3 fields change
                dbPrescription.setMedicineName(
                                prescription.getMedicineName());

                dbPrescription.setDosage(
                                prescription.getDosage());

                dbPrescription.setDurationDays(
                                prescription.getDurationDays());

                prescriptionRepo.save(
                                dbPrescription);

                return "Prescription updated successfully";
        }

        // =========================================================
        // C4 - DELETE
        // =========================================================
        public String deletePrescription(
                        UUID id) {

                if (!prescriptionRepo.existsById(id)) {

                        return "The prescription with that id does not exist";
                }

                prescriptionRepo.deleteById(id);

                return "Prescription deleted successfully";
        }

// =========================================================
// QUIZ 2 BONUS
// =========================================================
public List<Object[]> topMedicines() {

    return prescriptionRepo
            .findTopMedicines();
}

}
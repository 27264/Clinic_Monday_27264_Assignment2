package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class AppointmentService {

        @Autowired
        private AppointmentRepository appointmentRepo;

        @Autowired
        private PatientRepository patientRepo;

        @Autowired
        private DoctorRepository doctorRepo;
        @Autowired
        private PrescriptionRepository prescriptionRepo;

        // =========================================================
        // QUIZ 2 - B1 SAFE BOOKING
        // =========================================================
        public String saveAppointment(Appointment appointment) {

                if (appointment.getPatient() == null
                                || appointment.getPatient().getId() == null) {
                        return "Patient is required";
                }

                if (appointment.getDoctor() == null
                                || appointment.getDoctor().getId() == null) {
                        return "Doctor is required";
                }

                if (appointment.getAppointmentDate() == null) {
                        return "Appointment date is required";
                }

                // B1 - date cannot be in the past
                if (appointment.getAppointmentDate()
                                .isBefore(LocalDate.now())) {

                        return "Appointment date cannot be in the past";
                }

                Optional<Patient> patient = patientRepo.findById(
                                appointment.getPatient().getId());

                if (patient.isEmpty()) {
                        return "Patient not found";
                }

                Optional<Doctor> doctor = doctorRepo.findById(
                                appointment.getDoctor().getId());

                if (doctor.isEmpty()) {
                        return "Doctor not found";
                }

                boolean booked = appointmentRepo
                                .existsByDoctor_IdAndAppointmentDateAndStatusNot(
                                                doctor.get().getId(),
                                                appointment.getAppointmentDate(),
                                                AppointmentStatus.CANCELLED);

                if (booked) {
                        return "Doctor is already booked on that date";
                }

                appointment.setPatient(patient.get());
                appointment.setDoctor(doctor.get());

                // B1 - new appointments ALWAYS start as SCHEDULED
                appointment.setStatus(
                                AppointmentStatus.SCHEDULED);

                appointmentRepo.save(appointment);

                return "Appointment is saved Successfully";
        }

        // =========================================================
        // NORMAL CRUD
        // =========================================================

        public List<Appointment> findAll() {
                return appointmentRepo.findAll();
        }

        public Optional<Appointment> findById(Long id) {
                return appointmentRepo.findById(id);
        }

        public String updateAppointment(
                        Long id,
                        Appointment appointment) {

                Optional<Appointment> existing = appointmentRepo.findById(id);

                if (existing.isEmpty()) {
                        return "Appointment not found";
                }

                Appointment dbAppointment = existing.get();

                dbAppointment.setAppointmentDate(
                                appointment.getAppointmentDate());

                dbAppointment.setReason(
                                appointment.getReason());

                // IMPORTANT:
                // Do NOT change status here.
                // Quiz 2 says status changes only through B2.

                if (appointment.getPatient() != null
                                && appointment.getPatient().getId() != null) {

                        Optional<Patient> patient = patientRepo.findById(
                                        appointment.getPatient().getId());

                        if (patient.isEmpty()) {
                                return "Patient not found";
                        }

                        dbAppointment.setPatient(
                                        patient.get());
                }

                if (appointment.getDoctor() != null
                                && appointment.getDoctor().getId() != null) {

                        Optional<Doctor> doctor = doctorRepo.findById(
                                        appointment.getDoctor().getId());

                        if (doctor.isEmpty()) {
                                return "Doctor not found";
                        }

                        dbAppointment.setDoctor(
                                        doctor.get());
                }

                appointmentRepo.save(
                                dbAppointment);

                return "Appointment is updated Successfully";
        }

        public String deleteAppointment(Long id) {

                if (!appointmentRepo.existsById(id)) {

                        return "Appointment not found";
                }

                if (prescriptionRepo
                                .existsByAppointment_Id(id)) {

                        return "That appointment has prescriptions, delete them first";
                }

                appointmentRepo.deleteById(id);

                return "Appointment is deleted Successfully";
        }

        // =========================================================
        // QUIZ 2 - B2 STATUS FLOW
        // =========================================================

        public String changeStatus(
                        Long id,
                        AppointmentStatus newStatus) {

                Optional<Appointment> appointmentOpt = appointmentRepo.findById(id);

                if (appointmentOpt.isEmpty()) {

                        return "The appointment with that id does not exist";
                }

                Appointment appointment = appointmentOpt.get();

                AppointmentStatus currentStatus = appointment.getStatus();

                boolean allowed = false;

                if (currentStatus == AppointmentStatus.SCHEDULED) {

                        if (newStatus == AppointmentStatus.CONFIRMED
                                        || newStatus == AppointmentStatus.CANCELLED) {

                                allowed = true;
                        }
                }

                if (currentStatus == AppointmentStatus.CONFIRMED) {

                        if (newStatus == AppointmentStatus.COMPLETED
                                        || newStatus == AppointmentStatus.CANCELLED) {

                                allowed = true;
                        }
                }

                if (!allowed) {

                        return "Cannot change status from "
                                        + currentStatus
                                        + " to "
                                        + newStatus;
                }

                appointment.setStatus(
                                newStatus);

                appointmentRepo.save(
                                appointment);

                return "Appointment status updated successfully";
        }

        // =========================================================
        // QUIZ 2 - B3 RESCHEDULE
        // =========================================================

        public String rescheduleAppointment(
                        Long id,
                        LocalDate newDate) {

                Optional<Appointment> appointmentOpt = appointmentRepo.findById(id);

                if (appointmentOpt.isEmpty()) {

                        return "The appointment with that id does not exist";
                }

                Appointment appointment = appointmentOpt.get();

                if (appointment.getStatus() == AppointmentStatus.COMPLETED) {

                        return "A COMPLETED appointment cannot be rescheduled";
                }

                if (appointment.getStatus() == AppointmentStatus.CANCELLED) {

                        return "A CANCELLED appointment cannot be rescheduled";
                }

                if (newDate.isBefore(
                                LocalDate.now())) {

                        return "Appointment date cannot be in the past";
                }

                boolean booked = appointmentRepo
                                .existsByDoctor_IdAndAppointmentDateAndStatusNot(
                                                appointment.getDoctor().getId(),
                                                newDate,
                                                AppointmentStatus.CANCELLED);

                /*
                 * If rescheduling to the same existing date,
                 * the current appointment would find itself.
                 * Only check booking conflict when the date changes.
                 */
                if (!newDate.equals(
                                appointment.getAppointmentDate())
                                && booked) {

                        return "Doctor is already booked on that date";
                }

                appointment.setAppointmentDate(
                                newDate);

                appointment.setStatus(
                                AppointmentStatus.SCHEDULED);

                appointmentRepo.save(
                                appointment);

                return "Appointment rescheduled successfully";
        }

        // =========================================================
        // QUIZ 1 METHODS
        // =========================================================

        public List<Appointment> findByStatus(
                        AppointmentStatus status) {

                return appointmentRepo
                                .findByStatusOrderByAppointmentDateAsc(
                                                status);
        }

        public List<Appointment> findBetween(
                        LocalDate start,
                        LocalDate end) {

                return appointmentRepo
                                .findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                                                start,
                                                end);
        }

        public List<Object[]> statsByStatus() {

                return appointmentRepo
                                .countAppointmentsByStatus();
        }

        @Transactional
        public String cancelDay(
                        Long doctorId,
                        LocalDate date) {

                int updated = appointmentRepo
                                .cancelDoctorAppointmentsForDay(
                                                doctorId,
                                                date,
                                                AppointmentStatus.CANCELLED,
                                                AppointmentStatus.COMPLETED);

                return updated
                                + " appointments cancelled";
        }

        public Page<Appointment> findPage(
                        Pageable pageable) {

                return appointmentRepo
                                .findAll(pageable);
        }

        @Transactional
        public String deleteCancelledBefore(
                        LocalDate date) {

                int deleted = appointmentRepo
                                .deleteByStatusAndAppointmentDateBefore(
                                                AppointmentStatus.CANCELLED,
                                                date);

                return deleted
                                + " appointments deleted";
        }
}
package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;
import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private OfficeRepository officeRepo;

    @Autowired
    private SpecializationRepository specializationRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    // =========================================================
    // NORMAL CRUD
    // =========================================================

    public String saveDoctor(Doctor doctor) {

        if (doctor.getOffice() != null
                && doctor.getOffice().getId() != null) {

            Optional<Office> office =
                    officeRepo.findById(
                            doctor.getOffice().getId()
                    );

            if (office.isEmpty()) {
                return "Office not found";
            }

            if (doctorRepo.existsByOffice_Id(
                    office.get().getId())) {

                return "That office already has a doctor";
            }

            doctor.setOffice(office.get());
        }

        doctorRepo.save(doctor);

        return "Doctor is saved Successfully";
    }


    public List<Doctor> findAll() {
        return doctorRepo.findAll();
    }


    public Optional<Doctor> findById(Long id) {
        return doctorRepo.findById(id);
    }


    public String updateDoctor(
            Long id,
            Doctor doctor) {

        Optional<Doctor> existing =
                doctorRepo.findById(id);

        if (existing.isEmpty()) {
            return "Doctor not found";
        }

        Doctor dbDoctor =
                existing.get();

        dbDoctor.setFirstName(
                doctor.getFirstName());

        dbDoctor.setLastName(
                doctor.getLastName());

        dbDoctor.setDateOfBirth(
                doctor.getDateOfBirth());

        doctorRepo.save(dbDoctor);

        return "Doctor is updated Successfully";
    }


    @Transactional
    public String deleteDoctor(Long id) {

        Optional<Doctor> existing =
                doctorRepo.findById(id);

        if (existing.isEmpty()) {
            return "Doctor not found";
        }

        Doctor doctor =
                existing.get();

        doctor.setOffice(null);

        doctorRepo.save(doctor);

        doctorRepo.delete(doctor);

        return "Doctor is deleted Successfully";
    }


    // =========================================================
    // QUIZ 2 - A1
    // Add one specialization
    // =========================================================

    @Transactional
    public String addSpecialization(
            Long doctorId,
            Long specializationId) {

        Optional<Doctor> doctorOpt =
                doctorRepo.findById(doctorId);

        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> specOpt =
                specializationRepo.findById(
                        specializationId
                );

        if (specOpt.isEmpty()) {
            return "That specialization does not exist";
        }

        Doctor doctor =
                doctorOpt.get();

        Specialization specialization =
                specOpt.get();


        if (doctor.getSpecializations()
                .contains(specialization)) {

            return "The doctor already has that specialization";
        }


        doctor.getSpecializations()
                .add(specialization);

        doctorRepo.save(doctor);

        return "Specialization added to the doctor";
    }


    // =========================================================
    // QUIZ 2 - A2
    // Remove one specialization
    // =========================================================

    @Transactional
    public String removeSpecialization(
            Long doctorId,
            Long specializationId) {

        Optional<Doctor> doctorOpt =
                doctorRepo.findById(doctorId);

        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> specOpt =
                specializationRepo.findById(
                        specializationId
                );

        if (specOpt.isEmpty()) {
            return "That specialization does not exist";
        }


        Doctor doctor =
                doctorOpt.get();

        Specialization specialization =
                specOpt.get();


        if (!doctor.getSpecializations()
                .contains(specialization)) {

            return "The doctor does not have that specialization";
        }


        doctor.getSpecializations()
                .remove(specialization);

        doctorRepo.save(doctor);

        return "Specialization removed from the doctor";
    }


    // =========================================================
    // QUIZ 2 - A4
    // Move doctor to another office
    // =========================================================

    @Transactional
    public String changeOffice(
            Long doctorId,
            int officeNumber) {

        Optional<Doctor> doctorOpt =
                doctorRepo.findById(doctorId);

        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }


        Office office =
                officeRepo.findByOfficeNumber(
                        officeNumber
                );

        if (office == null) {
            return "The office with that office number does not exist";
        }


        Doctor doctor =
                doctorOpt.get();


        // If the office belongs to another doctor
        if (doctorRepo.existsByOffice_Id(
                office.getId())) {

            if (doctor.getOffice() == null
                    || !doctor.getOffice()
                    .getId()
                    .equals(office.getId())) {

                return "That office is taken by other doctor";
            }
        }


        doctor.setOffice(office);

        doctorRepo.save(doctor);

        return "Office changed successfully";
    }


    // =========================================================
    // QUIZ 1 METHODS
    // =========================================================

    public List<Doctor>
    findBySpecializationName(String name) {

        return doctorRepo
                .findBySpecializationName(name);
    }


    public List<Doctor>
    findDoctorsWithoutOffice() {

        return doctorRepo
                .findDoctorsWithoutOffice();
    }
    // =========================================================
// QUIZ 2 - D1
// DOCTOR DAILY AGENDA
// =========================================================
public List<Appointment> getDoctorAgenda(
        Long doctorId,
        LocalDate date) {

    return appointmentRepo
            .findByDoctor_IdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
                    doctorId,
                    date,
                    AppointmentStatus.CANCELLED
            );
}


// =========================================================
// QUIZ 2 - D3
// SEARCH DOCTORS
// =========================================================
public List<Doctor> searchDoctors(
        String name) {

    return doctorRepo.searchByName(
            name
    );
}

// =========================================================
// QUIZ 2 BONUS
// =========================================================
public List<Object[]> doctorWorkload(
        LocalDate start,
        LocalDate end) {

    return doctorRepo
            .doctorWorkload(
                    start,
                    end,
                    AppointmentStatus.COMPLETED
            );
}

}

package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class DashboardService {

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private OfficeService officeService;


    // =========================================================
    // QUIZ 2 - D4
    // =========================================================
    public Map<String, Long> getSummary() {

        LinkedHashMap<String, Long> summary =
                new LinkedHashMap<>();


        summary.put(
                "patients",
                patientRepo.count()
        );


        summary.put(
                "doctors",
                doctorRepo.count()
        );


        summary.put(
                "freeOffices",
                (long) officeService
                        .findFreeOffices()
                        .size()
        );


        summary.put(
                "appointmentsToday",
                appointmentRepo
                        .countByAppointmentDate(
                                LocalDate.now()
                        )
        );


        summary.put(
                "scheduled",
                appointmentRepo
                        .countByStatus(
                                AppointmentStatus.SCHEDULED
                        )
        );


        summary.put(
                "prescriptions",
                prescriptionRepo.count()
        );


        return summary;
    }
}
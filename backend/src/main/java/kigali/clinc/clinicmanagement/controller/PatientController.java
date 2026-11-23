package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.service.PatientService;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;


    @PostMapping(
            value = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> savePatient(
            @RequestBody Patient patient) {

        String message =
                patientService.savePatient(patient);

        return new ResponseEntity<>(
                message,
                HttpStatus.CREATED
        );
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllPatients() {

        return ResponseEntity.ok(
                patientService.findAll()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(
            @PathVariable Long id) {

        Optional<Patient> patient =
                patientService.findById(id);

        if (patient.isEmpty()) {
            return new ResponseEntity<>(
                    "Patient not found",
                    HttpStatus.NOT_FOUND
            );
        }

        return ResponseEntity.ok(
                patient.get()
        );
    }


    @PutMapping(
            value = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updatePatient(
            @PathVariable Long id,
            @RequestBody Patient patient) {

        String message =
                patientService.updatePatient(
                        id,
                        patient
                );

        if (message.equalsIgnoreCase(
                "Patient is updated Successfully")) {

            return ResponseEntity.ok(message);
        }

        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePatient(
            @PathVariable Long id) {

        String message =
                patientService.deletePatient(id);

        if (message.equalsIgnoreCase(
                "Patient is deleted Successfully")) {

            return ResponseEntity.ok(message);
        }

        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }


    // =========================================================
    // A1
    //
    // GET /api/patients/by-last-name?lastName=uwase
    // =========================================================
    @GetMapping("/by-last-name")
    public ResponseEntity<?> byLastName(
            @RequestParam String lastName) {

        return ResponseEntity.ok(
                patientService.findByLastName(lastName)
        );
    }


    // =========================================================
    // B4
    //
    // GET /api/patients/of-doctor/{doctorId}
    // =========================================================
    @GetMapping("/of-doctor/{doctorId}")
    public ResponseEntity<?> ofDoctor(
            @PathVariable Long doctorId) {

        Optional<List<Patient>> patients =
                patientService
                        .findPatientsOfDoctor(doctorId);


        if (patients.isEmpty()) {

            return new ResponseEntity<>(
                    "The doctor with that id does not exist",
                    HttpStatus.NOT_FOUND
            );
        }


        return ResponseEntity.ok(
                patients.get()
        );
    }


    // =========================================================
    // C2
    //
    // GET /api/patients/frequent?min=3
    // =========================================================
    @GetMapping("/frequent")
    public ResponseEntity<?> frequent(
            @RequestParam long min) {

        return ResponseEntity.ok(
                patientService
                        .findFrequentPatients(min)
        );
    }

// =========================================================
// QUIZ 2 - D2
//
// GET /api/patients/{ssNumber}/upcoming
// =========================================================
@GetMapping("/{ssNumber}/upcoming")
public ResponseEntity<?> upcomingAppointments(
        @PathVariable String ssNumber) {

    if (!patientService.existsBySsNumber(
            ssNumber)) {

        return new ResponseEntity<>(
                "That patient with that ssnumber does not exist",
                HttpStatus.NOT_FOUND
        );
    }


    return ResponseEntity.ok(
            patientService
                    .upcomingAppointments(
                            ssNumber
                    )
    );
}

}
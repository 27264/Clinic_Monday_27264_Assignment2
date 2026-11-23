package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.service.PrescriptionService;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;


    // =========================================================
    // C1
    //
    // POST /api/prescriptions/save?appointmentId=...
    // =========================================================
    @PostMapping(
            value = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> savePrescription(
            @RequestBody Prescription prescription,
            @RequestParam Long appointmentId) {

        String message =
                prescriptionService
                        .savePrescription(
                                prescription,
                                appointmentId
                        );


        if (message.equals(
                "Prescription saved successfully")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.CREATED
            );
        }


        if (message.equals(
                "The appointment with that id does not exist")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.NOT_FOUND
            );
        }


        return new ResponseEntity<>(
                message,
                HttpStatus.BAD_REQUEST
        );
    }


    // =========================================================
    // C2
    //
    // GET /api/prescriptions/appointment/{appointmentId}
    // =========================================================
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> appointmentPrescriptions(
            @PathVariable Long appointmentId) {

        Optional<List<Prescription>> prescriptions =
                prescriptionService
                        .findByAppointment(
                                appointmentId
                        );


        if (prescriptions.isEmpty()) {

            return new ResponseEntity<>(
                    "The appointment with that id does not exist",
                    HttpStatus.NOT_FOUND
            );
        }


        return ResponseEntity.ok(
                prescriptions.get()
        );
    }


    // =========================================================
    // C3
    //
    // GET /api/prescriptions/patient/{ssNumber}
    // =========================================================
    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<?> patientHistory(
            @PathVariable String ssNumber) {

        return ResponseEntity.ok(
                prescriptionService
                        .findPatientHistory(
                                ssNumber
                        )
        );
    }


    // =========================================================
    // C4 UPDATE
    //
    // PUT /api/prescriptions/update/{id}
    // =========================================================
    @PutMapping(
            value = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updatePrescription(
            @PathVariable UUID id,
            @RequestBody Prescription prescription) {

        String message =
                prescriptionService
                        .updatePrescription(
                                id,
                                prescription
                        );


        if (message.equals(
                "Prescription updated successfully")) {

            return ResponseEntity.ok(
                    message
            );
        }


        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }


    // =========================================================
    // C4 DELETE
    //
    // DELETE /api/prescriptions/delete/{id}
    // =========================================================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePrescription(
            @PathVariable UUID id) {

        String message =
                prescriptionService
                        .deletePrescription(id);


        if (message.equals(
                "Prescription deleted successfully")) {

            return ResponseEntity.ok(
                    message
            );
        }


        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }

// =========================================================
// QUIZ 2 BONUS
//
// GET /api/prescriptions/stats/top-medicines
// =========================================================
@GetMapping("/stats/top-medicines")
public ResponseEntity<?> topMedicines() {

    return ResponseEntity.ok(
            prescriptionService
                    .topMedicines()
    );
}
    
}
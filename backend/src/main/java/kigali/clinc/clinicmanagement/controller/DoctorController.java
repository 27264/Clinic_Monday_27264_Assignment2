package kigali.clinc.clinicmanagement.controller;

import java.util.Optional;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;


    // =========================================================
    // NORMAL CRUD
    // =========================================================

    @PostMapping(
            value = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> saveDoctor(
            @RequestBody Doctor doctor) {

        String message =
                doctorService.saveDoctor(doctor);

        if (message.equalsIgnoreCase(
                "Doctor is saved Successfully")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.CREATED
            );
        }

        return new ResponseEntity<>(
                message,
                HttpStatus.CONFLICT
        );
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllDoctors() {

        return ResponseEntity.ok(
                doctorService.findAll()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(
            @PathVariable Long id) {

        Optional<Doctor> doctor =
                doctorService.findById(id);

        if (doctor.isEmpty()) {

            return new ResponseEntity<>(
                    "Doctor not found",
                    HttpStatus.NOT_FOUND
            );
        }

        return ResponseEntity.ok(
                doctor.get()
        );
    }


    @PutMapping(
            value = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateDoctor(
            @PathVariable Long id,
            @RequestBody Doctor doctor) {

        String message =
                doctorService.updateDoctor(
                        id,
                        doctor
                );

        if (message.equalsIgnoreCase(
                "Doctor is updated Successfully")) {

            return ResponseEntity.ok(message);
        }

        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long id) {

        String message =
                doctorService.deleteDoctor(id);

        if (message.equalsIgnoreCase(
                "Doctor is deleted Successfully")) {

            return ResponseEntity.ok(message);
        }

        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }


    // =========================================================
    // QUIZ 2 - A1
    //
    // PUT /api/doctors/{doctorId}/specializations/{specializationId}
    // =========================================================

    @PutMapping(
            "/{doctorId}/specializations/{specializationId}"
    )
    public ResponseEntity<?> addSpecialization(
            @PathVariable Long doctorId,
            @PathVariable Long specializationId) {

        String message =
                doctorService.addSpecialization(
                        doctorId,
                        specializationId
                );


        if (message.equals(
                "Specialization added to the doctor")) {

            return ResponseEntity.ok(message);
        }


        if (message.equals(
                "The doctor with that id does not exist")
                || message.equals(
                "That specialization does not exist")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.NOT_FOUND
            );
        }


        return new ResponseEntity<>(
                message,
                HttpStatus.CONFLICT
        );
    }


    // =========================================================
    // QUIZ 2 - A2
    //
    // DELETE /api/doctors/{doctorId}/specializations/{specializationId}
    // =========================================================

    @DeleteMapping(
            "/{doctorId}/specializations/{specializationId}"
    )
    public ResponseEntity<?> removeSpecialization(
            @PathVariable Long doctorId,
            @PathVariable Long specializationId) {

        String message =
                doctorService.removeSpecialization(
                        doctorId,
                        specializationId
                );


        if (message.equals(
                "Specialization removed from the doctor")) {

            return ResponseEntity.ok(message);
        }


        if (message.equals(
                "The doctor with that id does not exist")
                || message.equals(
                "That specialization does not exist")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.NOT_FOUND
            );
        }


        return new ResponseEntity<>(
                message,
                HttpStatus.CONFLICT
        );
    }


    // =========================================================
    // QUIZ 2 - A4
    //
    // PATCH /api/doctors/{doctorId}/office?officeNumber=104
    // =========================================================

    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<?> changeOffice(
            @PathVariable Long doctorId,
            @RequestParam int officeNumber) {

        String message =
                doctorService.changeOffice(
                        doctorId,
                        officeNumber
                );


        if (message.equals(
                "Office changed successfully")) {

            return ResponseEntity.ok(message);
        }


        if (message.equals(
                "The doctor with that id does not exist")
                || message.equals(
                "The office with that office number does not exist")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.NOT_FOUND
            );
        }


        return new ResponseEntity<>(
                message,
                HttpStatus.CONFLICT
        );
    }


    // =========================================================
    // QUIZ 1
    // =========================================================

    @GetMapping("/by-specialization")
    public ResponseEntity<?> bySpecialization(
            @RequestParam String name) {

        return ResponseEntity.ok(
                doctorService
                        .findBySpecializationName(name)
        );
    }


    @GetMapping("/without-office")
    public ResponseEntity<?> withoutOffice() {

        return ResponseEntity.ok(
                doctorService
                        .findDoctorsWithoutOffice()
        );
    }
    // =========================================================
// QUIZ 2 - D1
//
// GET /api/doctors/{doctorId}/agenda?date=2026-10-20
// =========================================================
@GetMapping("/{doctorId}/agenda")
public ResponseEntity<?> doctorAgenda(
        @PathVariable Long doctorId,
        @RequestParam String date) {

    if (doctorService.findById(doctorId).isEmpty()) {

        return new ResponseEntity<>(
                "The doctor with that id does not exist",
                HttpStatus.NOT_FOUND
        );
    }


    LocalDate localDate =
            LocalDate.parse(date);


    return ResponseEntity.ok(
            doctorService.getDoctorAgenda(
                    doctorId,
                    localDate
            )
    );
}
// =========================================================
// QUIZ 2 - D3
//
// GET /api/doctors/search?name=gis
// =========================================================
@GetMapping("/search")
public ResponseEntity<?> searchDoctors(
        @RequestParam String name) {

    return ResponseEntity.ok(
            doctorService.searchDoctors(
                    name
            )
    );
}

// =========================================================
// QUIZ 2 BONUS
//
// GET /api/doctors/workload
// ?start=2026-10-01&end=2026-10-31
// =========================================================
@GetMapping("/workload")
public ResponseEntity<?> doctorWorkload(
        @RequestParam String start,
        @RequestParam String end) {

    LocalDate startDate =
            LocalDate.parse(start);

    LocalDate endDate =
            LocalDate.parse(end);


    return ResponseEntity.ok(
            doctorService
                    .doctorWorkload(
                            startDate,
                            endDate
                    )
    );
}

}

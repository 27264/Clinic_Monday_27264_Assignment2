package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;


    // =========================================================
    // SAVE APPOINTMENT
    // QUIZ 2 - B1 SAFE BOOKING
    // =========================================================

    @PostMapping(
            value = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> saveAppointment(
            @RequestBody Appointment appointment) {

        String message =
                appointmentService
                        .saveAppointment(appointment);


        if (message.equals(
                "Appointment is saved Successfully")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.CREATED
            );
        }


        if (message.equals(
                "Appointment date cannot be in the past")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.BAD_REQUEST
            );
        }


        if (message.equals(
                "Doctor is already booked on that date")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.CONFLICT
            );
        }


        if (message.equals("Patient not found")
                || message.equals("Doctor not found")) {

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
    // GET ALL APPOINTMENTS
    // =========================================================

    @GetMapping("/all")
    public ResponseEntity<?> getAllAppointments() {

        return ResponseEntity.ok(
                appointmentService.findAll()
        );
    }


    // =========================================================
    // GET APPOINTMENT BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(
            @PathVariable Long id) {

        Optional<Appointment> appointment =
                appointmentService.findById(id);


        if (appointment.isEmpty()) {

            return new ResponseEntity<>(
                    "Appointment not found",
                    HttpStatus.NOT_FOUND
            );
        }


        return ResponseEntity.ok(
                appointment.get()
        );
    }


    // =========================================================
    // GENERAL UPDATE
    //
    // IMPORTANT:
    // Quiz 2 status changes are NOT done here.
    // Status changes only through the B2 endpoint.
    // =========================================================

    @PutMapping(
            value = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateAppointment(
            @PathVariable Long id,
            @RequestBody Appointment appointment) {

        String message =
                appointmentService.updateAppointment(
                        id,
                        appointment
                );


        if (message.equals(
                "Appointment is updated Successfully")) {

            return ResponseEntity.ok(
                    message
            );
        }


        if (message.equals("Appointment not found")
                || message.equals("Patient not found")
                || message.equals("Doctor not found")) {

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
    // DELETE APPOINTMENT
    // QUIZ 2 - C4 PRESCRIPTION PROTECTION
    // =========================================================

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAppointment(
            @PathVariable Long id) {

        String message =
                appointmentService
                        .deleteAppointment(id);


        if (message.equals(
                "Appointment is deleted Successfully")) {

            return ResponseEntity.ok(
                    message
            );
        }


        if (message.equals(
                "That appointment has prescriptions, delete them first")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.CONFLICT
            );
        }


        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }


    // =========================================================
    // QUIZ 1 - A2
    // APPOINTMENTS BY STATUS
    //
    // GET /api/appointments/by-status?status=SCHEDULED
    // =========================================================

    @GetMapping("/by-status")
    public ResponseEntity<?> byStatus(
            @RequestParam AppointmentStatus status) {

        return ResponseEntity.ok(
                appointmentService.findByStatus(
                        status
                )
        );
    }


    // =========================================================
    // QUIZ 1 - A3
    // APPOINTMENTS BETWEEN TWO DATES
    //
    // GET /api/appointments/between
    // ?start=2026-10-01&end=2026-10-31
    // =========================================================

    @GetMapping("/between")
    public ResponseEntity<?> between(
            @RequestParam String start,
            @RequestParam String end) {

        LocalDate startDate =
                LocalDate.parse(start);

        LocalDate endDate =
                LocalDate.parse(end);


        return ResponseEntity.ok(
                appointmentService.findBetween(
                        startDate,
                        endDate
                )
        );
    }


    // =========================================================
    // QUIZ 1 - C1
    // APPOINTMENTS PER STATUS
    //
    // GET /api/appointments/stats/by-status
    // =========================================================

    @GetMapping("/stats/by-status")
    public ResponseEntity<?> statsByStatus() {

        return ResponseEntity.ok(
                appointmentService.statsByStatus()
        );
    }


    // =========================================================
    // QUIZ 1 - C4
    // CANCEL DOCTOR'S APPOINTMENTS FOR A DAY
    //
    // PATCH /api/appointments/cancel-day
    // ?doctorId=1&date=2026-10-20
    // =========================================================

    @PatchMapping("/cancel-day")
    public ResponseEntity<?> cancelDay(
            @RequestParam Long doctorId,
            @RequestParam String date) {

        LocalDate localDate =
                LocalDate.parse(date);


        return ResponseEntity.ok(
                appointmentService.cancelDay(
                        doctorId,
                        localDate
                )
        );
    }


    // =========================================================
    // QUIZ 2 - B2
    // APPOINTMENT STATUS FLOW
    //
    // PATCH /api/appointments/{id}/status
    // ?status=CONFIRMED
    // =========================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {

        String message =
                appointmentService.changeStatus(
                        id,
                        status
                );


        if (message.equals(
                "Appointment status updated successfully")) {

            return ResponseEntity.ok(
                    message
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
    // QUIZ 2 - B3
    // RESCHEDULE APPOINTMENT
    //
    // PATCH /api/appointments/{id}/reschedule
    // ?newDate=2026-10-30
    // =========================================================

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<?> reschedule(
            @PathVariable Long id,
            @RequestParam String newDate) {

        LocalDate date =
                LocalDate.parse(newDate);


        String message =
                appointmentService
                        .rescheduleAppointment(
                                id,
                                date
                        );


        if (message.equals(
                "Appointment rescheduled successfully")) {

            return ResponseEntity.ok(
                    message
            );
        }


        if (message.equals(
                "The appointment with that id does not exist")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.NOT_FOUND
            );
        }


        if (message.equals(
                "Doctor is already booked on that date")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.CONFLICT
            );
        }


        return new ResponseEntity<>(
                message,
                HttpStatus.BAD_REQUEST
        );
    }


    // =========================================================
    // QUIZ 1 BONUS
    // PAGINATION
    //
    // GET /api/appointments/page
    // ?page=0&size=5&sort=appointmentDate,desc
    // =========================================================

    @GetMapping("/page")
    public ResponseEntity<?> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(
                    defaultValue = "appointmentDate,asc")
            String sort) {

        String[] parts =
                sort.split(",");

        String property =
                parts[0];


        Sort.Direction direction =
                parts.length > 1
                        && parts[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;


        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                direction,
                                property
                        )
                );


        return ResponseEntity.ok(
                appointmentService.findPage(
                        pageable
                )
        );
    }


    // =========================================================
    // QUIZ 1 BONUS
    // DELETE OLD CANCELLED APPOINTMENTS
    //
    // DELETE /api/appointments/cancelled-before
    // ?date=2026-10-15
    // =========================================================

    @DeleteMapping("/cancelled-before")
    public ResponseEntity<?> cancelledBefore(
            @RequestParam String date) {

        LocalDate localDate =
                LocalDate.parse(date);


        return ResponseEntity.ok(
                appointmentService
                        .deleteCancelledBefore(
                                localDate
                        )
        );
    }
}
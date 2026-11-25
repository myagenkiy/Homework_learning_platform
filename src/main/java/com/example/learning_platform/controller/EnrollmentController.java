package com.example.learning_platform.controller;

import com.example.learning_platform.Enrollment;
import com.example.learning_platform.service.EnrollmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollRequest request) {
        Enrollment enrollment = enrollmentService.enroll(request.userId(), request.courseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(enrollment));
    }

    @DeleteMapping
    public ResponseEntity<Void> unenroll(@RequestParam Long userId, @RequestParam Long courseId) {
        enrollmentService.unenroll(userId, courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{userId}")
    public List<EnrollmentResponse> getByStudent(@PathVariable Long userId) {
        return enrollmentService.getEnrollmentsOfStudent(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/course/{courseId}")
    public List<EnrollmentResponse> getByCourse(@PathVariable Long courseId) {
        return enrollmentService.getEnrollmentsOfCourse(courseId).stream()
                .map(this::toResponse)
                .toList();
    }

    private EnrollmentResponse toResponse(Enrollment e) {
        return new EnrollmentResponse(e.getId(), e.getUser().getId(), e.getCourse().getId(), e.getStatus().name());
    }

    public record EnrollRequest(@NotNull Long userId, @NotNull Long courseId) {}
    public record EnrollmentResponse(Long id, Long userId, Long courseId, String status) {}
}
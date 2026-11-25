package com.example.learning_platform.controller;

import com.example.learning_platform.Submission;
import com.example.learning_platform.service.SubmissionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<SubmissionResponse> submit(@Valid @RequestBody SubmitRequest request) {
        Submission submission = submissionService.submit(
                request.studentId(), request.assignmentId(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(submission));
    }

    @PutMapping("/{id}/grade")
    public SubmissionResponse grade(@PathVariable Long id, @RequestBody GradeRequest request) {
        Submission submission = submissionService.grade(id, request.score(), request.feedback());
        return toResponse(submission);
    }

    @GetMapping("/student/{studentId}")
    public List<SubmissionResponse> getByStudent(@PathVariable Long studentId) {
        return submissionService.getSubmissionsOfStudent(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/assignment/{assignmentId}")
    public List<SubmissionResponse> getByAssignment(@PathVariable Long assignmentId) {
        return submissionService.getSubmissionsOfAssignment(assignmentId).stream()
                .map(this::toResponse)
                .toList();
    }

    private SubmissionResponse toResponse(Submission s) {
        return new SubmissionResponse(
                s.getId(),
                s.getStudent().getId(),
                s.getAssignment().getId(),
                s.getContent(),
                s.getScore(),
                s.getFeedback(),
                s.getSubmittedAt()
        );
    }

    public record SubmitRequest(
            @NotNull Long studentId,
            @NotNull Long assignmentId,
            @NotBlank String content
    ) {}

    public record GradeRequest(Integer score, String feedback) {}
    public record SubmissionResponse(Long id, Long studentId, Long assignmentId,
                                     String content, Integer score, String feedback,
                                     LocalDateTime submittedAt) {}
}
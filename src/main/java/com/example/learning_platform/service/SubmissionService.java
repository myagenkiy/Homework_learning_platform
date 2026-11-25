package com.example.learning_platform.service;

import com.example.learning_platform.Assignment;
import com.example.learning_platform.AssignmentRepository;
import com.example.learning_platform.Submission;
import com.example.learning_platform.SubmissionRepository;
import com.example.learning_platform.User;
import com.example.learning_platform.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public SubmissionService(SubmissionRepository submissionRepository,
                             AssignmentRepository assignmentRepository,
                             UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Submission submit(Long studentId, Long assignmentId, String content) {
        if (submissionRepository.existsByStudentIdAndAssignmentId(studentId, assignmentId)) {
            throw new IllegalStateException("Студент уже сдал это задание");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден: " + studentId));
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Задание не найдено: " + assignmentId));

        Submission submission = new Submission(content, student, assignment);
        return submissionRepository.save(submission);
    }

    @Transactional
    public Submission grade(Long submissionId, Integer score, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Решение не найдено: " + submissionId));
        submission.setScore(score);
        submission.setFeedback(feedback);
        return submissionRepository.save(submission);
    }

    @Transactional(readOnly = true)
    public List<Submission> getSubmissionsOfStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Submission> getSubmissionsOfAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }
}
package com.example.learning_platform;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "submissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "assignment_id"})
)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime submittedAt;
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    public Submission() {
    }

    public Submission(String content, User student, Assignment assignment) {
        this.content = content;
        this.student = student;
        this.assignment = assignment;
        this.submittedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
}

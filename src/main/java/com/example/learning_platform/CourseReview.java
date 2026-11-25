package com.example.learning_platform;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_reviews")
public class CourseReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    public CourseReview() {
    }

    public CourseReview(Integer rating, String comment, Course course, User student) {
        this.rating = rating;
        this.comment = comment;
        this.course = course;
        this.student = student;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
}
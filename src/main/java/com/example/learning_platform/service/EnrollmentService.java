package com.example.learning_platform.service;

import com.example.learning_platform.Course;
import com.example.learning_platform.CourseRepository;
import com.example.learning_platform.Enrollment;
import com.example.learning_platform.EnrollmentRepository;
import com.example.learning_platform.User;
import com.example.learning_platform.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Enrollment enroll(Long userId, Long courseId) {
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new IllegalStateException("Студент уже записан на этот курс");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден: " + courseId));

        Enrollment enrollment = new Enrollment(user, course);
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void unenroll(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Запись на курс не найдена"));
        enrollmentRepository.delete(enrollment);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentsOfStudent(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentsOfCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }
}
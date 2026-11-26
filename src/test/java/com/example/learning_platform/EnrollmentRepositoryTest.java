package com.example.learning_platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class EnrollmentRepositoryTest {

    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;

    @Test
    void shouldNotAllowDuplicateEnrollment() {
        User user = userRepository.save(new User("Студент", "student@example.com"));
        Course course = courseRepository.save(new Course("Java", "Курс"));

        enrollmentRepository.saveAndFlush(new Enrollment(user, course));

        assertThatThrownBy(() ->
                enrollmentRepository.saveAndFlush(new Enrollment(user, course)))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
}
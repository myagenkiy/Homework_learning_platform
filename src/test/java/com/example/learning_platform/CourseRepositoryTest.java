package com.example.learning_platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired private CourseRepository courseRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void shouldSaveCourseWithCategoryAndTeacher() {
        Category category = categoryRepository.save(new Category("Программирование"));
        User teacher = userRepository.save(new User("Преподаватель", "teacher@example.com"));

        Course course = new Course("Java", "Курс");
        course.setCategory(category);
        course.setTeacher(teacher);
        Course saved = courseRepository.save(course);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCategory().getName()).isEqualTo("Программирование");
        assertThat(saved.getTeacher().getEmail()).isEqualTo("teacher@example.com");
    }
}
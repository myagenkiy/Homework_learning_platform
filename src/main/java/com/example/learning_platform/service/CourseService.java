package com.example.learning_platform.service;

import com.example.learning_platform.Category;
import com.example.learning_platform.CategoryRepository;
import com.example.learning_platform.Course;
import com.example.learning_platform.CourseRepository;
import com.example.learning_platform.Module;
import com.example.learning_platform.ModuleRepository;
import com.example.learning_platform.User;
import com.example.learning_platform.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    public CourseService(CourseRepository courseRepository,
                         CategoryRepository categoryRepository,
                         UserRepository userRepository,
                         ModuleRepository moduleRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional
    public Course createCourse(String title, String description, Long categoryId, Long teacherId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Категория не найдена: " + categoryId));
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Преподаватель не найден: " + teacherId));

        Course course = new Course(title, description);
        course.setCategory(category);
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public List<Module> getModulesOfCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден: " + courseId));
        return course.getModules();
    }

    @Transactional
    public Module addModule(Long courseId, String title, Integer orderIndex) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден: " + courseId));
        Module module = new Module(title, orderIndex);
        module.setCourse(course);
        return moduleRepository.save(module);
    }
}
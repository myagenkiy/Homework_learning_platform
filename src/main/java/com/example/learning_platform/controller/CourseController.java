package com.example.learning_platform.controller;

import com.example.learning_platform.Course;
import com.example.learning_platform.service.CourseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        Course course = courseService.createCourse(
                request.title(),
                request.description(),
                request.categoryId(),
                request.teacherId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @GetMapping("/{id}/modules")
    public List<CourseService.ModuleDto> getModules(@PathVariable Long id) {
        return courseService.getModulesOfCourse(id);
    }

    @PostMapping("/{id}/modules")
    public ResponseEntity<CourseService.ModuleDto> addModule(@PathVariable Long id,
                                                             @RequestBody CreateModuleRequest request) {
        var module = courseService.addModule(id, request.title(), request.orderIndex());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CourseService.ModuleDto(module.getId(), module.getTitle(), module.getOrderIndex()));
    }

    public record CreateCourseRequest(
            @NotBlank String title,
            String description,
            @NotNull Long categoryId,
            @NotNull Long teacherId
    ) {}

    public record CreateModuleRequest(String title, Integer orderIndex) {}
}
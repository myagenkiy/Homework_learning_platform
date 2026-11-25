package com.example.learning_platform;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCourseIdOrderByOrderIndex(Long courseId);
}

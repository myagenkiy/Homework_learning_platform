package com.example.learning_platform.config;

import com.example.learning_platform.*;
import com.example.learning_platform.Module;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           CourseRepository courseRepository,
                           ModuleRepository moduleRepository,
                           LessonRepository lessonRepository,
                           AssignmentRepository assignmentRepository,
                           QuizRepository quizRepository,
                           QuestionRepository questionRepository,
                           AnswerOptionRepository answerOptionRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.assignmentRepository = assignmentRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // данные уже есть — не дублируем
        }

        // 1. Пользователи
        User teacher = new User("Преподаватель Петров", "teacher@example.com");
        User student = new User("Студент Иванов", "student@example.com");
        userRepository.save(teacher);
        userRepository.save(student);

        // 2. Категория
        Category category = new Category("Программирование");
        categoryRepository.save(category);

        // 3. Курс
        Course course = new Course("Java с нуля", "Базовый курс по Java");
        course.setCategory(category);
        course.setTeacher(teacher);
        courseRepository.save(course);

        // 4. Модуль
        Module module = new Module("Введение", 1);
        module.setCourse(course);
        moduleRepository.save(module);

        // 5. Урок
        Lesson lesson = new Lesson("Что такое Java?", "Java — язык программирования...");
        lesson.setModule(module);
        lessonRepository.save(lesson);

        // 6. Задание
        Assignment assignment = new Assignment("ДЗ 1", "Напишите Hello World", 100);
        assignment.setLesson(lesson);
        assignmentRepository.save(assignment);

        // 7. Тест
        Quiz quiz = new Quiz("Тест по модулю 1", 30);
        quiz.setModule(module);
        quizRepository.save(quiz);

        // 8. Вопрос
        Question question = new Question("Что такое JPA?", QuestionType.SINGLE_CHOICE);
        question.setQuiz(quiz);
        questionRepository.save(question);

        // 9. Варианты ответа
        AnswerOption correct = new AnswerOption("Спецификация Java EE", true);
        correct.setQuestion(question);
        answerOptionRepository.save(correct);

        AnswerOption wrong = new AnswerOption("База данных", false);
        wrong.setQuestion(question);
        answerOptionRepository.save(wrong);

        System.out.println(">>> Демо-данные созданы");
    }
}

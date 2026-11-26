Learning Platform
Учебная платформа на Spring Boot: курсы, модули, уроки, задания, тесты, запись студентов, отзывы и теги.

Стек
Java 17, Spring Boot 4.2.0 (Spring Data JPA, Spring Web MVC, Validation), Hibernate ORM 7.4, PostgreSQL, Maven.
Тесты: JUnit 6, Mockito, H2 (in-memory), @DataJpaTest, @SpringBootTest.

Запуск
1. PostgreSQL. Создайте пустую базу данных:


    CREATE DATABASE learning_platform;


2. Конфигурация. В src/main/resources/application.properties укажите свои параметры:


    spring.datasource.url=jdbc:postgresql://localhost:5432/learning_platform 

    spring.datasource.username=postgres

    spring.datasource.password=ВАШ_ПАРОЛЬ

    spring.jpa.hibernate.ddl-auto=update

    spring.jpa.generate-ddl=true 

    spring.jpa.show-sql=true

    spring.jpa.open-in-view=false


3. Запуск:


    mvn spring-boot:run


Приложение поднимется на http://localhost:8080.

При первом запуске на пустой базе DataInitializer создаст демо-данные: преподавателя, студента, категорию, курс, модуль, урок, задание, тест с вопросом и вариантами ответов. Если база уже заполнена — инициализатор пропускается.

Модель данных
16 сущностей, все типы связей:

1-1: User ↔ Profile, Module ↔ Quiz

1-M: Category → Course, User → Course (teacher), Course → Module, Module → Lesson, Lesson → Assignment, Assignment → Submission, Quiz → Question, Question → AnswerOption, Course → CourseReview

M-M: User ↔ Course через Enrollment (явная сущность с уникальностью user_id + course_id), Course ↔ Tag через @JoinTable(name = "course_tag")

Все коллекции — FetchType.LAZY.

REST API
Курсы:

    POST /api/courses — создать курс

    GET /api/courses/{id}/modules — модули курса

    POST /api/courses/{id}/modules — добавить модуль

Запись на курс:

    POST /api/enrollments — записать студента на курс

    DELETE /api/enrollments?userId=&courseId= — отписаться

    GET /api/enrollments/student/{userId} — курсы студента

    GET /api/enrollments/course/{courseId} — студенты курса

Задания:

    POST /api/submissions — сдать задание

    PUT /api/submissions/{id}/grade — оценить решение

    GET /api/submissions/student/{studentId} — решения студента

    GET /api/submissions/assignment/{assignmentId} — решения по заданию

Тесты:

    POST /api/quizzes — создать тест

    POST /api/quizzes/{quizId}/questions — добавить вопрос

    POST /api/quizzes/questions/{questionId}/options — добавить вариант ответа

    POST /api/quizzes/{quizId}/submit — пройти тест

    GET /api/quizzes/{quizId}/results — результаты теста

Коды ответов:

200 OK — чтение, обновление

201 Created — создание

204 No Content — удаление

400 Bad Request — ошибка валидации (@Valid)

404 Not Found — сущность не найдена

409 Conflict — нарушение уникальности (повторная запись, повторная сдача)

Ошибки обрабатываются централизованно в GlobalExceptionHandler (@RestControllerAdvice).

Lazy Loading
Все коллекции настроены на FetchType.LAZY. Это значит, что при загрузке Course коллекция modules не загружается сразу.

Воспроизведение проблемы: если обратиться к course.getModules() вне транзакции (например, в контроллере после возврата из сервиса), возникает:

org.hibernate.LazyInitializationException: Cannot lazily initialize collection of role 'com.example.learning_platform.Course.modules' with key '1' (no session)

В логе видно, что Hibernate загрузил только courses, а к modules не обратился — коллекция осталась неинициализированной.

Способы решения:

@Transactional — методы сервисов, которые обращаются к ленивым коллекциям, аннотированы @Transactional(readOnly = true). Внутри транзакции сессия открыта, и коллекция догружается.

DTO внутри транзакции — CourseService.getModulesOfCourse преобразует List<Module> в List<ModuleDto> внутри транзакции. Контроллер получает уже готовые объекты без JPA-прокси.

JOIN FETCH (опционально) — в CourseRepository можно добавить @Query("select c from Course c join fetch c.modules where c.id = :id") для загрузки коллекции одним запросом.

Тесты

    Запуск: mvn test

UserRepositoryTest — CRUD пользователя через @DataJpaTest (H2).

CourseRepositoryTest — сохранение курса с @ManyToOne (категория, преподаватель).

EnrollmentRepositoryTest — уникальность (user_id, course_id) на уровне БД.

CourseServiceTest — unit-тест с Mockito (бизнес-логика без БД).

Структура проекта

src/main/java/com/example/learning_platform/

config/ — DataInitializer (демо-данные)

controller/ — REST API + GlobalExceptionHandler

entity/ — 16 JPA-сущностей (в корне пакета)

repository/ — Spring Data JPA (в корне пакета)

service/ — CourseService, EnrollmentService, SubmissionService, QuizService

LearningPlatformApplication.java

src/main/resources/

application.properties

(application-test.properties в src/test/resources)

src/test/java/com/example/learning_platform/

UserRepositoryTest.java

CourseRepositoryTest.java

EnrollmentRepositoryTest.java

CourseServiceTest.java

Известные особенности
Spring Boot 4 перенёс @DataJpaTest в пакет org.springframework.boot.data.jpa.test.autoconfigure — импорт отличается от Boot 3.

spring.jpa.hibernate.ddl-auto=update в связке с generate-ddl=true создаёт новые таблицы при добавлении сущностей, но не пересоздаёт существующие. Для чистой схемы используйте create-drop (только для разработки).

При удалении сущностей с зависимостями через SQL (DBeaver) соблюдайте порядок: сначала «дети» (answer_options, quiz_submissions, questions), потом «родители» (quizzes). Через JPA каскад настроен на Module → Lesson и Quiz → Question.


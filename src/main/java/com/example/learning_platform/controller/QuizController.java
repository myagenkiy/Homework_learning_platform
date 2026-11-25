package com.example.learning_platform.controller;

import com.example.learning_platform.Question;
import com.example.learning_platform.QuestionType;
import com.example.learning_platform.QuizSubmission;
import com.example.learning_platform.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody CreateQuizRequest request) {
        var quiz = quizService.createQuiz(request.moduleId(), request.title(), request.timeLimit());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new QuizResponse(quiz.getId(), quiz.getTitle(), quiz.getTimeLimit()));
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<QuestionResponse> addQuestion(@PathVariable Long quizId,
                                                        @RequestBody AddQuestionRequest request) {
        Question question = quizService.addQuestion(quizId, request.text(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new QuestionResponse(question.getId(), question.getText(), question.getType().name()));
    }

    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<OptionResponse> addOption(@PathVariable Long questionId,
                                                    @RequestBody AddOptionRequest request) {
        var option = quizService.addAnswerOption(questionId, request.text(), request.isCorrect());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OptionResponse(option.getId(), option.getText(), option.getIsCorrect()));
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizResultResponse> submit(@PathVariable Long quizId,
                                                     @RequestBody SubmitQuizRequest request) {
        QuizSubmission submission = quizService.submitQuiz(quizId, request.studentId(), request.selectedOptionIds());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new QuizResultResponse(submission.getId(), submission.getScore(), submission.getTakenAt()));
    }

    @GetMapping("/{quizId}/results")
    public List<QuizResultResponse> getResults(@PathVariable Long quizId) {
        return quizService.getResultsOfQuiz(quizId).stream()
                .map(s -> new QuizResultResponse(s.getId(), s.getScore(), s.getTakenAt()))
                .toList();
    }

    public record CreateQuizRequest(Long moduleId, String title, Integer timeLimit) {}
    public record AddQuestionRequest(String text, QuestionType type) {}
    public record AddOptionRequest(String text, Boolean isCorrect) {}
    public record SubmitQuizRequest(Long studentId, List<Long> selectedOptionIds) {}
    public record QuizResponse(Long id, String title, Integer timeLimit) {}
    public record QuestionResponse(Long id, String text, String type) {}
    public record OptionResponse(Long id, String text, Boolean isCorrect) {}
    public record QuizResultResponse(Long id, Integer score, LocalDateTime takenAt) {}
}
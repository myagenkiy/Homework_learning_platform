package com.example.learning_platform.service;

import com.example.learning_platform.AnswerOption;
import com.example.learning_platform.AnswerOptionRepository;
import com.example.learning_platform.Module;
import com.example.learning_platform.ModuleRepository;
import com.example.learning_platform.Question;
import com.example.learning_platform.QuestionRepository;
import com.example.learning_platform.QuestionType;
import com.example.learning_platform.Quiz;
import com.example.learning_platform.QuizRepository;
import com.example.learning_platform.QuizSubmission;
import com.example.learning_platform.QuizSubmissionRepository;
import com.example.learning_platform.User;
import com.example.learning_platform.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository,
                       ModuleRepository moduleRepository,
                       QuestionRepository questionRepository,
                       AnswerOptionRepository answerOptionRepository,
                       QuizSubmissionRepository quizSubmissionRepository,
                       UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.moduleRepository = moduleRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Quiz createQuiz(Long moduleId, String title, Integer timeLimit) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Модуль не найден: " + moduleId));
        Quiz quiz = new Quiz(title, timeLimit);
        quiz.setModule(module);
        return quizRepository.save(quiz);
    }

    @Transactional
    public Question addQuestion(Long quizId, String text, QuestionType type) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Тест не найден: " + quizId));
        Question question = new Question(text, type);
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    @Transactional
    public AnswerOption addAnswerOption(Long questionId, String text, Boolean isCorrect) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Вопрос не найден: " + questionId));
        AnswerOption option = new AnswerOption(text, isCorrect);
        option.setQuestion(question);
        return answerOptionRepository.save(option);
    }

    @Transactional
    public QuizSubmission submitQuiz(Long quizId, Long studentId, List<Long> selectedOptionIds) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Тест не найден: " + quizId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден: " + studentId));

        List<Question> questions = questionRepository.findByQuizId(quizId);
        int correctCount = 0;

        for (Question question : questions) {
            List<AnswerOption> options = answerOptionRepository.findByQuestionId(question.getId());
            boolean questionCorrect = true;
            for (AnswerOption option : options) {
                boolean selected = selectedOptionIds.contains(option.getId());
                if (option.getIsCorrect() != selected) {
                    questionCorrect = false;
                    break;
                }
            }
            if (questionCorrect && !options.isEmpty()) {
                correctCount++;
            }
        }

        int score = questions.isEmpty() ? 0 : (correctCount * 100) / questions.size();

        QuizSubmission submission = new QuizSubmission(quiz, student);
        submission.setScore(score);
        return quizSubmissionRepository.save(submission);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmission> getResultsOfStudent(Long studentId) {
        return quizSubmissionRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmission> getResultsOfQuiz(Long quizId) {
        return quizSubmissionRepository.findByQuizId(quizId);
    }
}
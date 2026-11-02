package br.com.fiap.feedback.api;

public class InvalidEvaluationException extends RuntimeException {
    public InvalidEvaluationException(String message) {
        super(message);
    }
}

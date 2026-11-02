package br.com.fiap.feedback.shared.domain;

public final class UrgencyClassifier {
    private UrgencyClassifier() {
    }

    public static Urgency classify(int grade) {
        if (grade < 0 || grade > 10) throw new IllegalArgumentException("Grade must be between 0 and 10");
        return grade <= 3 ? Urgency.CRITICAL : grade <= 6 ? Urgency.MEDIUM : Urgency.LOW;
    }
}

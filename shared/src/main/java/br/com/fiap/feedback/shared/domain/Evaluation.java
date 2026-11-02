package br.com.fiap.feedback.shared.domain;

import java.time.Instant;

public record Evaluation(String id, String description, int grade, Urgency urgency, Instant createdAt,
                         String reportWeek) {
}

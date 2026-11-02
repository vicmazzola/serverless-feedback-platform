package br.com.fiap.feedback.shared.repository;

import br.com.fiap.feedback.shared.domain.Evaluation;

import java.util.List;

public interface EvaluationRepository {
    void save(Evaluation evaluation);

    List<Evaluation> findByReportWeek(String reportWeek);
}
